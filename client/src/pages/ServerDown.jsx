import { useState, useEffect, useRef } from "react";
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';


export default function ServerDown({ children }) {
    // State variables for teh status and the retry timer
    const [isUp, setIsUp] = useState(null);
    const [retryCount, setRetryCount] = useState(30);
    // UseReff for the interval
    const intervalRef = useRef(null);

    // Checks the /actuator/health endpoint to verify server status
    const checkHealth = async () => {
        try {
            const res = await fetch(`${API_BASE_URL}/actuator/health`);
            if (!res.ok) throw new Error("Server has not started yet");
            const data = await res.json();
            setIsUp(data.status === "UP");
        } catch (err) {
            console.error("Health check failed ", err);
            setIsUp(false);
        }
    };

    // Function to handle starting the interval
    const startRetryInterval = () => {
        intervalRef.current = setInterval(() => {
            setRetryCount((prev) => {
                if (prev === 0) { // Once it hits 0 we check the health again
                    checkHealth();
                    return 30;
                }
                return prev - 1;
            });
        }, 1000);
    }


    // Function to manually retry connections
    const handleRetryNow = () => {
        // Clears the interval canceling the timeout object created by it
        clearInterval(intervalRef.current);
        // Restarts the retry count
        setRetryCount(30);
        // Calls the health check function
        checkHealth();
        // Restart the interval
        startRetryInterval();
    }

    // UseEffect to check the server status
    useEffect(() => {
        checkHealth();
    }, []);

    // UseEffect to handle the interval
    useEffect(() => {
        if (isUp === false) {
            startRetryInterval()
        }
        // Clean up function 
        return () => clearInterval(intervalRef.current);
    }, [isUp]);

    // TODO: Consider adding a simple minigame while render server starts up 

    // While wew wait for a response from the server check we display a message letting the user know we are checking the status
    if (isUp === null) {
        return (
            <div className="bg-slate-950 h-screen flex justify-center items-center text-center">
                <div className="bg-slate-800 p-8 rounded-lg shadow-lg">
                    <h1 className="text-slate-300 text-2xl mb-4">Checking sever status, please wait a moment.</h1>
                </div>
            </div>
        );
    }

    // If server is down show a countdown until the next retry
    if (isUp === false) {
        return (
            <div className="bg-slate-950 h-screen flex justify-center items-center text-center">
                <div className="bg-slate-800 p-8 rounded-lg shadow-lg">
                    <h1 className="text-slate-300 text-2xl mb-4">Sorry! the server is down at the moment.</h1>
                    <p className="text-slate-400 text-lg mb-6">
                        Retrying in <span className="text-red-700">{retryCount}</span>
                    </p>
                    <button
                        onClick={handleRetryNow}
                        className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
                    >
                        Retry Now
                    </button>
                </div>
            </div>
        );
    }
    // If server is up, render the actual app
    return children;
}