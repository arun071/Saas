import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import api from "../services/api";
import { Plus, Briefcase, ChevronRight, Loader2, MoreVertical, Pencil, Trash2 } from "lucide-react";
import { DashboardLayout } from "../components/layout/DashboardLayout";

const WorkspacesPage = () => {
    const [workspaces, setWorkspaces] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [workspaceToEdit, setWorkspaceToEdit] = useState(null);
    const [newWorkspaceName, setNewWorkspaceName] = useState("");
    const [editName, setEditName] = useState("");

    const navigate = useNavigate();

    const fetchWorkspaces = async () => {
        try {
            const response = await api.get("/workspaces");
            setWorkspaces(response.data.content);
        } catch (err) {
            console.error("Failed to fetch workspaces", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchWorkspaces();
    }, []);

    const handleCreate = async (e) => {
        e.preventDefault();
        try {
            await api.post("/workspaces", { name: newWorkspaceName });
            setNewWorkspaceName("");
            setIsCreateModalOpen(false);
            fetchWorkspaces();
        } catch (err) {
            console.error("Failed to create workspace", err);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/workspaces/${workspaceToEdit.id}`, { name: editName });
            setIsEditModalOpen(false);
            setWorkspaceToEdit(null);
            fetchWorkspaces();
        } catch (err) {
            console.error("Failed to update workspace", err);
        }
    };

    const handleDelete = async (e, id) => {
        e.stopPropagation();
        if (!window.confirm("Are you sure? This will delete the workspace and all its projects/todos.")) return;
        try {
            await api.delete(`/workspaces/${id}`);
            fetchWorkspaces();
        } catch (err) {
            console.error("Failed to delete workspace", err);
        }
    };

    const openEditModal = (e, ws) => {
        e.stopPropagation();
        setWorkspaceToEdit(ws);
        setEditName(ws.name);
        setIsEditModalOpen(true);
    };

    return (
        <DashboardLayout>
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h2 className="text-2xl font-bold text-slate-900">Workspaces</h2>
                    <p className="text-slate-500 text-sm mt-1">Manage your organization's work environments</p>
                </div>
                <button
                    onClick={() => setIsCreateModalOpen(true)}
                    className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-xl font-bold text-sm hover:bg-indigo-700 transition-all shadow-md shadow-indigo-100"
                >
                    <Plus className="w-4 h-4" />
                    New Workspace
                </button>
            </div>

            {loading ? (
                <div className="flex items-center justify-center p-20">
                    <Loader2 className="w-8 h-8 text-indigo-500 animate-spin" />
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {workspaces.map((ws) => (
                        <div
                            key={ws.id}
                            onClick={() => navigate(`/workspaces/${ws.id}/projects`)}
                            className="group relative bg-white p-6 rounded-3xl border border-slate-100 shadow-sm hover:shadow-md hover:border-indigo-100 transition-all cursor-pointer"
                        >
                            <div className="flex items-start justify-between mb-4">
                                <div className="p-3 bg-indigo-50 text-indigo-600 rounded-2xl">
                                    <Briefcase className="w-6 h-6" />
                                </div>
                                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        onClick={(e) => openEditModal(e, ws)}
                                        className="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={(e) => handleDelete(e, ws.id)}
                                        className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                            <h3 className="text-lg font-bold text-slate-800">{ws.name}</h3>
                            <p className="text-xs text-slate-400 mt-2">Created {new Date(ws.createdAt).toLocaleDateString()}</p>
                        </div>
                    ))}
                    {workspaces.length === 0 && (
                        <div className="col-span-full py-20 text-center">
                            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-slate-100 text-slate-400 mb-4">
                                <Briefcase className="w-8 h-8" />
                            </div>
                            <h3 className="text-lg font-medium text-slate-600">No workspaces yet</h3>
                            <p className="text-slate-400 text-sm mt-1">Create your first workspace to start collaborating</p>
                        </div>
                    )}
                </div>
            )}

            {/* Create Modal */}
            {isCreateModalOpen && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-6">
                    <div className="bg-white rounded-3xl p-8 max-w-sm w-full shadow-2xl">
                        <h3 className="text-xl font-bold mb-6 text-slate-900">New Workspace</h3>
                        <form onSubmit={handleCreate} className="space-y-6">
                            <div>
                                <label className="block text-xs font-bold text-slate-700 uppercase mb-2">Workspace Name</label>
                                <input
                                    autoFocus
                                    type="text"
                                    required
                                    value={newWorkspaceName}
                                    onChange={(e) => setNewWorkspaceName(e.target.value)}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                                    placeholder="e.g. Engineering"
                                />
                            </div>
                            <div className="flex gap-3">
                                <button
                                    type="button"
                                    onClick={() => setIsCreateModalOpen(false)}
                                    className="flex-1 py-3 text-slate-600 font-bold text-sm hover:bg-slate-50 transition-all rounded-xl"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 py-3 bg-indigo-600 text-white rounded-xl font-bold text-sm hover:bg-indigo-700 shadow-lg shadow-indigo-100"
                                >
                                    Create
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Edit Modal */}
            {isEditModalOpen && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-6">
                    <div className="bg-white rounded-3xl p-8 max-w-sm w-full shadow-2xl">
                        <h3 className="text-xl font-bold mb-6 text-slate-900">Edit Workspace</h3>
                        <form onSubmit={handleUpdate} className="space-y-6">
                            <div>
                                <label className="block text-xs font-bold text-slate-700 uppercase mb-2">Workspace Name</label>
                                <input
                                    autoFocus
                                    type="text"
                                    required
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                                    placeholder="e.g. Engineering"
                                />
                            </div>
                            <div className="flex gap-3">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setIsEditModalOpen(false);
                                        setWorkspaceToEdit(null);
                                    }}
                                    className="flex-1 py-3 text-slate-600 font-bold text-sm hover:bg-slate-50 transition-all rounded-xl"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 py-3 bg-indigo-600 text-white rounded-xl font-bold text-sm hover:bg-indigo-700 shadow-lg shadow-indigo-100"
                                >
                                    Save Changes
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </DashboardLayout>
    );
};

export default WorkspacesPage;
