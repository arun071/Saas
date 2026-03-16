import { Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/Login";
import RegisterPage from "./pages/Register";
import WorkspacesPage from "./pages/Workspaces";
import ProjectsPage from "./pages/Projects";
import TodosPage from "./pages/Todos";
import OnboardingPage from "./pages/Onboarding";
import AdminOnboardingPage from "./pages/AdminOnboarding";

/**
 * Root component of the application.
 * Defines the main routing structure, including public and protected routes.
 * Wraps the application with AuthProvider for global authentication state.
 *
 * @returns {JSX.Element} The rendered App component.
 */
function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/onboarding" element={
          <ProtectedRoute>
            <OnboardingPage />
          </ProtectedRoute>
        } />

        <Route path="/workspaces" element={
          <ProtectedRoute>
            <WorkspacesPage />
          </ProtectedRoute>
        } />

        <Route path="/workspaces/:workspaceId/projects" element={
          <ProtectedRoute>
            <ProjectsPage />
          </ProtectedRoute>
        } />

        <Route path="/workspaces/:workspaceId/projects/:projectId/todos" element={
          <ProtectedRoute>
            <TodosPage />
          </ProtectedRoute>
        } />


        <Route path="/admin/onboarding" element={
          <ProtectedRoute>
            <AdminOnboardingPage />
          </ProtectedRoute>
        } />

        <Route path="/" element={<Navigate to="/workspaces" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
