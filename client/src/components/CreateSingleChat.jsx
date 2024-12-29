import { post } from "../utils/api";
import SearchUser from "./SearchUser";

export default function CreateSingleChat({setSelectedView, currentUser}) {

    // Function to handle when the user is selected
    const handleSelectUser = async (user) => {
        try {
            // API call to create the one to one chat
            const response = await post("/api/chats/single", { userId: user.id });

            // Sets the view to chat and passes the data from the response
            setSelectedView({type: "chat", data: {chat:response, currentUser}})
        } catch (error) {
            console.log("Failed to create the chat ", error);
        }
    }

    return <SearchUser onSelectUser={handleSelectUser} searchPlaceHolder="Start a private chat"/>;
}