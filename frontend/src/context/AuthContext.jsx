import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

/**
 * Context for managing user authentication state across the application.
 */
const AuthContext = createContext();

/**
 * Provider component that wraps the app and provides auth state and methods.
 * @param {Object} props - React props.
 * @param {React.ReactNode} props.children - Child components to be rendered.
 * @returns {JSX.Element} The provider component.
 */
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    /**
     * Persists authentication data to localStorage and redirects based on onboarding status.
     * @param {string} token - JWT token from backend.
     * @param {Object} userData - User profile data.
     * @private
     */
    const _persistAndRedirect = (token, userData) => {
        localStorage.setItem("token", token);
        localStorage.setItem("user", JSON.stringify(userData));
        setUser(userData);
        if (!userData.organizationId) {
            navigate("/onboarding");
        } else {
            navigate("/workspaces");
        }
    };

    /**
     * Authenticates user via email and password.
     * @param {string} email - User's email.
     * @param {string} password - User's password.
     */
    const login = async (email, password) => {
        const response = await api.post("/auth/login", { email, password });
        const { token, ...userData } = response.data;
        _persistAndRedirect(token, userData);
    };

    /**
     * Registers a new user and organization.
     * @param {Object} data - Registration payload.
     */
    const register = async (data) => {
        const response = await api.post("/auth/register", data);
        const { token, ...userData } = response.data;
        _persistAndRedirect(token, userData);
    };

    /**
     * Authenticates user using Google OAuth credential.
     * @param {string} credential - JWT token from Google.
     */
    const googleLogin = async (credential) => {
        const response = await api.post("/auth/google", { token: credential });
        const { token, ...userData } = response.data;
        _persistAndRedirect(token, userData);
    };

    /**
     * Logs out the current user and clears local session.
     */
    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUser(null);
        navigate("/login");
    };

    return (
        <AuthContext.Provider value={{ user, setUser, login, register, googleLogin, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

/**
 * Hook to access the current authentication context.
 * @returns {Object} User state and auth methods.
 */
export const useAuth = () => useContext(AuthContext);
