import { useState, useEffect} from "react";
import UpdateDetails from "./UpdateDetails";
import AddUserToGroup from "./AddUserToGroup";
import DeleteChatModal from "./DeleteChatModal";
import MembersModal from "./MemberList";

import { useDispatch } from 'react-redux';
import { setCall } from '../redux/callSlice';

import { get } from "../utils/api";
import ws from "../utils/ws";

export default function ChatHeader({ chat, currentUser }) {
    // Dispatch used to send actions to the Redux store
    const dispatch = useDispatch();

    // State for managing modals and dropdown visibility
    const [modal, setModal] = useState("");
    const [dropDownOpen, setDropDownOpen] = useState(false);

    // Initializes `lastConnection` state variable and uses `otherUser` for all conditional logic
    const otherUser = chat.group ? null : chat.members.find((member) => member.id !== currentUser.id);
    const [lastConnection, setLastConnetion] = useState(otherUser?.lastConnection);

    // Function to close modal
    const closeModal = () => setModal("");

    // Toggles the dropdown menu
    const toggleDropDown = () => setDropDownOpen(!dropDownOpen);

    // Function to handle call requests
    const handleCall = () => {

        // Gets the group name if it is a group or the user username if it is a private chat and the caller id to skip notifying the caller
        const caller = {
            displayName: chat.group ? chat.chat_name : currentUser.username,
            userId:currentUser.id,
            chatId:chat.id, 
        };
        // Emits the call request event to the chat members
        ws.publish(`/app/call/${chat.id}`, caller);
        // Opens the call room
        dispatch(setCall(chat.id));
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

        const topic = `/topic/online-status/${otherUser.email}`;
        // Subscribes to the online status of the other user
        ws.subscribe(topic, (isOnline)=> {
            // If the user is online set the last connection to null and if the user disconnets sets their last connection to that moment
            isOnline ? setLastConnetion(null) : setLastConnetion(new Date().toLocaleString('en-US', { year: '2-digit', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: true }).replace(',', ''));
        });
        
        // Calls the getLatestLastConnection funtion
        getLatestLastConnection();

        // Cleanup on unmount
        return () => {
            ws.unsubscribe(topic);
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
                                    { chat.adminIds.includes(currentUser.id) && (<button
                                        onClick={() => setModal("addUser")}
                                        className="block px-4 py-2 text-left text-gray-700 hover:bg-gray-100 w-full"
                                    >
                                        Add User to Group
                                    </button>)}
                                    <button
                                        onClick={() => setModal("viewMembers")}
                                        className="block px-4 py-2 text-left text-gray-700 hover:bg-gray-100 w-full"
                                    >
                                        View Members
                                    </button>
                                </>
                            )}
                            { (chat.adminIds.includes(currentUser.id) || !chat.group) && (<button
                                onClick={() => setModal("deleteChat")}
                                className="block px-4 py-2 text-left text-red-600 hover:bg-red-100 w-full"
                            >
                                Delete Chat
                            </button>)}
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
