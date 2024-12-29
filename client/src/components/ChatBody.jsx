import { useState, useEffect, useRef } from "react";
import { get } from "../utils/api";
import Message from "./Message";

export default function ChatBody({ chat, currentUser }) {
    // State variables for managing messages, skipped messages count, and loading state
    // const [messages, setMessages] = useState([]);
    const [messages, setMessages] = useState([]);

    const [skip, setSkip] = useState(0);
    const [loading, setLoading] = useState(false);

    // Reference for the chat body element
    const chatBodyRef = useRef();

    // Increments the skip count when scrolled to the top
    const handleScroll = () => {
        if (chatBodyRef.current.scrollTop === 0) {
            setSkip((prevSkip) => prevSkip + 100);
            console.log("Fetching messages");
            console.log(skip);
        }
    };

    // Method to retrieve the messages from the server
    const fetchMessages = async () => {
        try {
            // Sets the loading flag to indicate it is loading
            setLoading(true);
            // Makes API call to get the messages for the current chat and skip the indiated messages
            const response = await get(`/api/messages/chat/${chat.id}?skip=${skip}`);
            // Prepends the fetched messages to the existing list
            setMessages((prevMessages) => [...response, ...prevMessages]);
        } catch (error) {
            console.log("Error fetching messages", error.message);
        } finally {
            // Once the fetching is done set loading to false
            setLoading(false);
        }
    }

    // Fetches the messages when the chat or the skip changes
    useEffect(() => {
        //Clear the messages for each chat
        setMessages([]);
        fetchMessages();
    }, [chat.id]);

    // Adds a scroll event listener to the chat body element to detect when the user scrolls to the top
    useEffect(() => {
        const chatBody = chatBodyRef.current;
        chatBody.addEventListener("scroll", handleScroll);
        // Adds a scroll event listener to the chat body element to detect when the user scrolls to the top
        return () => chatBody.removeEventListener("scroll", handleScroll);
    }, [loading]); // Re-attaches event listener when `loading` changes


    return (
        // Container for chat messages
        <div
            ref={chatBodyRef}
            className="flex flex-col h-full overflow-y-auto p-4 bg-gray-100"
        >

            {/* Loading indicator */}
            {loading && <div className="flex justify-center items-center h-screen text-5xl">Loading...</div>}
            
            {/* Render messages */}
            {messages.map((msg) => (
                <Message
                    key={msg.id}
                    message={msg}
                    currentUser={{ ...currentUser, isAdmin: chat.members.includes(currentUser) }}
                />
            ))}

            {/* Empty chat message */}
            {!loading && messages.length === 0 && (
                <div className="flex justify-center items-center h-screen text-5xl">
                    No messages in this chat yet. Start the conversation!
                </div>
            )}
        </div>
    )
}