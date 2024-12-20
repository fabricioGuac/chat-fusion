import { put } from "../utils/api";
import SearchUser from "./SearchUser";

export default function AddUserToGroup({  chatId ,setSelectedView }) {
    const handleAddUser = async (user) => {
        try {
            // Makes the Api call to update the chat users
            const Updatedchat = await put(`/api/${chatId}/add/${user.id}`);
            // Sets the view to the chat with the latest data
            setSelectedView({type:"chat", data:Updatedchat});
        } catch (error) {
            console.log("Error adding user to group", error);
        }
    };

    return <SearchUser onSelectUser={handleAddUser} searchPlaceHolder="Select you new user"/>
}