import { useState, useRef } from "react";
import { post } from "../utils/api";

export default function ({ chatId }) {
    // State variables to manage input fields
    const [message, setMessage] = useState("");
    const [file, setFile] = useState(null);
    const [audioBlob, setAudioBlob] = useState(null);
    const [isRecording, setIsRecording] = useState(false);
    // References for managing the media recorder and audio chunks during recording
    const mediaRecorderRef = useRef(null);
    const audioChunks = useRef([]);


    // Handles changes to the file input 
    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);
        }
    }

    // Starts recording audio using the MediaRecorder API
    const startRecording = async () => {
        try {
            // Requests access to the user's microphone
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            // Initializes a new mMediaRecorder intance with the audio stream
            mediaRecorderRef.current = new MediaRecorder(stream);
            // Pushes recorded audio data into the audioChunks array
            mediaRecorderRef.current.ondataavailable = (e) => {
                audioChunks.current.push(e.data);
            };

            // Handles the finalization of the recording
            mediaRecorderRef.current.onstop = () => {
                // Combines the audio chunks into a single blob
                const audio = new Blob(audioChunks.current, { type: "audio/webm" });
                // Resets the audioChunks for future recordings
                audioChunks.current = [];
                // Saves the audio blob to the state
                setAudioBlob(audio);
                // Sets the is recording flag to false
                setIsRecording(false);
            };

            // Starts recording
            mediaRecorderRef.current.start();
            // Sets the is recording flag to true
            setIsRecording(true);
        } catch (error) {
            console.log("Failed to start recording", error);
        }
    };

    // Stops the ongoing audio recording
    const stopRecording = async () => {
        // Checks if the mediaRecorder is still active
        if (mediaRecorderRef.current) {
            // Stops the recording process
            mediaRecorderRef.current.stop();
            // Retrieves the media tracks (audio tracks in this case) from the MediaRecorder's stream
            const tracks = mediaRecorderRef.current.stream.getTracks();
            // Stops each audio track to release the microphone and stop the browser's recording indicator
            tracks.forEach(track => track.stop());
        }
    };

    // Determines the type of the uploaded file based  on it's MIME type
    const getFileType = (file) => {
        const mimeType = file.type;
        if (mimeType.startsWith("image")) {
            return "image";
        }

        if (mimeType.startsWith("video")) {
            return "video";
        }

        if (mimeType.startsWith("audio")) {
            return "audio";
        }
        // Treats all other files as generic files
        return "file";
    };

    // Hanldes sending the message, audio or file to the server
    const handleSend = async () => {
        // Returns early if there is no content to send
        if (!message && !file && !audioBlob) {
            return;
        }

        // Prepares the data to send using FormData for file uploads
        const formData = new FormData();
        // Appends the hat ID to the form
        formData.append("chatId", chatId);

        // Appends the appropiate data based on the type
        if (message) {
            // Adds the message as the content
            formData.append("content", message);
        } else if (audioBlob) {
            // Adds the audio as the file
            formData.append("file", audioBlob);
            // Specifies the type as audio
            formData.append("type", "audio");
        } else if (file) {
            // Adds the file as the file
            formData.append("file", file);
            // Gets the type dinamially with the getFileType method
            formData.append("type", getFileType(file));
        }




        try {
            // Log all FormData entries
            for (let [key, value] of formData.entries()) {
                console.log(`${key}:`, value);
            }
            // Sends the data to the server
            await post("/api/messages/send", formData);
            // Resets the state after a successful send
            setMessage("");
            setFile(null);
            setAudioBlob(null);
        } catch (error) {
            console.log("Failed to send message", error);
        }
    };

    return (
        <div className="flex flex-col items-center p-4 bg-white w-full mx-auto">
            {/* Form to handle submit on Enter */}
            <form
                onSubmit={(e) => {
                    e.preventDefault(); // Prevent the default form submission
                    handleSend(); // Call the send handler
                }}
                className="w-full"
            >
                {/* Message input and send button */}
                <div className="flex items-center w-full gap-2 mb-3">
                    <input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        className="flex-grow p-2 border rounded-md bg-white text-gray-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Type your message..."
                    />

                    <div className="flex flex-col gap-2">
                        <button
                            type="submit"
                            className={`px-4 py-2 rounded-md text-white ${!message && !file && !audioBlob
                                ? "bg-gray-300 cursor-not-allowed"
                                : "bg-blue-500 hover:bg-blue-600"
                                }`}
                            disabled={!message && !file && !audioBlob}
                        >
                            {audioBlob ? "Send Audio" : file ? "Send Media" : "Send Message"}
                        </button>

                        <button
                            type="button"
                            onClick={isRecording ? stopRecording : startRecording}
                            className={`px-4 py-2 rounded-md text-white ${isRecording
                                ? "bg-red-500 hover:bg-red-600"
                                : "bg-yellow-500 hover:bg-yellow-600"
                                } focus:outline-none focus:ring-2`}
                        >
                            {isRecording ? "Stop" : "Record"}
                        </button>
                    </div>
                </div>

                {/* Displays the selected file or a message indicating the audio recording is ready */}
                {(file || audioBlob) && (
                    <div className="w-full text-sm text-gray-600 mb-3">
                        {file && <p>{file.name}</p>}
                        {audioBlob && <p>{"Audio ready"}</p>}
                    </div>
                )}

                {/* File upload input */}
                <input
                    type="file"
                    onChange={handleFileChange}
                    accept="image/*,video/*,audio/*"
                    className="w-full p-2 mb-3 text-sm text-gray-600 border rounded-md cursor-pointer bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </form>
        </div>
    );
}