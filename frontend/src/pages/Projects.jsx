import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { Plus, Layout as LayoutIcon, ChevronRight, Loader2, Folder, Pencil, Trash2 } from "lucide-react";
import { DashboardLayout } from "../components/layout/DashboardLayout";

const ProjectsPage = () => {
    const { workspaceId } = useParams();
    const navigate = useNavigate();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [projectToEdit, setProjectToEdit] = useState(null);
    const [newProjectName, setNewProjectName] = useState("");
    const [editName, setEditName] = useState("");

    const fetchProjects = async () => {
        try {
            const response = await api.get(`/projects/workspace/${workspaceId}`);
            setProjects(response.data.content);
        } catch (err) {
            console.error("Failed to fetch projects", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (workspaceId) fetchProjects();
    }, [workspaceId]);

    const handleCreate = async (e) => {
        e.preventDefault();
        try {
            await api.post("/projects", {
                name: newProjectName,
                workspaceId: workspaceId
            });
            setNewProjectName("");
            setIsCreateModalOpen(false);
            fetchProjects();
        } catch (err) {
            console.error("Failed to create project", err);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/projects/${projectToEdit.id}`, { name: editName });
            setIsEditModalOpen(false);
            setProjectToEdit(null);
            fetchProjects();
        } catch (err) {
            console.error("Failed to update project", err);
        }
    };

    const handleDelete = async (e, id) => {
        e.stopPropagation();
        if (!window.confirm("Are you sure? This will delete the project and all its tasks.")) return;
        try {
            await api.delete(`/projects/${id}`);
            fetchProjects();
        } catch (err) {
            console.error("Failed to delete project", err);
        }
    };

    const openEditModal = (e, project) => {
        e.stopPropagation();
        setProjectToEdit(project);
        setEditName(project.name);
        setIsEditModalOpen(true);
    };

    return (
        <DashboardLayout>
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h2 className="text-2xl font-bold text-slate-900">Projects</h2>
                    <p className="text-slate-500 text-sm mt-1">Organize your work into manageable projects</p>
                </div>
                <button
                    onClick={() => setIsCreateModalOpen(true)}
                    className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-xl font-bold text-sm hover:bg-indigo-700 transition-all shadow-md shadow-indigo-100"
                >
                    <Plus className="w-4 h-4" />
                    New Project
                </button>
            </div>

            {loading ? (
                <div className="flex items-center justify-center p-20">
                    <Loader2 className="w-8 h-8 text-indigo-500 animate-spin" />
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {projects.map((project) => (
                        <div
                            key={project.id}
                            onClick={() => navigate(`/workspaces/${workspaceId}/projects/${project.id}/todos`)}
                            className="group relative bg-white p-6 rounded-3xl border border-slate-100 shadow-sm hover:shadow-md hover:border-indigo-100 transition-all cursor-pointer"
                        >
                            <div className="flex items-start justify-between mb-4">
                                <div className="p-3 bg-indigo-50 text-indigo-600 rounded-2xl">
                                    <LayoutIcon className="w-6 h-6" />
                                </div>
                                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        onClick={(e) => openEditModal(e, project)}
                                        className="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={(e) => handleDelete(e, project.id)}
                                        className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                            <h3 className="text-lg font-bold text-slate-800">{project.name}</h3>
                            <p className="text-xs text-slate-400 mt-2">Created {new Date(project.createdAt).toLocaleDateString()}</p>
                        </div>
                    ))}
                    {projects.length === 0 && (
                        <div className="col-span-full py-20 text-center bg-white border border-dashed border-slate-200 rounded-3xl">
                            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-slate-50 text-slate-300 mb-4">
                                <Folder className="w-8 h-8" />
                            </div>
                            <h3 className="text-lg font-medium text-slate-600">No projects yet</h3>
                            <p className="text-slate-400 text-sm mt-1">Create your first project to start tracking tasks</p>
                        </div>
                    )}
                </div>
            )}

            {/* Create Modal */}
            {isCreateModalOpen && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-6">
                    <div className="bg-white rounded-3xl p-8 max-w-sm w-full shadow-2xl">
                        <h3 className="text-xl font-bold mb-6 text-slate-900">New Project</h3>
                        <form onSubmit={handleCreate} className="space-y-6">
                            <div>
                                <label className="block text-xs font-bold text-slate-700 uppercase mb-2">Project Name</label>
                                <input
                                    autoFocus
                                    type="text"
                                    required
                                    value={newProjectName}
                                    onChange={(e) => setNewProjectName(e.target.value)}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                                    placeholder="e.g. Website Redesign"
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
                        <h3 className="text-xl font-bold mb-6 text-slate-900">Edit Project</h3>
                        <form onSubmit={handleUpdate} className="space-y-6">
                            <div>
                                <label className="block text-xs font-bold text-slate-700 uppercase mb-2">Project Name</label>
                                <input
                                    autoFocus
                                    type="text"
                                    required
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                                    placeholder="e.g. Website Redesign"
                                />
                            </div>
                            <div className="flex gap-3">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setIsEditModalOpen(false);
                                        setProjectToEdit(null);
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

export default ProjectsPage;
