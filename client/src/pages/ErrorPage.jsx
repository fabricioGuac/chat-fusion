import { Link } from "react-router-dom";

export default function ErrorPage() {
    return (
        <div className="bg-slate-950 h-screen flex justify-center items-center text-center">
            <div className="bg-slate-800 p-8 rounded-lg shadow-lg">
                <h1 className="text-slate-300 text-2xl mb-4">Oops! Something went wrong.</h1>
                <p className="text-slate-400 text-lg mb-6">
                    The page you're looking for doesn't exist or there's an error.
                </p>
                <Link
                    to="/"
                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                >
                    Go Back to Home
                </Link>
            </div>
        </div>
    );
}
