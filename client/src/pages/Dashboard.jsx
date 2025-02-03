import { useEffect, useState, useRef } from "react"
import { useNavigate } from "react-router-dom";
import auth from "../utils/auth";
import SideBar from "../components/SideBar";
import UpdateDetails from "../components/UpdateDetails";
import Chat from "../components/Chat";
import CreateGroup from "../components/CreateGroup";
import CreateSingleChat from "../components/CreateSingleChat";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";


export default function Dashboard() {
    // State to manage the selected view
    const [selectedView, setSelectedView] = useState({ type: null, data: null });
    const navigate = useNavigate();

    // WebSocket client ref
    const stompClient = useRef(null);

    const connectWebSocket = () => {
        stompClient.current = new Client({
            // Uses SockJS as te websocket factory
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
            // Sets the reconnection attemptafter 5 seconds
            reconnectDelay: 5000,
            // Debug log
            debug: (str) => console.log(str),
        });

        // Emmits the event to notify that the user is online
        stompClient.current.onConnect = () => {
            console.log("Connected to websocket");

            console.log(auth.getEmail())
            stompClient.current.publish({
                destination:"/app/user.online.status",
                body: JSON.stringify({email: auth.getEmail(), online: true}),
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

    const disconnectWebSocket = () => {
        if (stompClient.current) {
            stompClient.current.publish({
                destination: "/app/user.online.status",
                body: JSON.stringify({email: auth.getEmail(), online: false}),
            });
            stompClient.current.deactivate();
        }
    };

    // Redirects the user to the login page if not logged in and connects to the websocket 
    useEffect(() => {
        if (!auth.loggedIn()) {
            navigate('/login');
            return;
        }

        // Initiates the websocket connection
        connectWebSocket();

        // Adds an event listener to ensure disconnection on window close
        window.addEventListener("beforeunload", disconnectWebSocket);

        // Cleanup on unmount
        return () => {
            console.log("CLEANUP");
            disconnectWebSocket();
            window.removeEventListener("beforeunload",  disconnectWebSocket);
        }
    }, []);


    // Dynamically render the content based on `selectedView`
    const renderContent = () => {
        switch (selectedView.type) {
            case "chat":
                return <Chat chatId={selectedView.data.chatId} setSelectedView={setSelectedView} />;
            case "single-create":
                return <CreateSingleChat setSelectedView={setSelectedView} />;
            case "group-create":
                return <CreateGroup setSelectedView={setSelectedView} />;
            case "update-details":
                return <UpdateDetails data={selectedView.data} />;
            default:
                // Default message when no view is seleted
                return <h2 className="flex justify-center items-center h-screen text-5xl">Select a chat or create a group!</h2>;
        }
    };


    return (
        <div className="flex h-screen">
            {/* Side bar to control the rendered view */}
            <SideBar onSelectView={setSelectedView} />
            {/* Dynamically rendered main content */}
            <main className="flex-1 overflow-y-auto bg-white">
                {renderContent()}
            </main>
        </div>
    )
}
