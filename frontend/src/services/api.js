import axios from "axios";

/**
 * Axios instance configured for the SaaS backend.
 */
const api = axios.create({
    baseURL: "http://localhost:8080",
});

/**
 * Request interceptor to attach JWT token from localStorage to every request.
 */
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

/**
 * Response interceptor to handle session expiration (401 Unauthorized).
 * Redirects user to login page if the token is invalid or expired.
 */
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            // Only redirect if not already on auth pages
            if (!window.location.pathname.includes("/login") &&
                !window.location.pathname.includes("/register")) {
                window.location.href = "/login";
            }
        }
        return Promise.reject(error);
    }
);

export default api;
