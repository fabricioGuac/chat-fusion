import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom";
import auth from "../utils/auth";
import SideBar from "../components/SideBar";
import UpdateDetails from "../components/UpdateDetails";
import Chat from "../components/Chat";
import SearchUser from "../components/SearchUser";
import CreateGroup from "../components/CreateGroup";
import CreateSingleChat from "../components/CreateSingleChat";


export default function Dashboard() {
    // State to manage the selected view
    const [selectedView, setSelectedView] = useState({ type: null, data: null });
    const navigate = useNavigate();

    // Redirects the user to the login page if not logged in
    useEffect(() => {
        if (!auth.loggedIn()) {
            navigate('/login');
        }
    }, []);

    // Dynamically render the content based on `selectedView`
    const renderContent = () => {
        switch (selectedView.type) {
            case "chat":
                return <Chat chat={selectedView.data} /> ;
            case "single-create":
                return <CreateSingleChat setSelectedView={setSelectedView} /> ;
            case "group-create":
                return <CreateGroup setSelectedView={setSelectedView} /> ;
            case "update-details":
                return <UpdateDetails data={selectedView.data} />;
            default:
                return <h2 className="flex justify-center items-center h-screen text-5xl">Select a chat or create a group!</h2>;
        }
    };


    return (
        <div className="flex h-screen">
            <SideBar onSelectView={setSelectedView} />
            <main className="flex-1 overflow-y-auto bg-white">
                {renderContent()}
            </main>
        </div>
    )
}
