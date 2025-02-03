import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./userSlice";
import chatsReducer from "./chatsSlice";

// Configures the Redux store with the `user` reducer
const store = configureStore({
    reducer: {
        // Handles user related state
        user: userReducer,
        // Handles chats related state
        chats: chatsReducer,
    },
});

export default store;
