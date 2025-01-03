import ChatHeader from "./ChatHeader";
import ChatBody from "./ChatBody";
import ChatInput from "./ChatInput";
import { useSelector } from "react-redux";


// Chat component combining the header, body and input sections
export default function Chat({ chat }) {

    // Retrieves the current user data from the Redux store
    const currentUser = useSelector((state) => state.user.user);
    // Renders a loading indicator if the user data is not yet available
    if (!currentUser) {
        return <div>Loading...</div>;
    }
    
    return (
        <div className="flex flex-col h-full">
            {/* Chat header with chat details and user data */}
            <ChatHeader chat={chat} currentUser={currentUser} />
            <div className="flex-1 overflow-y-auto">
                {/* Chat boy where the messages are displayed */}
                <ChatBody chat={chat} currentUser={currentUser} />
            </div>
            {/* Input field for sending new messages */}
            <ChatInput chatId={chat.id} />
        </div>
    );
}