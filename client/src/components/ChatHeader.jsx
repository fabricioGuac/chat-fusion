import { useState, useEffect, useRef } from "react";
import UpdateDetails from "./UpdateDetails";
import AddUserToGroup from "./AddUserToGroup";
import DeleteChatModal from "./DeleteChatModal";
import MembersModal from "./MemberList";

import { get } from "../utils/api";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

export default function ChatHeader({ chat, currentUser }) {
    // State for managing modals and dropdown visibility
    const [modal, setModal] = useState("");
    const [dropDownOpen, setDropDownOpen] = useState(false);


    // Initializes `lastConnection` state variable and uses `otherUser` for all conditional logic
    const otherUser = chat.group ? null : chat.members.find((member) => member.id !== currentUser.id);
    const [lastConnection, setLastConnetion] = useState(otherUser?.lastConnection);


    // WebSocket client ref
    const stompClient = useRef(null);

    // Function to close modal
    const closeModal = () => setModal("");

    // Toggles the dropdown menu
    const toggleDropDown = () => setDropDownOpen(!dropDownOpen);

    // Determines if the chat is a self chat
    // const isSelfChat = !chat.group && chat.members.length === 1;

    // Placeholder for calls
    const handleCall = () => {
        console.log("Call logic coming soon");
    };

    // Placeholder for videocalls
    const handleVideoCall = () => {
        console.log("Video call logic coming soon");
    };

    // Determines the image source for the chat
    const imageSrc = otherUser
        ? otherUser.pfp || "/logo192.png"
        : chat.group
            ? chat.chat_image || "/logo192.png"
            : currentUser.pfp || "/logo192.png";

    // Determines the display name for the chat
    const displayName = otherUser
        ? otherUser.username
        : chat.group
            ? chat.chat_name
            : "You";

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

            // Subscribes to the other user's online status topic
            stompClient.current.subscribe(`/topic/online-status/${otherUser.email}`, (messageOutput) => {
                // Gets the online status from the parsed event message
                const isOnline = JSON.parse(messageOutput.body);
                // If the user is online set the last connection to null and if the user disconnets sets their last connection to that moment
                isOnline ? setLastConnetion(null) : setLastConnetion(new Date().toLocaleString('en-US', { year: '2-digit', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: true }).replace(',', ''));
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

    useEffect(() => {
        // Early return if the other user is not defined meaning it is a self or group chat
        if (!otherUser) {
            return;
        };

        // Function to fetch the most up to date last connection for an user
        const getLatestLastConnection = async () => {
            try {
                // Fetches the last connection
                const actualLastConnection = await get(`/api/users/lastConnection/${otherUser.id}`);
                // Sets the last connection to the state variable
                setLastConnetion(new Date(actualLastConnection).toLocaleString('en-US', { year: '2-digit', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: true }).replace(',', ''));
            } catch (error) {
                // If the user is online it means the lastConnection is null so we set the lastConnection to null
                setLastConnetion(null);
                console.log("Error getting the othe user latest last connection ", error);
            }
        }

        // Initiates the websocket connection
        connectWebSocket();
        
        // Calls the getLatestLastConnection funtion
        getLatestLastConnection();

        // Cleanup on unmount
        return () => {
            if (stompClient.current) {
                // Disconnects if connected and stops auto reconnect loop
                stompClient.current.deactivate();
            }
        }

    }, [chat.id]);

    return (
        <div className="flex items-center justify-between p-4 border-b bg-white">
            {/* Chat header section displaying image and name */}
            <div className="flex items-center space-x-4">
                <img
                    src={imageSrc}
                    alt="Chat avatar"
                    className="w-10 h-10 rounded-full object-cover"
                />
                <h1 className="text-lg font-bold text-gray-800">{displayName}</h1>
                {otherUser && (
                    <p className="text-sm text-gray-600">
                        {/* {lastConnection ? lastConnection : "online" } */}
                        {lastConnection ? `Last connection ${lastConnection}` : "online"}

                    </p>
                )}
            </div>

            {/* Action buttons (call, video call, options dropdown) */}
            <div className="flex items-center space-x-2">
                <button
                    onClick={handleCall}
                    className="text-gray-600 hover:text-gray-800"
                    aria-label="Call"
                >
                    📞
                </button>
                <button
                    onClick={handleVideoCall}
                    className="text-gray-600 hover:text-gray-800"
                    aria-label="Video Call"
                >
                    🎥
                </button>

                {/* Options dropdown */}
                <div className="relative">
                    <button onClick={toggleDropDown} className="text-gray-600 hover:text-gray-800">
                        Options
                    </button>
                    {dropDownOpen && (
                        <div className="absolute right-0 mt-2 w-48 bg-white border rounded shadow-md">
                            {chat.group && (
                                <>
                                    <button
                                        onClick={() => setModal("updateDetails")}
                                        className="block px-4 py-2 text-left text-gray-700 hover:bg-gray-100 w-full"
                                    >
                                        Update Details
                                    </button>
                                    <button
                                        onClick={() => setModal("addUser")}
                                        className="block px-4 py-2 text-left text-gray-700 hover:bg-gray-100 w-full"
                                    >
                                        Add User to Group
                                    </button>
                                    <button
                                        onClick={() => setModal("viewMembers")}
                                        className="block px-4 py-2 text-left text-gray-700 hover:bg-gray-100 w-full"
                                    >
                                        View Members
                                    </button>
                                </>
                            )}
                            <button
                                onClick={() => setModal("deleteChat")}
                                className="block px-4 py-2 text-left text-red-600 hover:bg-red-100 w-full"
                            >
                                Delete Chat
                            </button>
                        </div>
                    )}
                </div>
            </div>

            {/* Modals for different actions */}
            {modal === "updateDetails" && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
                        <UpdateDetails
                            data={{ type: "group", details: chat }}
                            closeModal={closeModal}
                        />
                    </div>
                </div>
            )}
            {modal === "addUser" && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
                        <AddUserToGroup chatId={chat.id} closeModal={closeModal} />
                    </div>
                </div>
            )}
            {modal === "deleteChat" && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
                        <DeleteChatModal chatId={chat.id} closeModal={closeModal} />
                    </div>
                </div>
            )}
            {modal === "viewMembers" && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
                        <MembersModal chat={chat} currentUserId={currentUser.id} closeModal={closeModal} />
                    </div>
                </div>
            )}
        </div>
    );
}
