import { useState, useEffect, useRef } from "react";
import { get } from "../utils/api";
import Message from "./Message";
import ws from "../utils/ws";


export default function ChatBody({ chat, currentUser, lastConnectedUser }) {
    // State variables for managing messages, and loading state
    const [messages, setMessages] = useState([]);

    const [loading, setLoading] = useState(false);

    // Reference to track the skipped messages
    const skipRef = useRef(0);
    // Reference for the chat body element
    const chatBodyRef = useRef(null);
    // Reference for the end of the chat
    const chatEndRef = useRef(null);
    // Referece for the flag if it is prepending old messages
    const isPrependingRef = useRef(false);
    // Reference to mantain the heigh once old messages are prepended
    const prevScrollHeightRef = useRef(0);

    // Increments the skip count when scrolled to the top
    const handleScroll = () => {
        if (chatBodyRef.current.scrollTop === 0) {
            isPrependingRef.current = true;
            prevScrollHeightRef.current = chatBodyRef.current.scrollHeight;
            skipRef.current += 100;
            console.log("Fetching messages");
            console.log(skipRef.current);
            fetchMessages();
        }
    };

    // Method to retrieve the messages from the server
    const fetchMessages = async () => {
        try {
            // Sets the loading flag to indicate it is loading
            setLoading(true);
            // Makes API call to get the messages for the current chat and skip the indiated messages
            const response = await get(`/api/messages/chat/${chat.id}?skip=${skipRef.current}`);
            // Prepends the fetched messages to the existing list
            setMessages((prevMessages) => [...response.reverse(), ...prevMessages]);

        } catch (error) {
            console.log("Error fetching messages", error.message);
        } finally {
            // Once the fetching is done set loading to false
            setLoading(false);
        }
    }

    // Scrolls to the bottom of the chat
    const scrollToBottom = () => {
        if (chatEndRef.current) {
            chatEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    };

    // Fetches the messages when the chat or the skip changes
    useEffect(() => {
        skipRef.current = 0;
        //Clear the messages for each chat
        setMessages([]);
        fetchMessages();

        const topic = `/chat/${chat.id}`;
        // Initiates the subscription to the message events
        ws.subscribe(topic, (event) => {
            // If it is a send event adds the new message to the chat
            if (event.type === "send") {
                setMessages((prevMessages) => [...prevMessages, event.payload]);
            } else if (event.type === "edit") {
                // Replaces the edited message in the chat
                setMessages((prevMessages) =>
                    prevMessages.map((msg) => msg.id === event.payload.id ? event.payload : msg));
            } else if (event.type === "delete") {
                // Removes the delete message from the chat
                setMessages((prevMessages) => prevMessages.filter((msg) => msg.id !== event.payload));
            }
        })

        // Cleanup on unmount
        return () => {
            // Unsubsribes from the message events
            ws.unsubscribe(topic);
        };

    }, [chat.id]);

    // Scrolls to bottom when chat changes or new messages arrive
    useEffect(() => {
    const chatBody = chatBodyRef.current;

    if (isPrependingRef.current && chatBody) {
        // Calculates the difference in scroll height caused by new messages
        const newScrollHeight = chatBody.scrollHeight;
        const scrollDifference = newScrollHeight - prevScrollHeightRef.current;

        // Offsets the scroll position by the height difference to preserve position
        chatBody.scrollTop = scrollDifference;

        isPrependingRef.current = false;
    } else {
        scrollToBottom(); // Only scroll to bottom on new messages / chat change
    }
    }, [chat.id, messages])

    // Adds a scroll event listener to the chat body element to detect when the user scrolls to the top
    useEffect(() => {
        const chatBody = chatBodyRef.current;
        chatBody.addEventListener("scroll", handleScroll);
        // Adds a scroll event listener to the chat body element to detect when the user scrolls to the top
        return () => chatBody.removeEventListener("scroll", handleScroll);
    }, [loading]); // Re-attaches event listener when `loading` changes

    // UseEffect to mark the unread messsages as read
    useEffect(() => {
        // Early return if there is no lastConnected user
        if (!lastConnectedUser) {
            return;
        }
        // Iterates over the message lista and adds the lastConnectedUser if not included in the readBy field
        setMessages((prevMessages) =>
            prevMessages.map((message) =>
                message.user.id !== lastConnectedUser.id && !message.readBy.some(user => user.id === lastConnectedUser.id) ? { ...message, readBy: [...message.readBy, lastConnectedUser] } : message
            )
        )
    }, [lastConnectedUser]); // Uses the lastConnectedUser as a dependency array so that it triggers on each connection 


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
                    currentUser={{ ...currentUser, isAdmin: chat.adminIds.includes(currentUser.id) }}
                    chatId={chat.id}
                />
            ))}

            {/* Empty chat message */}
            {!loading && messages.length === 0 && (
                <div className="flex justify-center items-center h-screen text-5xl">
                    No messages in this chat yet. Start the conversation!
                </div>
            )}

            {/* Marks the end of the chat */}
            <div ref={chatEndRef} />
        </div>
    )
}