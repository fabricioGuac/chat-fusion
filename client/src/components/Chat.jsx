import ChatHeader from "./ChatHeader";
import ChatBody from "./ChatBody";
import ChatInput from "./ChatInput";
import { useSelector } from "react-redux";
import { useState, useEffect, useRef } from "react";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

// Chat component combining the header, body and input sections
export default function Chat({ chatId, setSelectedView }) {

    // WebSocket client ref
        const stompClient = useRef(null);
    // State variable to manage the last user to connect to the chat
    const [lastConnectedUser, setLastconnectedUser] = useState(null);



    // Retrieves the current user data and current chat data from the Redux store
    const currentUser = useSelector((state) => state.user.user);
    const chat = useSelector((state) => state.chats.chats.find((chat) => chat.id === chatId));

    // Function to connect to the websocket
    const connectWebSocket = () => {
        stompClient.current = new Client({
            // Uses SockJS as te websocket factory
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
            // Sets the reconnection attemptafter 5 seconds
            reconnectDelay: 5000,
            // Debug log
            debug: (str) => console.log(str),
        });

        // Sets up subscriptions on successful connection
        stompClient.current.onConnect = () => {
            console.log("Connected to websocket");
            
            stompClient.current.publish({
                destination: `/app/connected/${chat.id}`,
                body: JSON.stringify({ userId: currentUser.id, online: true }),
            });


            // Subscribes to the chat topic
            stompClient.current.subscribe(`/chat/${chat.id}/connected`, (messageOutput) => {
                // Parses the message event
                const event = JSON.parse(messageOutput.body);
                if(event.online) { 
                    // Set the lastConnectedUser to the user with the id from the event
                    setLastconnectedUser(chat.members.find((member) => member.id === event.userId));
                } else {
                    setLastconnectedUser(null);
                }
            });
        }

        // Handles stomp errors
        stompClient.current.onStompError = (frame) => {
            console.error("Broker reported error: " + frame.headers["message"]);
            console.error("Additinal details: " + frame.body);
        }

        // Initiates the connetion
        stompClient.current.activate();
    }

    // Fucntion to disconnect from the websocket
    const disconnectWebSocket = () => {
        if (stompClient.current) {
            stompClient.current.unsubscribe(`/chat/${chat.id}/connected`);
            stompClient.current.publish({
                destination: `/app/connected/${chat.id}`,
                body: JSON.stringify({ userId: currentUser.id, online: false }),
            });
            stompClient.current.deactivate();
        }
    };

    // UseEffect to handle connection and disconection from the websocket on chat change
    useEffect( () => {
        
        // If the chat is not found go back to basic view
        if (!chat) {
            setSelectedView({ type: null, data: null });
            return;
        }

        // Connects to the websocket
        connectWebSocket();

        // Cleanup function to disconnect from the websocket
        return () => {
            disconnectWebSocket();
        };

    }, [chatId, chat])

        // Renders a loading indicator if the user data is not yet available
        if (!currentUser || !chat) {
            return <div>Loading...</div>;
        }


    return (
        <div className="flex flex-col h-full">
            {/* Chat header with chat details and user data */}
            <ChatHeader chat={chat} currentUser={currentUser} />
            <div className="flex-1 overflow-y-auto">
                {/* Chat boy where the messages are displayed */}
                <ChatBody chat={chat} currentUser={currentUser} lastConnectedUser={lastConnectedUser} />
            </div>
            {/* Input field for sending new messages */}
            <ChatInput chatId={chat.id} />
        </div>
    );
}