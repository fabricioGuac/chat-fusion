import auth from "./auth";
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

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
    const response = await fetch(`${API_BASE_URL}${url}`, { ...options, headers });
    // Throws an error if the response status is not OK
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    // Parses and return the response as JSON
    return response.json();
};

// A wrapper function for GET requests
export const get = async (url) => fetchApi(url, { method: "GET" });

// A wrapper function for POST requests including a JSON body
export const post = async (url, body) => {
    // Check if the body is an instance of FormData
    const isFormData = body instanceof FormData;

    return fetchApi(url, {
        method: "POST",
        body: isFormData ? body : JSON.stringify(body), 
    });
}

// A wrapper function for PUT requests including a JSON body
export const put = async (url, body) => {
    // Check if the body is an instance of FormData
    const isFormData = body instanceof FormData;

    fetchApi(url, {
        method: "PUT",
        body: isFormData ? body :JSON.stringify(body), // Use FormData directly or JSON stringify
    });
}

// A wrapper function for DELETE requests
export const del = async (url) => fetchApi(url, { method: "DELETE" });
