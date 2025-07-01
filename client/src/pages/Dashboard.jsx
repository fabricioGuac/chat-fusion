import { useEffect, useState, useRef } from "react"
import { useNavigate } from "react-router-dom";
import auth from "../utils/auth";
import SideBar from "../components/SideBar";
import UpdateDetails from "../components/UpdateDetails";
import Chat from "../components/Chat";
import CreateGroup from "../components/CreateGroup";
import CreateSingleChat from "../components/CreateSingleChat";
import IncomingCallModal from "../components/IncomingCallModal";

import { useSelector, useDispatch } from "react-redux";
import { setCall } from '../redux/callSlice';


import ws from "../utils/ws";
import CallRoomModal from "../components/CallRoomModal";


export default function Dashboard() {
    // Dispatch used to send actions to the Redux store
    const dispatch = useDispatch();

    // State variable to track call related status
    const [incomingCall, setIncomingCall] = useState(null); // Stores data about an incoming call (before it's accepted)
    // State variable to track the side bar status on smaller screens
    const [sidebarOpen, setSidebarOpen] = useState(false);

    // useRefs for tracking current user and active call chatId
    // These are updated via useEffect to ensure access during unload
    const activeCallIdRef = useRef(null);
    const activeCallId = useSelector((state) => state.call.activeCallId);
    useEffect(() => {
        activeCallIdRef.current = activeCallId;
    }, [activeCallId]);
    const currentUser = useSelector((state) => state.user.user);
    const userRef = useRef(null);
    useEffect(() => {
        if (currentUser) {
            userRef.current = currentUser;
        }
    }, [currentUser]);

    // State to manage the selected view
    const [selectedView, setSelectedView] = useState({ type: null, data: null });
    const navigate = useNavigate();

    // Handles cleanup on window/tab close
    const handleBeforeUnload = () => {
        // Notifies other users about your online status
        ws.publish("/app/user.online.status", {
            email: auth.getEmail(),
            online: false,
        });
        // Gets the values from the useRefs for the current user and the current active call chat id
        const user = userRef.current;
        const callId = activeCallIdRef.current;
        // If both user and callId are available, notify the call room that you're leaving
        if (callId && user) {
            ws.publish(`/app/call-room/${callId}`, {
                type: "leave",
                user: {
                    id: user.id,
                    username: user.username,
                },
            });
        }
        // Closes the web socket connection
        ws.disconnect();
    };

    // Redirects the user to the login page if not logged in and connects to the websocket 
    useEffect(() => {
        if (!auth.loggedIn()) {
            navigate('/login');
            return;
        }

        // Initiates the websocket connection
        ws.connect(() => {
            ws.publish("/app/user.online.status", {
                email: auth.getEmail(),
                online: true,
            })

            // Listens for call events of the current user
            const topic = `/topic/call/${auth.getEmail()}`;
            ws.subscribe(topic, (incoming) => {
                console.log("Incoming call from", incoming.displayName);
                // If  not already in a call, update the incoming call state to open the incoming call modal
                if (!activeCallIdRef.current) {
                    setIncomingCall({
                        displayName: incoming.displayName,
                        chatId: incoming.chatId,
                    });
                }
            });
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
                return <Chat chat={selectedView.data.chat} setSelectedView={setSelectedView} />;
            case "single-create":
                return <CreateSingleChat setSelectedView={setSelectedView} />;
            case "group-create":
                return <CreateGroup setSelectedView={setSelectedView} />;
            case "update-details":
                return <UpdateDetails data={selectedView.data} />;
            default:
                // Default message when no view is seleted
                return (
                    <div className="flex items-center justify-center h-full w-full">
                        <h2 className="text-3xl sm:text-5xl text-center px-4">
                            Select a chat or create a group!
                        </h2>
                    </div>
                )
        }
    };

    // Function to handle accepting incoming calls
    const handleAccept = () => {
        // Sets the incoming call id as the active call in the redux state
        dispatch(setCall(incomingCall.chatId));
        // Closes the incoming call modal
        setIncomingCall(null);
    }

    // Function to handle declining incoming calls
    const handleDecline = () => {
        console.log("Call declined");
        // Clears the incoming call state to close the modal
        setIncomingCall(null);
    }


    return (
        <div className="relative h-screen overflow-hidden">

            {/* Arrow to open the side bar on mobile  */}
            <button
                onClick={() => setSidebarOpen(!sidebarOpen)}
                className="md:hidden fixed top-12 left-7 z-5"
            >
                {!sidebarOpen && "←"}
            </button>

            {/* Dark Backdrop when sidebar is open on mobile */}
            {sidebarOpen && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-30 z-30 md:hidden"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            <div className="flex h-full">

                {/* Sidebar */}
                <div
                    className={`
                        fixed z-40 inset-y-0 left-0 w-3/4 max-w-xs bg-white shadow-md transform transition-transform duration-300
                        md:relative md:translate-x-0 md:w-64 md:z-auto
                        ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
                    `}
                >
                    <SideBar
                        onSelectView={(view) => {
                            setSelectedView(view);
                            setSidebarOpen(false);
                        }}
                    />
                </div>

                {/* Main Content */}
                <main className="flex-1 overflow-y-auto bg-white">
                    {renderContent()}
                </main>

                {/* Call Modals */}
                {incomingCall && (
                    <IncomingCallModal
                        caller={incomingCall.displayName}
                        onAccept={handleAccept}
                        onDecline={handleDecline}
                    />
                )}
                {activeCallId && <CallRoomModal />}
            </div>
        </div>
    );
}
