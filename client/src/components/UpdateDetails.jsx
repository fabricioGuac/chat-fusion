import { put } from "../utils/api";
import { useState } from "react";

export default function UpdateDetails({ data, closeModal }) {
    // Destructures the data
    const { type, details } = data;

    // Field mapping to dinamically adjust form labels based on type
    const fieldMapping = {
        user: { title: "Username", image: "Profile Picture" },
        group: { title: "Group Name", image: "Group Image" },
    };

    // State to store the form data
    const [formData, setFormData] = useState({
        title: details.username || details.group_name,
        image: null,
    });

    // Handles submission for updating details
    const handleSubmit = async (e) => {
        e.preventDefault();
    
        // Creates a FormData object to send data
        const formDataToSend = new FormData();
        formDataToSend.append("name", formData.title);
        if (formData.image) {
            formDataToSend.append("pfp", formData.image);
        }
    
        console.log("Form Data:", formData);
    
        try {
            // Determines the API endpoint based on the type
            if (type === "user") {
                // Updates the user
                await put(`/api/users/update`, formDataToSend);
            } else {
                // Updates the group
                await put(`/api/chats/update/${details.id}`, formDataToSend);
                // Close modal after successful group update
                if (closeModal) closeModal();
            }
            console.log("Details updated successfully!");
        } catch (error) {
            console.log("Error updating details:", error);
        }
    };
    

    // Handles displaying image preview
    const imagePreview = formData.image
        // Shows the selected image preview
        ? URL.createObjectURL(formData.image)
        // Falls back to existing image if no new image is selected
        : details.pfp || details.group_image;

    return (
        <form
            className="p-4"
            onSubmit={handleSubmit}
        >
            <h1 className="text-xl font-bold mb-4">
                {/* Dynamic header based on type */}
                Update the details for {type === "user" ? "User" : "Group"}
            </h1>

            <div className="m-4">
                {/* Label for image input */}
                <label htmlFor="image" className="block font-bold mb-2">
                    {fieldMapping[type].image}
                </label>

                {/* Displays image preview if available */}
                {imagePreview && (
                    <div className="mb-4">
                        <img
                            src={imagePreview}
                            alt="Preview"
                            className="w-80 h-80 object-cover rounded-full mb-2"
                        />
                    </div>
                )}

                {/* Input for uploading an image */}
                <input
                    type="file"
                    name="image"
                    accept="image/*"
                    onChange={(e) =>
                        // Updates formdata with the selected image
                        setFormData({ ...formData, image: e.target.files[0] })
                    }
                    className="w-full border px-3 py-2 rounded"
                />
            </div>

            <div className="m-4">
                {/* Label and input for username or group name */}
                <label htmlFor="title" className="block font-bold mb-2">
                    {fieldMapping[type].title}
                </label>
                <input
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={(e) =>
                        // Updates formdata with the new title
                        setFormData({ ...formData, title: e.target.value })
                    }
                    className="w-full border px-3 py-2 rounded"
                />
            </div>

            <div className="flex justify-end gap-4">
                {/* Cancel button only shown if it is a modal */}
                {closeModal && (
                    <button
                        type="button"
                        onClick={closeModal}
                        className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
                    >
                        Cancel
                    </button>
                )}
                {/* Submit button to update details */}
                <button
                    type="submit"
                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                >
                    Update
                </button>
            </div>
        </form>
    );
}
