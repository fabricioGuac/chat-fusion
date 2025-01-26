import { useState, useEffect, useRef } from "react";
import { get } from "../utils/api";

import { useDispatch } from "react-redux";
import { setUser } from "../redux/userSlice";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

export default function SideBar({ onSelectView }) {
    // Dispatch used to send actions to the Redux store
    const dispatch = useDispatch();

    // State  variables to handle the data and it's loading state
    const [currentUser, setCurrentUser] = useState(null);
    const [chats, setChats] = useState([]);
    const [loading, setLoading] = useState(true);

    // WebSocket client ref
    const stompClient = useRef(null);





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
            console.log("Connected NOTIFICATIONS WS");

            // Subscribes to the chat topic
            stompClient.current.subscribe(`/chat/notifications/${currentUser.id}`, (messageOutput) => {
                // Parses the message event
                const chatId = messageOutput.body;
                // Increates the unread count for the chat that received the new message
                setChats((prevChats) =>
                    prevChats.map((chat) =>
                        chat.id === chatId
                            ? {
                                ...chat,
                                unreadCounts: {
                                    ...chat.unreadCounts,
                                    [currentUser.id]: (chat.unreadCounts[currentUser.id] || 0) + 1,
                                },
                            }
                            : chat
                    )
                );
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

    // Fucntion to reset the unread count to zero
    const resetCount = (chatId) => {
        setChats((prevChats) =>
            prevChats.map((chat) => chat.id === chatId ?
                { ...chat, unreadCounts: { ...chat.unreadCounts, [currentUser.id]: 0, } } :
                chat
            ))
    };








    // UseEffect to fetch user profile and chats on component mount
    useEffect(() => {
        // Asynchronous function to fetch sidebar data
        const fetchSidebarData = async () => {
            try {
                // Awaits the resolution of both API calls simultaneously using Promise.all
                const [userData, chatsData] = await Promise.all([
                    get("/api/users/profile"),
                    get("/api/chats/user"),
                ]);

                // Stores the fetched user data in the Redux store
                dispatch(setUser(userData));
                // Updates the state variables with the fetched data 
                setCurrentUser(userData);
                setChats(chatsData);

            } catch (error) {
                console.log(error.message);
                console.log("Error fetching data" + error);
            } finally {
                // Sets the loading to false
                setLoading(false);
            }
        }

        // Calls the fetch function
        fetchSidebarData();

        // Connects to the websocket
        connectWebSocket();

        return () => {
            if (stompClient.current) {
                // Disconnects if connected and stops auto reconnect loop
                stompClient.current.deactivate();
            }
        }
    }, []);

    if (loading) {
        return <div className="w-64 bg-gray-100 text-center h-screen p-4">Loading...</div>;
    }

    return (
        <aside className="w-64 bg-gray-100 h-screen flex flex-col">
            {/* Header Section */}
            <div
                className="p-4 flex items-center gap-4 cursor-pointer hover:bg-gray-200"
                onClick={() =>
                    onSelectView({ type: "update-details", data: { type: "user", details: currentUser } })
                }
            >
                <img
                    src={currentUser.pfp || "/logo192.png"}
                    alt="profile picture"
                    className="w-12 h-12 rounded-full object-cover"
                />
                <h2 className="font-bold">{currentUser.username}</h2>
            </div>


            <div className="p-4">
                <button
                    onClick={() =>
                        onSelectView({ type: "single-create", data: { currentUser } })
                    }
                    className="flex items-center justify-center bg-gray-200 p-2 rounded w-full hover:bg-gray-300"
                >
                    <span role="img" aria-label="search" className="text-lg">
                        🔍
                    </span>
                    Search for users
                </button>
            </div>

            {/* Create Group Button */}
            <div className="p-4">
                <button
                    className="bg-blue-500 text-white py-2 px-4 rounded shadow-lg hover:bg-blue-600 w-full"
                    onClick={() => onSelectView({ type: "group-create" })}
                >
                    Create Group
                </button>
            </div>

            {/* Chat List Section */}
            <div className="flex-1 overflow-y-auto p-4">
                <h2 className="text-xl font-bold mb-4">Chats</h2>
                {chats.length === 0 ? (
                    <div className="text-gray-500 text-center">
                        Search for an user to start a conversation!
                    </div>
                ) : (
                    <ul>
                        {chats.map((chat) => {
                            // Determines if this is a self-chat
                            const isSelfChat = !chat.group && chat.members.length === 1;

                            // Determines the image source
                            const imageSrc = isSelfChat
                                ? currentUser.pfp || "/logo192.png"
                                : chat.group
                                    ? chat.chat_image || "/logo192.png"
                                    : chat.members[0].id === currentUser.id
                                        ? chat.members[1].pfp || "/logo192.png"
                                        : chat.members[0].pfp || "/logo192.png";

                            // Determines the name to display
                            const displayName = isSelfChat
                                ? "You"
                                : chat.group
                                    ? chat.chat_name
                                    : chat.members[0].id === currentUser.id
                                        ? chat.members[1].username
                                        : chat.members[0].username;

                            return (
                                <li
                                    key={chat.id}
                                    className="p-2 flex items-center gap-4 hover:bg-gray-200 cursor-pointer"
                                    onClick={() => {
                                        onSelectView({ type: "chat", data: { chat } });
                                        resetCount(chat.id);
                                    }
                                    }
                                >
                                    <img
                                        src={imageSrc}
                                        alt="chat image"
                                        className="w-10 h-10 rounded-full object-cover"
                                    />
                                    <span>{displayName}</span>
                                    {/* Unread message counter for the logged in user */}
                                    {(chat.unreadCounts[currentUser.id] || 0) > 0 && <span className="bg-red-200 rounded-full ml-8 px-2">{chat.unreadCounts[currentUser.id]}</span>}
                                </li>
                            );
                        })}
                    </ul>
                )}
            </div>
        </aside>
    );
}