import { useEffect, useState} from "react"
import { useNavigate } from "react-router-dom";
import auth from "../utils/auth";
import SideBar from "../components/SideBar";
import UpdateDetails from "../components/UpdateDetails";
import Chat from "../components/Chat";
import CreateGroup from "../components/CreateGroup";
import CreateSingleChat from "../components/CreateSingleChat";

import ws from "../utils/ws";


export default function Dashboard() {
    // State to manage the selected view
    const [selectedView, setSelectedView] = useState({ type: null, data: null });
    const navigate = useNavigate();

    // Callback function for the beforeUnload event listener
    const handleBeforeUnload = () => {
        ws.publish("/app/user.online.status", {
            email: auth.getEmail(),
            online: false,
        });
        ws.disconnect();
    };

    // Redirects the user to the login page if not logged in and connects to the websocket 
    useEffect(() => {
        if (!auth.loggedIn()) {
            navigate('/login');
            return;
        }

        // Initiates the websocket connection
        ws.connect(()=> {
            ws.publish("/app/user.online.status", {
                email: auth.getEmail(),
                online: true,
            })
        });

        // Adds an event listener to ensure disconnection on window close
        window.addEventListener("beforeunload", handleBeforeUnload);

        // Cleanup on unmount
        return () => {
            console.log("CLEANUP");
            
            // Publishes offline status then disconnect
            ws.publish("/app/user.online.status", {
                email: auth.getEmail(),
                online: false,
            });
            // Closes the webscoket connection
            ws.disconnect();
            window.removeEventListener("beforeunload", handleBeforeUnload);
            
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
