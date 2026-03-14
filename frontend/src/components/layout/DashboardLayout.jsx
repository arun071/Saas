import { useState, useEffect } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";
import { LogOut, Layout as LayoutIcon, CheckSquare, Briefcase, ChevronRight, Hash, Folder } from "lucide-react";

export const DashboardLayout = ({ children }) => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const { workspaceId, projectId } = useParams();
    const location = useLocation();

    const [workspaces, setWorkspaces] = useState([]);
    const [activeWorkspaceProjects, setActiveWorkspaceProjects] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchWorkspaces = async () => {
            try {
                const res = await api.get("/workspaces");
                setWorkspaces(res.data.content);
            } catch (err) {
                console.error("Sidebar: failed to fetch workspaces", err);
            }
        };
        fetchWorkspaces();
    }, []);

    useEffect(() => {
        const fetchProjects = async () => {
            if (!workspaceId) {
                setActiveWorkspaceProjects([]);
                return;
            }
            try {
                const res = await api.get(`/projects/workspace/${workspaceId}`);
                setActiveWorkspaceProjects(res.data.content);
            } catch (err) {
                console.error("Sidebar: failed to fetch projects", err);
            }
        };
        fetchProjects();
    }, [workspaceId]);

    const activeWorkspace = workspaces.find(w => w.id === workspaceId);

    return (
        <div className="flex h-screen bg-slate-50 font-sans text-slate-900">
            {/* Sidebar */}
            <aside className="w-64 bg-white border-r border-slate-200 flex flex-col">
                <div className="p-6 border-b border-slate-100">
                    <h1 className="text-xl font-bold text-indigo-600 flex items-center gap-2 cursor-pointer" onClick={() => navigate("/workspaces")}>
                        <CheckSquare className="w-6 h-6" />
                        SaaS Todo
                    </h1>
                    <p className="text-[10px] text-slate-500 mt-1 uppercase tracking-widest font-bold">
                        {user?.organizationName || "Your Organization"}
                    </p>
                </div>

                <nav className="flex-1 overflow-y-auto p-4 space-y-6">
                    {/* Workspaces Section */}
                    <div>
                        <div className="flex items-center justify-between px-3 mb-2">
                            <h3 className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Workspaces</h3>
                        </div>
                        <div className="space-y-1">
                            {workspaces.map((ws) => (
                                <button
                                    key={ws.id}
                                    onClick={() => navigate(`/workspaces/${ws.id}/projects`)}
                                    className={`w-full flex items-center gap-3 px-3 py-2 rounded-xl text-sm transition-all ${ws.id === workspaceId
                                        ? "bg-indigo-50 text-indigo-700 font-bold"
                                        : "text-slate-600 hover:bg-slate-50"
                                        }`}
                                >
                                    <Briefcase className={`w-4 h-4 ${ws.id === workspaceId ? "text-indigo-600" : "text-slate-400"}`} />
                                    <span className="truncate">{ws.name}</span>
                                </button>
                            ))}
                        </div>
                    </div>


                    {/* Active Workspace / Projects Section */}
                    {workspaceId && (
                        <div>
                            <div className="flex items-center justify-between px-3 mb-2">
                                <h3 className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                                    {activeWorkspace?.name || "Projects"}
                                </h3>
                            </div>
                            <div className="space-y-1">
                                {activeWorkspaceProjects.map((p) => (
                                    <button
                                        key={p.id}
                                        onClick={() => navigate(`/workspaces/${workspaceId}/projects/${p.id}/todos`)}
                                        className={`w-full flex items-center gap-3 px-3 py-2 rounded-xl text-sm transition-all ${p.id === projectId
                                            ? "bg-slate-100 text-slate-900 font-bold"
                                            : "text-slate-500 hover:bg-slate-50"
                                            }`}
                                    >
                                        <Folder className={`w-4 h-4 ${p.id === projectId ? "text-slate-700" : "text-slate-300"}`} />
                                        <span className="truncate">{p.name}</span>
                                    </button>
                                ))}
                                {activeWorkspaceProjects.length === 0 && (
                                    <p className="text-[10px] text-slate-400 px-3 italic">No projects yet</p>
                                )}
                            </div>
                        </div>
                    )}
                </nav>

                <div className="p-4 border-t border-slate-100">
                    <div className="flex items-center gap-3 px-3 mb-4">
                        <div className="w-8 h-8 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-xs">
                            {user?.name?.[0]}
                        </div>
                        <div className="flex-1 overflow-hidden">
                            <p className="text-xs font-bold text-slate-800 truncate">{user?.name}</p>
                            <p className="text-[10px] text-slate-500 truncate">{user?.email}</p>
                        </div>
                    </div>
                    <button
                        onClick={logout}
                        className="w-full flex items-center gap-3 px-3 py-2 rounded-xl text-red-500 hover:bg-red-50 transition-all font-medium text-sm"
                    >
                        <LogOut className="w-4 h-4" />
                        <span>Sign Out</span>
                    </button>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-y-auto">
                <div className="max-w-6xl mx-auto p-6 md:p-10">
                    {children}
                </div>
            </main>
        </div>
    );
};
