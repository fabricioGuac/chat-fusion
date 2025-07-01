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

            // Prepare FormData object to send multipart form data
            const formData = new FormData();
            // Adds each user ID individually
            members.forEach((member, index) => {
                formData.append(`userIds[${index}]`, member.id); // Append each member ID
            });

            formData.append("chat_name", groupName); // Add group name
            if (groupImage) {
                formData.append("chat_image", groupImage); // Add the image file if it exists
            }

            // Send the FormData with the POST request
            const response = await post("/api/chats/group", formData);

            // Set the view to the newly created group chat passing the chat data
            setSelectedView({ type: "chat", data: { chat: response } });
        } catch (error) {
            console.log("Error creating chat ", error);
        }
    };

    return (
        <div className="p-4">
            <form className="mt-4 p-4">
                <input
                    type="text"
                    placeholder="Group name"
                    required
                    onChange={(e) => setGroupName(e.target.value)}
                    className="border border-gray-300 rounded p-2 w-full mt-2"
                />

                {/* <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setGroupImage(e.target.files[0])}
                    className="mt-2"
                /> */}

                <label className="block mt-2 cursor-pointer bg-blue-100 border border-blue-300 text-blue-700 px-4 py-1 rounded hover:bg-blue-200">
                    Choose Group Image
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setGroupImage(e.target.files[0])}
                        className="hidden"
                    />
                </label>


                {groupImage && (
                    <img src={URL.createObjectURL(groupImage)}
                        alt="preview"
                        className="w-80 h-80 object-cover rounded-full mt-1" />
                )}
            </form>

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
            <div className="mt-6">
                <button
                    onClick={handleCreateGroup}
                    className="bg-blue-500 text-white py-2 px-6 rounded w-full"
                >
                    Create Group
                </button>
            </div>
        </div>
    );
}
