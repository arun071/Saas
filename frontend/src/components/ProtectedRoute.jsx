import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Higher-order component representing a route that requires authentication.
 * Redirects unauthenticated users to the login page.
 * 
 * @param {Object} props - Component props.
 * @param {React.ReactNode} props.children - Components to render if authenticated.
 */
const ProtectedRoute = ({ children }) => {
    const { user } = useAuth();
    if (!user) {
        return <Navigate to="/login" />;
    }
    return children;
};

export default ProtectedRoute;
