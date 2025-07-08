import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

const backendUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

class WebSocketService{
    constructor(){
        // WebSocket client ref
        this.client = null;
        // Map to manage the subscriptions
        this.subscriptions = new Map();
    }

    // Method to initialize the websocket connection
    connect(onConnectCallback){
        // If there is already a connecion return
        if(this.client) return;

        this.client = new Client({
            // Uses SockJS as te websocket factory
            webSocketFactory: () => new SockJS(`${backendUrl}/ws`),
            // Sets reconnection attempts after 5 seconds
            reconnectDelay: 5000,
            // Debug log
            // debug: (str) => console.log(str),
        });

        // Logs message on successfull connection
        this.client.onConnect = () => {
            console.log("Connected to Websocket");
            // Callback function on a successfull connection
            if(onConnectCallback) onConnectCallback();
        };

        // Handles stomp errors
        this.client.onStompError = (frame) => {
            console.error("Broker reported error: "+ frame.headers["message"]);
            console.error("Additional details: "+ frame.body);
        }

        // Initiates the connetion
        this.client.activate();
    }

    // Method to terminate the websocket connection
    disconnect(onDisconnectCallback){
        if(this.client){
            if (onDisconnectCallback) onDisconnectCallback();
            this.client.deactivate();
            this.client = null;
            this.subscriptions.clear();
        }
    }

    // Method to emmit a message to an specific topic
    publish(destination, body){
        if(this.client && this.client.connected){
            this.client.publish({destination, body: JSON.stringify(body)});
        } else{
            console.error("Websocket is not connected");
        }
    }

    // Method to listen to an specific topic
    subscribe(topic, callback){
        if(this.client && this.client.connected){
            const subscription = this.client.subscribe(topic,(messageOutput) =>{
                const data = JSON.parse(messageOutput.body);
                callback(data);
            });
            this.subscriptions.set(topic, subscription);
        } else{
            console.error("Websocket is not conneted");
        }
    }

    // Method to stop listening on an specific topic
    unsubscribe(topic){
        if(this.subscriptions.has(topic)){
            this.subscriptions.get(topic).unsubscribe();
            this.subscriptions.delete(topic);
        }
    }

}

export default new WebSocketService();