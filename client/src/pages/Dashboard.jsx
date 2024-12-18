import { useEffect } from "react"
import { useNavigate } from "react-router-dom";
import SideBar from "../components/SideBar"
import auth from "../utils/auth"

export default function Dashboard() {
    const navigate = useNavigate();

    // Redirects the user to the login page if not logged in
    useEffect(() => {
        if (!auth.loggedIn()) {
            navigate('/login');
        }
    }, []);


    return (
        <>
            <SideBar />
        </>
    )
}
