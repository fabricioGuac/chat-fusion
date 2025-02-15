import { useState } from "react";
import { del, put } from "../utils/api";

export default function Message({ message, currentUser, chatId }) {
    // Deconstructs the message
    const { id, content, user, timestamp, type, readBy } = message;

    // State variables to handle editing state, the edit content and error messages
    const [isEditing, setIsEditing] = useState(false);
    const [editContent, setEditcontent] = useState(content);
    const [errorMessage, setErrorMessage] = useState("");

    // Flag to indicate if the user is the author of the message
    const isCurrentUser = user.id === currentUser.id;


    // Handles editing the message
    const handleEdit = async () => {
        try {
            await put("/api/messages/edit", {
                newContent: editContent,
                messageId: id,
            });

            setIsEditing(false);
            setErrorMessage("");
        } catch (error) {
            console.log("Error updating message", error.message || error);
            setErrorMessage("Failed to edit the message. Try again.");
        }
    };

    // Handles deleting the message
    const handleDelete = async () => {
        try {
            await del(`/api/messages/delete/${id}/${chatId}`);
            setErrorMessage("");
        } catch (error) {
            console.log("Error deleting message", error.message || error);
            setErrorMessage("Failed to delete the message. Try again.");
        }
    };

    return (
        <div className={`flex items-start ${isCurrentUser ? "justify-end" : "justify-start"} mb-2`}>
            <div className={`max-w-xs p-3 rounded-lg ${isCurrentUser ? "bg-blue-500 text-white" : "bg-gray-200 text-gray-800"} break-words`}>
                {/* Dispplays the author's username */}
                <h5 className="text-xs mb-1">{user.username}</h5>

                {/* Shows edit input or message content */}
                {isEditing ? (
                    <div>
                        <input
                            type="text"
                            value={editContent}
                            onChange={(e) => setEditcontent(e.target.value)}
                            className="w-full p-1 rounded border text-black"
                        />
                        <button
                            onClick={handleEdit}
                            className="text-sm text-green-500 hover:underline mr-2"
                        >
                            Save changes
                        </button>
                        <button
                            onClick={() => setIsEditing(false)}
                            className="text-sm text-gray-800 hover:underline"
                        >
                            Cancel
                        </button>
                    </div>
                ) : (
                    <div>
                        {type === "text" ? (
                            <p>{content}</p>
                        ) : type === "image" ? (
                            <img src={content} alt="image" className="rounded" />
                        ) : type === "audio" ? (
                            <audio controls>
                                <source src={content} type="audio/mp3" />
                            </audio>
                        ) : type === "video" ? (
                            <video controls preload="metadata" className="rounded">
                                <source src={content} type="video/mp4" />
                            </video>
                        ) : (
                            <p>
                                Unsupported media type
                            </p>
                        )}
                    </div>
                )}
                {/* Shows error message */}
                {errorMessage && <p className="text-red-500 text-xs mt-2">{errorMessage}</p>}

                {/* Displays message timestamp */}
                <div className="text-sm mt-2 text-right">
                    {new Date(timestamp).toLocaleString('en-US', { year: '2-digit', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: true }).replace(',', '')}
                    {/* {new Date(timestamp).toLocaleDateString()} */}
                </div>



                {/* Display read status for the message */}
                {readBy && isCurrentUser && (
                    <div className="mt-2 relative group">
                        {readBy.length > 0 ? (<span className="text-sm cursor-pointer hover:underline">
                            Read by {readBy.length} {readBy.length > 1 ? "users" : "user"}
                            {/* Hover container */}
                            <div className="hidden group-hover:block absolute left-0 bg-gray-400 p-2 border rounded shadow-lg  mt-1 z-10">
                                <ul className="space-y-1">
                                    {readBy.map((reader) => (
                                        <li key={reader.id} className="text-sm text-white">{reader.username}</li>
                                    ))}
                                </ul>
                            </div>
                        </span>) : (<span className="text-sm">
                            Unread
                                </span>)
                        }
                    </div>
                )}


                {/* Shows edit and delete options */}
                {(isCurrentUser || currentUser.isAdmin) && (
                    <div className="flex items-center ml-2 space-x-2">
                        {isCurrentUser && !isEditing && type === "text" && (
                            <button
                                onClick={() => setIsEditing(true)}
                                className="text-yellow-500 hover:underline text-sm"
                            >
                                Edit
                            </button>
                        )}
                        <button
                            onClick={handleDelete}
                            className="text-red-500 hover:underline text-sm"
                        >
                            Delete
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
