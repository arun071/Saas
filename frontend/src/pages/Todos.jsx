import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";
import { Plus, CheckSquare, Search, Filter, Clock, MoreVertical, CheckCircle2, Circle, Loader2, Pencil, Trash2 } from "lucide-react";
import { DashboardLayout } from "../components/layout/DashboardLayout";

/**
 * TodosPage component
 * Displays and manages tasks (todos) within a specific project.
 * Provides functionality to create, update, delete, and toggle the status of tasks.
 *
 * @returns {JSX.Element} The rendered TodosPage component.
 */
const TodosPage = () => {
    const { projectId } = useParams();
    const [todos, setTodos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [todoToEdit, setTodoToEdit] = useState(null);

    const [newTodo, setNewTodo] = useState({
        title: "",
        description: "",
        priority: "MEDIUM",
        status: "TODO"
    });

    const [editTodo, setEditTodo] = useState({
        title: "",
        description: "",
        priority: "MEDIUM",
        status: "TODO"
    });

    /**
     * Fetches todos associated with the current project.
     */
    const fetchTodos = async () => {
        try {
            const response = await api.get("/todos", { params: { projectId } });
            setTodos(response.data.content);
        } catch (err) {
            console.error("Fetch failed", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (projectId) fetchTodos();
    }, [projectId]);

    /**
     * Handles the creation of a new todo task.
     *
     * @param {Event} e - The form submission event.
     */
    const handleCreateTodo = async (e) => {
        e.preventDefault();
        try {
            await api.post("/todos", { ...newTodo, projectId });
            setIsCreateModalOpen(false);
            setNewTodo({ title: "", description: "", priority: "MEDIUM", status: "TODO" });
            fetchTodos();
        } catch (err) {
            console.error("Create failed", err);
        }
    };

    /**
     * Handles the update of an existing todo task.
     *
     * @param {Event} e - The form submission event.
     */
    const handleUpdateTodo = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/todos/${todoToEdit.id}`, { ...editTodo, projectId });
            setIsEditModalOpen(false);
            setTodoToEdit(null);
            fetchTodos();
        } catch (err) {
            console.error("Update failed", err);
        }
    };

    /**
     * Handles the deletion of a todo task.
     * Confirms deletion with the user before proceeding.
     *
     * @param {string} id - The ID of the todo task to delete.
     */
    const handleDeleteTodo = async (id) => {
        if (!window.confirm("Delete this task?")) return;
        try {
            await api.delete(`/todos/${id}`);
            fetchTodos();
        } catch (err) {
            console.error("Delete failed", err);
        }
    };

    /**
     * Toggles the status of a todo task between 'DONE' and 'TODO'.
     *
     * @param {Object} todo - The todo task object to toggle.
     */
    const toggleStatus = async (todo) => {
        const newStatus = todo.status === "DONE" ? "TODO" : "DONE";
        try {
            await api.put(`/todos/${todo.id}/status?status=${newStatus}`);
            fetchTodos();
        } catch (err) {
            console.error("Update failed", err);
        }
    };

    /**
     * Opens the edit modal for a specific todo task.
     *
     * @param {Object} todo - The todo task object to edit.
     */
    const openEditModal = (todo) => {
        setTodoToEdit(todo);
        setEditTodo({
            title: todo.title,
            description: todo.description,
            priority: todo.priority,
            status: todo.status
        });
        setIsEditModalOpen(true);
    };

    return (
        <DashboardLayout>
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
                <div>
                    <h2 className="text-2xl font-bold text-slate-900">Task Board</h2>
                    <p className="text-slate-500 text-sm mt-1">Manage and track your project's progress</p>
                </div>
                <button
                    onClick={() => setIsCreateModalOpen(true)}
                    className="flex items-center justify-center gap-2 px-6 py-2.5 bg-indigo-600 text-white rounded-2xl font-bold text-sm hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-100"
                >
                    <Plus className="w-4 h-4" />
                    Add New Task
                </button>
            </div>

            {/* Filters */}
            <div className="bg-white p-4 rounded-3xl border border-slate-100 mb-8 flex flex-wrap gap-4 items-center">
                <div className="flex-1 min-w-[200px] relative">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search tasks..."
                        className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-indigo-500/10 transition-all"
                    />
                </div>
                <div className="flex items-center gap-2 px-4 py-2 bg-slate-50 border border-slate-100 rounded-xl text-slate-600 cursor-pointer hover:bg-slate-100 transition-all">
                    <Filter className="w-4 h-4" />
                    <span className="text-sm font-medium">Filters</span>
                </div>
            </div>

            {/* Tasks List */}
            {loading ? (
                <div className="flex items-center justify-center p-20">
                    <Loader2 className="w-8 h-8 text-indigo-500 animate-spin" />
                </div>
            ) : (
                <div className="grid grid-cols-1 gap-4">
                    {todos.map((todo) => (
                        <div
                            key={todo.id}
                            className="group bg-white p-5 rounded-3xl border border-slate-100 shadow-sm hover:border-indigo-100 transition-all flex items-center gap-4"
                        >
                            <button
                                onClick={() => toggleStatus(todo)}
                                className={`w-6 h-6 rounded-full flex items-center justify-center border-2 transition-all ${todo.status === 'DONE'
                                    ? 'bg-indigo-600 border-indigo-600 text-white'
                                    : 'border-slate-200 text-transparent hover:border-indigo-400'
                                    }`}
                            >
                                {todo.status === 'DONE' ? <CheckCircle2 className="w-4 h-4" /> : <Circle className="w-4 h-4" />}
                            </button>

                            <div className="flex-1">
                                <h4 className={`font-bold text-sm ${todo.status === 'DONE' ? 'text-slate-400 line-through' : 'text-slate-800'}`}>
                                    {todo.title}
                                </h4>
                                <p className="text-xs text-slate-400 mt-1 line-clamp-1">{todo.description}</p>
                            </div>

                            <div className="flex items-center gap-2">
                                <span className={`px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider ${todo.priority === 'HIGH' ? 'bg-red-50 text-red-600' :
                                    todo.priority === 'MEDIUM' ? 'bg-amber-50 text-amber-600' :
                                        'bg-emerald-50 text-emerald-600'
                                    }`}>
                                    {todo.priority}
                                </span>
                                <div className="hidden sm:flex items-center gap-1.5 text-slate-400 text-xs px-3 py-1 bg-slate-50 rounded-lg whitespace-nowrap">
                                    <Clock className="w-3.5 h-3.5" />
                                    <span>{new Date(todo.createdAt).toLocaleDateString()}</span>
                                </div>
                                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        onClick={() => openEditModal(todo)}
                                        className="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => handleDeleteTodo(todo.id)}
                                        className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                    {todos.length === 0 && (
                        <div className="py-20 text-center bg-white border border-dashed border-slate-200 rounded-3xl">
                            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-slate-50 text-slate-300 mb-4">
                                <CheckSquare className="w-8 h-8" />
                            </div>
                            <h3 className="text-lg font-medium text-slate-600">No tasks yet</h3>
                            <p className="text-slate-400 text-sm mt-1">Add your first task to get started</p>
                        </div>
                    )}
                </div>
            )}

            {/* Create Modal */}
            {isCreateModalOpen && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-6">
                    <div className="bg-white rounded-3xl p-8 max-w-lg w-full shadow-2xl">
                        <h3 className="text-xl font-bold mb-6 text-slate-900 text-center">New Task</h3>
                        <form onSubmit={handleCreateTodo} className="space-y-4">
                            <div>
                                <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Task Title</label>
                                <input
                                    autoFocus
                                    type="text"
                                    required
                                    value={newTodo.title}
                                    onChange={(e) => setNewTodo({ ...newTodo, title: e.target.value })}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                                    placeholder="e.g. Update API version"
                                />
                            </div>
                            <div>
                                <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Description</label>
                                <textarea
                                    value={newTodo.description}
                                    onChange={(e) => setNewTodo({ ...newTodo, description: e.target.value })}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm h-24"
                                    placeholder="Provide some details..."
                                />
                            </div>
                            <div>
                                <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Priority</label>
                                <select
                                    value={newTodo.priority}
                                    onChange={(e) => setNewTodo({ ...newTodo, priority: e.target.value })}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm appearance-none"
                                >
                                    <option value="LOW">Low</option>
                                    <option value="MEDIUM">Medium</option>
                                    <option value="HIGH">High</option>
                                </select>
                            </div>
                            <div className="flex gap-4 pt-4">
                                <button
                                    type="button"
                                    onClick={() => setIsCreateModalOpen(false)}
                                    className="flex-1 py-3 text-slate-600 font-bold text-sm hover:bg-slate-50 transition-all rounded-2xl"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 py-3 bg-indigo-600 text-white rounded-2xl font-bold text-sm hover:bg-indigo-700 shadow-lg shadow-indigo-100"
                                >
                                    Add Task
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Edit Modal */}
            {isEditModalOpen && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-6">
                    <div className="bg-white rounded-3xl p-8 max-w-lg w-full shadow-2xl">
                        <h3 className="text-xl font-bold mb-6 text-slate-900 text-center">Edit Task</h3>
                        <form onSubmit={handleUpdateTodo} className="space-y-4">
                            <div>
                                <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Task Title</label>
                                <input
                                    autoFocus
                                    type="text"
                                    required
                                    value={editTodo.title}
                                    onChange={(e) => setEditTodo({ ...editTodo, title: e.target.value })}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                                    placeholder="e.g. Update API version"
                                />
                            </div>
                            <div>
                                <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Description</label>
                                <textarea
                                    value={editTodo.description}
                                    onChange={(e) => setEditTodo({ ...editTodo, description: e.target.value })}
                                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm h-24"
                                    placeholder="Provide some details..."
                                />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Priority</label>
                                    <select
                                        value={editTodo.priority}
                                        onChange={(e) => setEditTodo({ ...editTodo, priority: e.target.value })}
                                        className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm appearance-none"
                                    >
                                        <option value="LOW">Low</option>
                                        <option value="MEDIUM">Medium</option>
                                        <option value="HIGH">High</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Status</label>
                                    <select
                                        value={editTodo.status}
                                        onChange={(e) => setEditTodo({ ...editTodo, status: e.target.value })}
                                        className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm appearance-none"
                                    >
                                        <option value="TODO">To Do</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="DONE">Done</option>
                                    </select>
                                </div>
                            </div>
                            <div className="flex gap-4 pt-4">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setIsEditModalOpen(false);
                                        setTodoToEdit(null);
                                    }}
                                    className="flex-1 py-3 text-slate-600 font-bold text-sm hover:bg-slate-50 transition-all rounded-2xl"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 py-3 bg-indigo-600 text-white rounded-2xl font-bold text-sm hover:bg-indigo-700 shadow-lg shadow-indigo-100"
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

export default TodosPage;
