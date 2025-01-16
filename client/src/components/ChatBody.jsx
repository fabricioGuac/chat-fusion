import { useState, useEffect, useRef } from "react";
import { get } from "../utils/api";
import Message from "./Message";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";


export default function ChatBody({ chat, currentUser }) {
    // State variables for managing messages, and loading state
    const [messages, setMessages] = useState([]);

    const [loading, setLoading] = useState(false);

    // Reference to track the skipped messages
    const skipRef = useRef(0);
    // Reference for the chat body element
    const chatBodyRef = useRef(null);
    // Reference for the end of the chat
    const chatEndRef = useRef(null);

    // WebSocket client ref
    const stompClient = useRef(null);

    // Method to connect to WebSocket
    const connectWebSocket = () => {
        stompClient.current = new Client({
            // Uses SockJS as te websocket factory
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
            // Sets the reconnection attempt after 5 seconds
            reconnectDelay: 5000,
            // Debug log
            debug: (str) => console.log(str),
        });

        // Sets up subscriptions on successful connection
        stompClient.current.onConnect = () => {
            console.log("Connected to websocket");

            // Subscribes to the chat topic
            stompClient.current.subscribe(`/chat/${chat.id}`, (messageOutput) => {
                // Parses the message event
                const event = JSON.parse(messageOutput.body);

                // If it is a send event adds the new message to the chat
                if (event.type === "send") {
                    setMessages((prevMessages) => [...prevMessages, event.payload]);
                } else if (event.type === "edit") {
                    // Replaces the edited message in the chat
                    setMessages((prevMessages) =>
                        prevMessages.map((msg) => msg.id === event.payload.id ? event.payload : msg)
                    );
                } else if (event.type === "delete") {
                    // Removes the delete message from the chat
                    setMessages((prevMessages) => prevMessages.filter((msg) => msg.id !== event.payload));
                }
            });
        }

        // Handles stomp errors
        stompClient.current.onStompError = (frame) => {
            console.error("Broker reported error: " + frame.headers["message"]);
            console.error("Additinal details: " + frame.body);
        }

        // Initiates the connetion
        stompClient.current.activate();
    };





    // Increments the skip count when scrolled to the top
    const handleScroll = () => {
        if (chatBodyRef.current.scrollTop === 0) {
            skipRef.current += 100;
            console.log("Fetching messages");
            console.log(skipRef.current);
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
            setMessages((prevMessages) => [...response, ...prevMessages]);
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

        // Initiates the websocket connection
        connectWebSocket();

        // Cleanup on unmount
        return () => {
            if (stompClient.current) {
                // Disconnects if connected and stops auto reconnect loop
                stompClient.current.deactivate();
            }
        };

    }, [chat.id]);

    // Scrolls to bottom when chat changes or new messages arrive
    useEffect(() => {
        scrollToBottom();
    }, [chat.id, messages])

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