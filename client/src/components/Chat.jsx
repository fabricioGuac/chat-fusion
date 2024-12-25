import ChatHeader from "./ChatHeader";
import ChatBody from "./ChatBody";
import ChatInput from "./ChatInput";

// Chat component combining the header, body and input sections
export default function Chat({ chat, currentUser }) {
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