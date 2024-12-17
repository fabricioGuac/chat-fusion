import { useState } from "react";
import { Link } from "react-router-dom";

import auth from "../utils/auth";

export default function Login() {
    // State variables
    const [form, setForm] = useState({ email: "", password: "" });
    const [errorMessage, setErrorMessage] = useState("");
    const [emailValid, setEmailValid] = useState(true);

    // Input change handler
    const handleInputChange = (e) => {
        const { id, value } = e.target;
        setForm({ ...form, [id]: value });
        if(id === "email") {
            setEmailValid(true);
        }
    };

    // Email validation handler
    const handleEmailBlur = () => {
        const regex =/^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        setEmailValid(regex.test(form.email));
    };

    // Form submit handler
    const handleSubmit = async (e) => {
        e.preventDefault();

        if ( !form.email || !form.password) {
            setErrorMessage("All fields are required.");
            return;
        }

        if (!emailValid) {
            setErrorMessage("Please enter a valid email address.");
            return;
        }

        try {
            // Sends login request to the backend
            const response = await fetch('/auth/login', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    email: form.email,
                    password: form.password,
                }),
            });
        
            // Checks if the response is successful
            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.jwt || "An error occurred. Please try again.");
                return;
            }
        
            // Parses the response data and store JWT
            const data = await response.json();
            // Stores JWT in local storage and redirects to dashboard
            auth.login(data.jwt);
        
        } catch (error) {
            console.log(error);
            setErrorMessage("An error occurred. Please try again.");
        }
        
    };

    

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md p-6 bg-white rounded-lg shadow-lg">
                <h1 className="text-center text-4xl font-semibold text-teal-700 mb-6">Login</h1>

                <form onSubmit={handleSubmit}>

                    <div className="mb-4">
                        <label htmlFor="email" className="block text-gray-700 font-medium">Email</label>
                        <input
                            type="email"
                            id="email"
                            value={form.email}
                            onChange={handleInputChange}
                            onBlur={handleEmailBlur}
                            className={`w-full p-3 mt-1 border ${emailValid ? 'border-gray-300' : 'border-red-500'} rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500`}
                            required
                        />
                        {!emailValid  && <p className="text-red-500 text-sm mt-2">Invalid email address.</p>}
                    </div>

                    <div className="mb-6">
                        <label htmlFor="password" className="block text-gray-700 font-medium">Password</label>
                        <input
                            type="password"
                            id="password"
                            value={form.password}
                            onChange={handleInputChange}
                            className="w-full p-3 mt-1 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                            required
                        />
                    </div>

                    {errorMessage && <p className="text-red-500 mb-4 text-center">{errorMessage}</p>}

                    <button
                        type="submit"
                        className="w-full bg-teal-700 text-white py-3 px-4 rounded-lg hover:bg-teal-800 transition duration-300"
                    >
                        Sign Up
                    </button>
                </form>
                <Link
                    to="/signup"
                    className="text-sm hover:underline"
                >
                    Are you new here? Sign up
                </Link>
            </div>
        </div>
    );
}
