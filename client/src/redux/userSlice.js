import { createSlice } from '@reduxjs/toolkit';

// Slice for managing user-related state in redux
const userSlice = createSlice({
    name:'user', // Name of the slice
    initialState: {user: null}, // Initila state (no user logged in)
    reducers: {
        // Sets the user data in the state
        setUser: (state, action) => {
            state.user = action.payload;
        },
        // Clears the user data
        clearUser: (state) => {
            state.user = null;
        },
    },
});

// Exports the actions for use in components 
export const {setUser, clearUser} = userSlice.actions;
// Exports the reducer to be included in the redux store
export default userSlice.reducer;