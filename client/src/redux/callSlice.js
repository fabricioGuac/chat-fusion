import { createSlice } from '@reduxjs/toolkit';

// Slice for managing call-related state in redux
const callSlice = createSlice({
    name:'call', // Name of the slice
    initialState: {activeCallId: null }, // Initial state (no call)
    reducers: {
        // Sets the call data in the state
        setCall: (state, action) => {
            state.activeCallId = action.payload;
        },
        // Clears the call data
        clearCall: (state) => {
            state.activeCallId= null;
        },
    },
});

// Exports the actions for use in components 
export const {setCall, clearCall} = callSlice.actions;
// Exports the reducer to be included in the redux store
export default callSlice.reducer;