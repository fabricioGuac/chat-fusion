import { put } from "../utils/api";
import SearchUser from "./SearchUser";

export default function AddUserToGroup({ chatId, closeModal }) {
    // Handles adding a seleted user to the group
    const handleAddUser = async (user) => {
        try {
            // Makes the API call to add the selected user to the group
            await put(`/api/${chatId}/add/${user.id}`);
            // Closes the modal on success
            closeModal();
            // TODO: handle the notification with WebSocket
        } catch (error) {
            console.log("Error adding user to group", error);
        }
    };

    return (
        <div>
            {/* SearchUser component allows searching and selecting a user */}
            <SearchUser 
                onSelectUser={handleAddUser} 
                searchPlaceHolder="Select your new user"
            />
            {/* Button to close the modal */}
            <div className="mt-4 flex justify-end">
                <button 
                    onClick={closeModal} 
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
                >
                    Cancel
                </button>
            </div>
        </div>
    );
}