import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./userSlice";

// Configures the Redux store with the `user` reducer
const store = configureStore({
    reducer: {
        // Handles user related state
        user: userReducer,
    },
});

export default store;
