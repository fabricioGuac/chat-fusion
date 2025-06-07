import { useState } from "react";
import { del } from "../utils/api";

export default function DeleteChatModal({ chatId, closeModal }) {
    // State variables for managing the error message and the confirmation input
    const [errorMessage, setErrorMessage] = useState("");
    const [confirmation, setConfirmation] = useState("");

    // Method to delete a chat
    const handleDeleteChat = async () => {
        // Ensures the confirmation input matches the desired message
        if (confirmation.toLowerCase() === "i want to delete the chat") {
            console.log(`Deleting chat ${chatId}`);
            try {
                // Makes the API call to delete the chat
                await  del(`/api/chats/delete/${chatId}`);
                // Closes the modal on success
                closeModal();
                //TODO: Ensure the chat is closed after deletion (At least notify the user the chat does not exist anymore)
            } catch (error) {
                console.log(`Unable to delete chat`, error);
                setErrorMessage(error.message || "An unexpected error occurred");
            }
        } else {
            console.log(`Confirmation does not match the requested message "i want to delete this chat" not equals  ${confirmation}`);
            setErrorMessage("The text does not match the required input");
        }
    };

    return (
        <>
        {/* Modal header */}
            <h2 className="text-lg font-bold text-gray-800 mb-4">Confirm chat deletion</h2>
            <p className="text-sm text-gray-600 mb-4">To confirm deletion type <strong>I want to delete the chat</strong></p>
            
            {/* Confirmation input */}
            <input type="text"
                value={confirmation}
                onChange={(e) => setConfirmation(e.target.value)}
                className="border px-3 py-2 rounded w-full mb-4"
                placeholder="I want to delete the chat"
            />

            {/* Buttons */}
            <div className="flex justify-end space-x-2">

                {/* Cancel button */}
                <button
                    onClick={closeModal}
                    className="px-4 py-2 bg-gray-700 rounded hover:bg-gray-300"
                >
                    Cancel
                </button>


                {/* Delete button */}
                <button
                    onClick={handleDeleteChat}
                    className="px-4 py-2 bg-red-200 text-gray-700 rounded hover:bg-red-700"
                >
                    Delete
                </button>

            </div>

            {/* Error message */}
            {errorMessage && (
                <div className="mt-4 text-red-600 text-sm">
                    <strong>Error:</strong> {errorMessage}
                </div>
            )}
        </>
    );
}