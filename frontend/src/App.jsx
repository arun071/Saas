import { Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/Login";
import RegisterPage from "./pages/Register";
import WorkspacesPage from "./pages/Workspaces";
import ProjectsPage from "./pages/Projects";
import TodosPage from "./pages/Todos";

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

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

        <Route path="/" element={<Navigate to="/workspaces" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
