import { useState, useEffect } from "react";
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';


export default function ServerDown({ children }) {
    // State variables for teh status and the retry timer
    const [isUp, setIsUp] = useState(null);
    const [retryCount, setRetryCount] = useState(30);

    // UseEffect to check the server status and modify the countdown
    useEffect(() => {
        // Checks the /actuator/health endpoint to verify server status
        const checkHealth = async () => {
            try {
                const res = await fetch(`${API_BASE_URL}/actuator/health`);
                const data = await res.json();
                setIsUp(data.status === "UP");
            } catch (err) {
                setIsUp(false);
            }
        };

        checkHealth();

        let interval;
        // Starts a countdown timer to retry health check every 30 seconds
        if (isUp === false) {
            interval = setInterval(() => {
                setRetryCount((prev) => {
                    if (prev === 0) {
                        checkHealth();
                        return 30;
                    }
                    return prev - 1;
                });
            }, 1000);
        }
        return () => clearInterval(interval);
    }, [isUp]);
    
    // TODO: Consider adding a simple minigame while render server starts up 

    // If server is up show a countdown until the next retry
    if (isUp === false) {
        return (
            <div className="bg-slate-950 h-screen flex justify-center items-center text-center">
            <div className="bg-slate-800 p-8 rounded-lg shadow-lg">
                <h1 className="text-slate-300 text-2xl mb-4">Sorry! the server is down at the moment.</h1>
                <p className="text-slate-400 text-lg mb-6">
                    Retrying in <span className="text-red-700">{retryCount}</span>
                </p>
            </div>
        </div>
        );
    }
    // If server is up, render the actual app
    return children;
}