import { createSlice } from '@reduxjs/toolkit';

// Slice for managing chats-related state in redux
const chatsSlice = createSlice({
    name: 'chats', // Name of the slice
    initialState: { chats: [] }, // Initial state (no chats)
    reducers: {
        // Sets the chats data in the state
        setChats: (state, action) => {
            state.chats = action.payload;
        },

        // Adds new chat to the chats
        addChat: (state, action) => {
            state.chats.push(action.payload);
        },



        // Updates the chat details  (group chat name, profile picture) of an specific chat
        updateChat: (state, action) => {
            // Finds the index of the desired chat to update
            const index = state.chats.findIndex(chat => chat.id === action.payload.chatId);
            // Ensures the chat is present
            if (index !== -1) {
                // Updates the chat details of the chat in said index using a spread operator
                state.chats[index] = { ...state.chats[index], ...action.payload };
            }
        },

        // Adds a new member to an specific chat
        addMember: (state, action) => {
            // Gets the data of the user that is the new member and the chat that user is a new member of
            const { newMember, chatId } = action.payload;
            // Finds the index of the desired chat to update
            const index = state.chats.findIndex(chat => chat.id === chatId);
            // Ensures the chat is present
            if (index !== -1) {
                state.chats[index].members.push(newMember);
            }
        },

        // Grants a member of a chat admin priviledges
        addAdmin: (state, action) => {
            // Gets the id of the member to turn into an admin and the id of the chat to add it to from the payload
            const { newAdminId, chatId } = action.payload;
            // Finds the index of the desired chat to update
            const index = state.chats.findIndex(chat => chat.id === chatId);
            // Ensures the chat is present
            if (index !== -1) {
                state.chats[index].adminIds.push(newAdminId);
            }
        },

        // Updates the unread messages for the logged in user for the propper chat
        updateUnreadCounts: (state, action) => {

            // Gets the ids for the chat and user to be updated, and the update type
            const { chatId, userId, increase } = action.payload;
            // Finds the index of the desired chat to update
            const index = state.chats.findIndex(chat => chat.id === chatId);
            // Ensures the chat is present
            if (index !== -1) {
                // Increases the user's unread count by one or sets it to zero
                state.chats[index].unreadCounts[userId] = increase ? (state.chats[index].unreadCounts[userId] || 0) + 1 : 0
            }
        },

        // Removes a chat from the chats
        removeChat: (state, action) => {
            state.chats = state.chats.filter(chat => chat.id !== action.payload);
        },

        // Removes a member from the chat and if the member is the current user removes the chat from the chats
        removeMember: (state, action) => {
            // Gets the ids for the chat to be updated, the user to be removed and the current user
            const { chatId, removedUserId } = action.payload;
            // Finds the index of the desired chat to update
            const index = state.chats.findIndex(chat => chat.id === chatId);

            // Ensures the chat is present
            if (index !== -1) {
                // Removes the user from the members
                state.chats[index].members = state.chats[index].members.filter(member => member.id !== removedUserId);
            }
        },

        // Clears the chats data (I dont know if I will use this one)
        clearChats: (state) => {
            state.chats = [];
        },
    },
});

export const { setChats, addChat, updateChat, updateUnreadCounts, addMember, addAdmin, removeChat, removeMember } = chatsSlice.actions;

export default chatsSlice.reducer;