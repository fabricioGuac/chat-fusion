import { useState, useEffect } from "react";
import { put } from "../utils/api";

export default function MembersModal({ chat, currentUserId, closeModal }) {
    // State variable to flag if the user is an admin of the group
    const [isAdmin, setIsAdmin] = useState(false);
    // Array of chat members
    const members = chat.members;

    // Checks if current user is an admin
    useEffect(() => {
        if (chat.adminIds.includes(currentUserId)) {
            setIsAdmin(true);
        }
    }, [chat.admins, currentUserId]);

    // Method to grant admin rights
    const makeAdmin = async (memberId) => {
        console.log(`Make member with ID ${memberId} an admin`);
        try {
            // Makes api call to update the member role
            await put(`/api/chats/${chat.id}/makeAdmin/${memberId}`);
            
        } catch (error) {
            console.log("Error making member an admin ", error)
        }
    };

    // Method to remove a user from the group
    const removeFromGroup = async (memberId) => {
        console.log(`Remove member with ID ${memberId} from group ${chat.id}`);
        try {
            // Makes api call to update the member list
            await put(`/api/chats/${chat.id}/remove/${memberId}`);
            
        } catch (error) {
            console.log("Error removing member ", error);
        }

    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
            <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-lg">
                {/* Modal title */}
                <h2 className="text-xl font-bold mb-4 text-center">Group Members</h2>
                {/* Member list */}
                <ul className="divide-y divide-gray-200">
                    {members.map((member) => (
                        <li key={member.id} className="py-4 flex justify-between items-center">
                            <div className="flex items-center">
                                <img
                                    src={member.pfp || "/logo192.png"}
                                    alt={`${member.username} avatar`}
                                    className="w-10 h-10 rounded-full mr-3"
                                />
                                <span className="text-gray-800">{member.username}</span>
                            </div>

                            {/* Actions for admin or current user */}
                            <div className="flex items-center space-x-2">
                                {isAdmin && member.id !== currentUserId && (
                                    <>
                                    {/* Button to make the member an admin */}
                                        { !chat.adminIds.includes(member.id) && (<button
                                            onClick={() => makeAdmin(member.id)}
                                            className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600"
                                        >
                                            Make Admin
                                        </button>)}
                                        
                                        {/* Button to remove a  membe from the group */}
                                        <button
                                            onClick={() => removeFromGroup(member.id)}
                                            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                                        >
                                            Remove
                                        </button>
                                    </>
                                )}

                                {/* Option for the current user to leave the group */}
                                { member.id === currentUserId && (
                                    <button
                                        onClick={() => removeFromGroup(member.id)}
                                        className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                                    >
                                        Leave Group
                                    </button>
                                )}
                            </div>
                        </li>
                    ))}
                </ul>

                {/* Close button */}
                <button
                    onClick={closeModal}
                    className="mt-4 bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
                >
                    Close
                </button>
            </div>
        </div>
    );
}
