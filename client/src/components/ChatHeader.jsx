import { useState } from "react";
import UpdateDetails from "./UpdateDetails";
import AddUserToGroup from "./AddUserToGroup";
import DeleteChatModal from "./DeleteChatModal";
import MembersModal from "./MemberList";

export default function ChatHeader({ chat, currentUser }) {
    // State for managing modals and dropdown visibility
    const [modal, setModal] = useState("");
    const [dropDownOpen, setDropDownOpen] = useState(false);

    // Function to close modal
    const closeModal = () => setModal("");

    // Toggles the dropdown menu
    const toggleDropDown = () => setDropDownOpen(!dropDownOpen);

    // Determines if thechat is a self chat
    const isSelfChat = !chat.group && chat.members.length === 1;

    // Determines the image source for the chat
    const imageSrc = isSelfChat
        ? currentUser.pfp || "/logo192.png"
        : chat.group
            ? chat.pfp || chat.chat_image || "/logo192.png"
            : chat.members[0].id === currentUser.id
                ? chat.members[1].pfp || "/logo192.png"
                : chat.members[0].pfp || "/logo192.png";

    // Determines the display name for the chat
    const displayName = isSelfChat
        ? "You"
        : chat.group
            ? chat.chat_name
            : chat.members[0].id === currentUser.id
                ? chat.members[1].username
                : chat.members[0].username;

    // Placeholder for calls
    const handleCall = () => {
        console.log("Call logic coming soon");
    };
    
    // Placeholder for videocalls
    const handleVideoCall = () => {
        console.log("Video call logic coming soon");
    };

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
                            data={{ type: chat.group ? "group" : "user", details: chat }}
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
