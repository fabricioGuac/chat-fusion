import { useState } from "react";
import { post } from "../utils/api";
import SearchUser from "./SearchUser";

export default function CreateGroup({ setSelectedView }) {
    // State variables to manage the members, the group name, and the image
    const [members, setMembers] = useState([]);
    const [groupName, setGroupName] = useState("");
    const [groupImage, setGroupImage] = useState(null);

    // Function to add a member to the list
    const handleAddMember = (user) => {
        if (!members.find((member) => member.id === user.id)) {
            setMembers([...members, user]);
        }
    };

    // Function to remove a member from the list
    const handleRemoveMember = (userId) => {
        setMembers(members.filter((member) => member.id !== userId));
    };

    // Function to create the group chat
    const handleCreateGroup = async (e) => {
        e.preventDefault();

        try {
            const memberIds = members.map((m) => m.id);

            // Prepare FormData object to send multipart form data
            const formData = new FormData();
            formData.append("userIds", JSON.stringify(memberIds)); // Add member IDs
            formData.append("chat_name", groupName); // Add group name
            if (groupImage) {
                formData.append("chat_image", groupImage); // Add the image file if it exists
            }

            // Send the FormData with the POST request
            const response = await post("/api/chats/group", formData);

            // Set the view to the newly created group chat passing the chat data
            setSelectedView({ type: "chat", data: response });
        } catch (error) {
            console.log("Error creating chat ", error);
        }
    };

    return (
        <div className="p-4">
            <SearchUser onSelectUser={handleAddMember} searchPlaceHolder="Find your group members" />

            {members.length > 0 && (
                <div className="mt-4">
                    <h3 className="font-semibold mb-2">Selected Members:</h3>
                    <ul className="flex flex-wrap gap-2">
                        {members.map((member) => (
                            <li
                                key={member.id}
                                className="bg-gray-200 px-3 py-1 rounded flex items-center gap-2"
                            >
                                {member.username}
                                <button
                                    onClick={() => handleRemoveMember(member.id)}
                                    className="text-red-500 font-bold"
                                >
                                    ×
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            <form onSubmit={handleCreateGroup} className="mt-4">
                <input
                    type="text"
                    placeholder="Group name"
                    required
                    onChange={(e) => setGroupName(e.target.value)}
                    className="border border-gray-300 rounded p-2 w-full mt-2"
                />

                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setGroupImage(e.target.files[0])}
                    className="mt-2"
                />

                <button
                    type="submit"
                    className="bg-blue-500 text-white py-1 px-4 rounded mt-4"
                >
                    Create Group
                </button>
            </form>
        </div>
    );
}
