import auth from "./auth";

// Function for making api calls with authentication
const fetchApi = async (url, options = {}) => {
    // Retrieves the JWT token from the auth service
    const token = auth.getToken();
    
    // Check if the body is an instance of FormData, which requires multipart/form-data
    const headers = {
        Authorization: `Bearer ${token}`,
        ...options.headers, // Merges any additional headers provided in the options
    };

    // If body is FormData, we don't set Content-Type (it will be auto-set)
    if (options.body instanceof FormData) {
        delete headers['Content-Type']; // FormData will set the correct Content-Type automatically
    } else {
        headers["Content-Type"] = "application/json"; // For other cases, use JSON
    }

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
