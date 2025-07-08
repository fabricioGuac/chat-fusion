import { useState } from "react";
import { get } from "../utils/api";

export default function SearchUser({ onSelectUser, searchPlaceHolder }) {

    // State  variables for managing search query, results and loading
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);

    // Function to make the query from the api
    const handleSearch = async () => {
        // Returns early if the query only contains white space
        if (!query.trim()) {
            return;
        }
        // Enables loading indicator
        setLoading(true);

        try {
            // Makes the api call
            const response = await get(`/api/users/${query.trim()}`);
            // Updates the state variable to the fetched data
            setResults(response);

        } catch (error) {
            console.log("Failed to search users", error)
            setResults([]);
        } finally {
            // Disables loading indicator
            setLoading(false);
        }
    }



    return (
        <div className="p-4">
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    handleSearch();
                }}
                className="mb-2"
            >
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder={searchPlaceHolder}
                    className="border border-gray-300 rounded p-2 w-full mb-2"
                />
                <button
                    type="submit"
                    disabled={loading}
                    className="bg-blue-500 text-white py-1 px-4 rounded disabled:opacity-50"
                >
                    {loading ? "Searching..." : "Search"}
                </button>
            </form>


            {results.length > 0 && (
                <ul className="mt-2 border border-gray-200 rounded">
                    {results.map((user) => (
                        <li
                            key={user.id}
                            className="p-2 hover:bg-gray-100 cursor-pointer"
                            onClick={() => onSelectUser(user)}>
                            <img
                                src={user.pfp || '/logo192.png'}
                                alt={`${user.username}'s profile picture`}
                                className="w-10 h-10 rounded-full object-cover"
                            />
                            <span>
                                {user.username} email: {user.email}
                            </span>
                        </li>
                    ))
                    }
                </ul>
            )}
        </div>
    )
}