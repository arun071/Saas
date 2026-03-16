import React, { useState, useEffect } from "react";
import {
    UserPlus,
    Ticket,
    Users,
    Check,
    X,
    Plus,
    Copy,
    Calendar,
    AlertCircle,
    Loader2
} from "lucide-react";
import api from "../services/api";

/**
 * AdminOnboardingPage component
 * Provides administrative functionality to manage organization members.
 * Supports adding users, generating invite codes, and approving/rejecting membership requests.
 *
 * @returns {JSX.Element} The rendered AdminOnboardingPage component.
 */
const AdminOnboardingPage = () => {
    const [pendingUsers, setPendingUsers] = useState([]);
    const [emailToAdd, setEmailToAdd] = useState("");
    const [inviteCode, setInviteCode] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false);
    const [message, setMessage] = useState({ type: "", text: "" });

    useEffect(() => {
        fetchPendingUsers();
    }, []);

    /**
     * Fetches the list of users with pending membership requests.
     */
    const fetchPendingUsers = async () => {
        try {
            const response = await api.get("/api/admin/users/pending");
            setPendingUsers(response.data);
        } catch (err) {
            console.error("Failed to fetch pending users", err);
        } finally {
            setLoading(false);
        }
    };

    /**
     * Handles adding a user directly to the organization by email.
     *
     * @param {Event} e - The form submission event.
     */
    const handleAddUser = async (e) => {
        e.preventDefault();
        setActionLoading(true);
        setMessage({ type: "", text: "" });
        try {
            await api.post("/api/admin/users/add", emailToAdd);
            setMessage({ type: "success", text: `User ${emailToAdd} added successfully!` });
            setEmailToAdd("");
        } catch (err) {
            setMessage({ type: "error", text: err.response?.data?.message || "Failed to add user" });
        } finally {
            setActionLoading(false);
        }
    };

    /**
     * Generates a new organization invite code.
     */
    const handleGenerateInvite = async () => {
        setActionLoading(true);
        setMessage({ type: "", text: "" });
        try {
            const response = await api.post("/api/admin/invites/generate");
            setInviteCode(response.data);
            setMessage({ type: "success", text: "Invite code generated!" });
        } catch (err) {
            setMessage({ type: "error", text: "Failed to generate invite code" });
        } finally {
            setActionLoading(false);
        }
    };

    /**
     * Approves a pending membership request.
     *
     * @param {string} userId - The ID of the user to approve.
     */
    const handleApprove = async (userId) => {
        try {
            await api.post(`/api/admin/users/${userId}/approve`);
            setPendingUsers(pendingUsers.filter(u => u.id !== userId));
            setMessage({ type: "success", text: "User approved!" });
        } catch (err) {
            setMessage({ type: "error", text: "Failed to approve user" });
        }
    };

    /**
     * Rejects a pending membership request.
     *
     * @param {string} userId - The ID of the user to reject.
     */
    const handleReject = async (userId) => {
        try {
            await api.post(`/api/admin/users/${userId}/reject`);
            setPendingUsers(pendingUsers.filter(u => u.id !== userId));
            setMessage({ type: "success", text: "User rejected" });
        } catch (err) {
            setMessage({ type: "error", text: "Failed to reject user" });
        }
    };

    /**
     * Copies text to the clipboard and shows a success message.
     *
     * @param {string} text - The text to copy.
     */
    const copyToClipboard = (text) => {
        navigator.clipboard.writeText(text);
        setMessage({ type: "success", text: "Code copied to clipboard!" });
    };

    return (
        <div className="min-h-screen bg-slate-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-4xl mx-auto space-y-8">
                <div>
                    <h1 className="text-3xl font-extrabold text-slate-900 flex items-center gap-3">
                        <Users className="text-indigo-600" size={32} />
                        Organization Management
                    </h1>
                    <p className="mt-2 text-slate-600">
                        Add users, generate invite codes, and manage pending approvals.
                    </p>
                </div>

                {message.text && (
                    <div className={`p-4 rounded-lg flex items-center gap-3 ${message.type === "success" ? "bg-emerald-50 text-emerald-700 border border-emerald-100" : "bg-rose-50 text-rose-700 border border-rose-100"
                        }`}>
                        {message.type === "success" ? <Check size={20} /> : <AlertCircle size={20} />}
                        {message.text}
                    </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {/* Add User Section */}
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                        <h2 className="text-xl font-bold text-slate-900 mb-4 flex items-center gap-2">
                            <UserPlus className="text-indigo-600" size={20} />
                            Add User Directly
                        </h2>
                        <form onSubmit={handleAddUser} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Email Address</label>
                                <input
                                    type="email"
                                    value={emailToAdd}
                                    onChange={(e) => setEmailToAdd(e.target.value)}
                                    placeholder="user@example.com"
                                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                                    required
                                />
                            </div>
                            <button
                                type="submit"
                                disabled={actionLoading}
                                className="w-full bg-indigo-600 text-white py-2 rounded-lg font-semibold hover:bg-indigo-700 transition-colors flex items-center justify-center gap-2 disabled:bg-indigo-400"
                            >
                                {actionLoading ? <Loader2 className="animate-spin" size={20} /> : <Plus size={20} />}
                                Add to Organization
                            </button>
                        </form>
                    </div>

                    {/* Invite Code Section */}
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                        <h2 className="text-xl font-bold text-slate-900 mb-4 flex items-center gap-2">
                            <Ticket className="text-indigo-600" size={20} />
                            Invite Codes
                        </h2>
                        <p className="text-sm text-slate-600 mb-6">
                            Generate a code that users can use to join your organization during onboarding.
                        </p>

                        {inviteCode ? (
                            <div className="space-y-4">
                                <div className="p-4 bg-slate-50 rounded-lg border-2 border-dashed border-slate-200 relative group">
                                    <div className="text-center">
                                        <span className="text-2xl font-mono font-bold text-indigo-600 tracking-widest uppercase">
                                            {inviteCode.inviteCode}
                                        </span>
                                    </div>
                                    <button
                                        onClick={() => copyToClipboard(inviteCode.inviteCode)}
                                        className="absolute top-2 right-2 p-1 text-slate-400 hover:text-indigo-600 transition-colors"
                                        title="Copy code"
                                    >
                                        <Copy size={16} />
                                    </button>
                                </div>
                                <div className="flex items-center gap-2 text-xs text-slate-500 justify-center">
                                    <Calendar size={14} />
                                    <span>Expires: {new Date(inviteCode.expiresAt).toLocaleDateString()}</span>
                                </div>
                                <button
                                    onClick={handleGenerateInvite}
                                    className="w-full text-indigo-600 text-sm font-semibold hover:underline"
                                >
                                    Generate new code
                                </button>
                            </div>
                        ) : (
                            <button
                                onClick={handleGenerateInvite}
                                disabled={actionLoading}
                                className="w-full border-2 border-indigo-600 text-indigo-600 py-2 rounded-lg font-semibold hover:bg-indigo-50 transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                            >
                                {actionLoading ? <Loader2 className="animate-spin" size={20} /> : <Ticket size={20} />}
                                Generate Invite Code
                            </button>
                        )}
                    </div>
                </div>

                {/* Pending Approvals Section */}
                <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                    <div className="px-6 py-4 border-b border-slate-200 bg-slate-50/50">
                        <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
                            <Check className="text-indigo-600" size={20} />
                            Pending Approvals
                        </h2>
                    </div>

                    {loading ? (
                        <div className="py-12 flex flex-col items-center gap-4 text-slate-400">
                            <Loader2 className="animate-spin" size={32} />
                            <p>Loading pending requests...</p>
                        </div>
                    ) : pendingUsers.length > 0 ? (
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead className="bg-slate-50 border-b border-slate-200">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">User</th>
                                        <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Date</th>
                                        <th className="px-6 py-3 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-200">
                                    {pendingUsers.map((user) => (
                                        <tr key={user.id} className="hover:bg-slate-50 transition-colors">
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex flex-col">
                                                    <span className="text-sm font-medium text-slate-900">{user.name}</span>
                                                    <span className="text-xs text-slate-500">{user.email}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">
                                                {new Date(user.createdAt).toLocaleDateString()}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-3">
                                                <button
                                                    onClick={() => handleApprove(user.id)}
                                                    className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-100 text-emerald-700 rounded-md hover:bg-emerald-200 transition-colors"
                                                >
                                                    <Check size={14} /> Approve
                                                </button>
                                                <button
                                                    onClick={() => handleReject(user.id)}
                                                    className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-rose-100 text-rose-700 rounded-md hover:bg-rose-200 transition-colors"
                                                >
                                                    <X size={14} /> Reject
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="py-12 text-center text-slate-400">
                            <Users size={48} className="mx-auto mb-4 opacity-20" />
                            <p>No pending approval requests at this time.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AdminOnboardingPage;
