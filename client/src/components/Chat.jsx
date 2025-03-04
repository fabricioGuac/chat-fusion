import ChatHeader from "./ChatHeader";
import ChatBody from "./ChatBody";
import ChatInput from "./ChatInput";
import { useSelector } from "react-redux";
import { useState, useEffect, useRef } from "react";

import ws from "../utils/ws";


// Chat component combining the header, body and input sections
export default function Chat({ chatId, setSelectedView }) {

    // State variable to manage the last user to connect to the chat
    const [lastConnectedUser, setLastconnectedUser] = useState(null);



    // Retrieves the current user data and current chat data from the Redux store
    const currentUser = useSelector((state) => state.user.user);
    const chat = useSelector((state) => state.chats.chats.find((chat) => chat.id === chatId));

    // UseEffect to handle connection and disconection from the websocket on chat change
    useEffect( () => {
        
        // If the chat is not found go back to basic view
        if (!chat) {
            setSelectedView({ type: null, data: null });
            return;
        }

        ws.publish(`/app/connected/${chat.id}`, { userId: currentUser.id, online: true });

        const topic = `/chat/${chat.id}/connected`;
        // Initializes a subscription to listen on connections to a chat
        ws.subscribe(topic,(event) =>{
            if(event.online) { 
                // Set the lastConnectedUser to the user with the id from the event
                setLastconnectedUser(chat.members.find((member) => member.id === event.userId));
            } else {
                setLastconnectedUser(null);
            }
        } )
        
        // Cleanup function to unsubscribe from the chat connections events
        return () => {
            ws.unsubscribe(topic);
        };

    }, [chatId, chat])

        // Renders a loading indicator if the user data is not yet available
        if (!currentUser || !chat) {
            return <div>Loading...</div>;
        }


    return (
        <div className="flex flex-col h-full">
            {/* Chat header with chat details and user data */}
            <ChatHeader chat={chat} currentUser={currentUser} />
            <div className="flex-1 overflow-y-auto">
                {/* Chat boy where the messages are displayed */}
                <ChatBody chat={chat} currentUser={currentUser} lastConnectedUser={lastConnectedUser} />
            </div>
            {/* Input field for sending new messages */}
            <ChatInput chatId={chat.id} />
        </div>
    );
}