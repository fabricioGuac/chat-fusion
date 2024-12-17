// Imports the decode function from the jwt-decode library
import { jwtDecode } from "jwt-decode";

// Defines the AuthService class for handling authentication related functions
class AuthService {

    // Fucntion to check if the user is logged in
    loggedIn() {
        // Retrieves token from local storage
        const token = this.getToken();
        // Convert the token to its boolean value and returns true if the token exists and is not expired
        return !!token && !this.isTokenExpired(token); 
    }

    // Fucntion to check if the jwt is expired
    isTokenExpired(token) {
        try {
            // Decodes the token
            const decoded = jwtDecode(token);
            // Compares the token expiration date in seconds with the current time in seconds
            if (decoded.exp < Date.now() / 1000) {
                return true;
            } else return false;
        } catch (err) {
            return false;
        }
    }

    // Fucntion to get the jwt from local storage
    getToken() {
        return localStorage.getItem('id_token');
    }

    // Function to log an user in
    login(idToken) {
        // save user token to localStorage
        localStorage.setItem('id_token', idToken);
        // this will reload the page and send the user to the dashboard page
        window.location.assign('/dashboard');
    }

    // Function to log an user out
    logout() {
        // Clears user token and profile data from localStorage
        localStorage.removeItem('id_token');
        // this will reload the page and reset the state of the application
        window.location.assign('/');
    }
}

export default new AuthService();