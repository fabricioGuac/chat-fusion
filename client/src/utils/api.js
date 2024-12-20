import auth from "./auth";

// Function for making api calls with authentication
const fetchApi = async (url, options = {}) => {
    // Retrieves the JWT token from the auth service
    const token = auth.getToken();
    // Defines the default headers including the Authorization header with the token
    const headers = {
        Authorization: `Bearer ${token}`, // Adds the Bearer token for authorization
        "Content-Type": "application/json", // Ensures JSON format for request payload
        ...options.headers, // Merges any additional headers provided in the options
    };

    // Makes the API call with the provided URL and options
    const response = await fetch(url, { ...options, headers });
    // Throws an error if the response status is not OK
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    // Parses and return the response as JSON
    return response.json();
};

// A wrapper function for GET requests
export const get = async (url) => fetchApi(url, { method: "GET" });

// A wrapper function for POST requests including a JSON body
export const post = async (url, body) =>
    fetchApi(url, {
        method: "POST",
        body: JSON.stringify(body), // Converts the body object to a JSON string
    });

// A wrapper function for PUT requests including a JSON body
export const put = async (url, body) =>
    fetchApi(url, {
        method: "PUT",
        body: JSON.stringify(body), // Converts the body object to a JSON string
    });

// A wrapper function for DELETE requests
export const del = async (url) => fetchApi(url, { method: "DELETE" });
