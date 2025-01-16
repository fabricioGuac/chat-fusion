// Imports the decode function from the jwt-decode library
import { jwtDecode } from "jwt-decode";

// Defines the AuthService class for handling authentication related functions
class AuthService {
    constructor() {
        // In-memory storage for the JWT
        this.token = null; 
    }

    // Function to check if the user is logged in
    loggedIn() {
        const token = this.getToken(); // Retrieve token from memory or localStorage
        return !!token && !this.isTokenExpired(token);
    }

    // Function to check if the JWT is expired
    isTokenExpired(token) {
        try {
            const decoded = jwtDecode(token);
            // Compares the token expiration date in seconds with the current time in seconds
            return decoded.exp < Date.now() / 1000;
        } catch (err) {
            console.error("Error decoding token:", err);
            return true;
        }
    }

    // Function to get the JWT
    getToken() {
        if (!this.token) {
            this.token = localStorage.getItem("id_token"); 
        }
        return this.token;
    }

    // Fucntion to get the email from the JWT regardless of the expiration
    getEmail() {
        const token = this.getToken(); // Retrieve token from memory or localStorage
        try {
            const decoded = jwtDecode(token); // Decodes the JWT
            return decoded.email; // Returns the email
        } catch (error) {
            console.error("Error decoding token:", err);
            return null;
        }
    }

    // Function to log a user in
    login(idToken) {
        this.token = idToken;
        localStorage.setItem("id_token", idToken); // Sets to localStorage
        window.location.assign("/"); // Redirect to dashboard
    }

    // Function to log a user out
    logout() {
        this.token = null; // Clears from memory
        window.location.assign("/login"); // Redirect to login
        localStorage.removeItem("id_token"); // Clears from localStorage
    }
}

export default new AuthService();