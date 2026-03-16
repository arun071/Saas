import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { CheckSquare, ArrowRight, Building2, LayoutGrid, LogIn } from "lucide-react";
import api from "../services/api";

/**
 * Onboarding page for new users.
 * Allows users to either create a new organization or join an existing one using an invite code.
 * 
 * @returns {JSX.Element} The onboarding page.
 */
const OnboardingPage = () => {
    const { user, setUser } = useAuth();
    const [step, setStep] = useState("choice"); // choice, create, join
    const [organizationName, setOrganizationName] = useState("");
    const [inviteCode, setInviteCode] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    /**
     * Handles organization creation.
     * Provisions a new schema and updates user session.
     */
    const handleCreateOrg = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        try {
            const response = await api.post("/api/onboarding/create-org", { organizationName });
            const { token, ...userData } = response.data;
            localStorage.setItem("token", token);
            localStorage.setItem("user", JSON.stringify(userData));
            setUser(userData);
            window.location.href = "/workspaces";
        } catch (err) {
            setError(err.response?.data?.error || "Failed to create organization");
        } finally {
            setLoading(false);
        }
    };

    /**
     * Handles joining an existing organization via invite code.
     */
    const handleJoinOrg = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        try {
            const response = await api.post("/api/onboarding/join-org", { inviteCode });
            const { token, ...userData } = response.data;
            localStorage.setItem("token", token);
            localStorage.setItem("user", JSON.stringify(userData));
            setUser(userData);
            window.location.href = "/workspaces";
        } catch (err) {
            setError(err.response?.data?.error || "Failed to join organization");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6">
            <div className="max-w-xl w-full">
                <div className="text-center mb-12">
                    <div className="inline-flex items-center justify-center p-3 rounded-2xl bg-indigo-600 text-white mb-6">
                        <CheckSquare className="w-8 h-8" />
                    </div>
                    <h2 className="text-3xl font-extrabold text-slate-900">Welcome, {user?.name}</h2>
                    <p className="text-slate-500 mt-2">Let's get you set up with an organization</p>
                </div>

                {error && (
                    <div className="mb-8 p-4 bg-red-50 text-red-600 text-sm rounded-xl border border-red-100 italic">
                        {error}
                    </div>
                )}

                {step === "choice" && (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <button
                            onClick={() => setStep("create")}
                            className="bg-white p-8 rounded-3xl shadow-sm border border-slate-100 hover:border-indigo-500 hover:ring-4 hover:ring-indigo-50 transition-all text-left group"
                        >
                            <div className="w-12 h-12 bg-indigo-50 rounded-2xl flex items-center justify-center text-indigo-600 mb-6 group-hover:bg-indigo-600 group-hover:text-white transition-colors">
                                <Building2 className="w-6 h-6" />
                            </div>
                            <h3 className="text-xl font-bold text-slate-900 mb-2">Create Organization</h3>
                            <p className="text-slate-500 text-sm mb-6 leading-relaxed">Start a new team and manage your own workspace and projects.</p>
                            <div className="flex items-center text-indigo-600 font-bold text-sm">
                                Get Started <ArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform" />
                            </div>
                        </button>

                        <button
                            onClick={() => setStep("join")}
                            className="bg-white p-8 rounded-3xl shadow-sm border border-slate-100 hover:border-indigo-500 hover:ring-4 hover:ring-indigo-50 transition-all text-left group"
                        >
                            <div className="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center text-slate-600 mb-6 group-hover:bg-indigo-600 group-hover:text-white transition-colors">
                                <LogIn className="w-6 h-6" />
                            </div>
                            <h3 className="text-xl font-bold text-slate-900 mb-2">Join Organization</h3>
                            <p className="text-slate-500 text-sm mb-6 leading-relaxed">Have an invite code? Join an existing team to collaborate.</p>
                            <div className="flex items-center text-indigo-600 font-bold text-sm">
                                Enter Code <ArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform" />
                            </div>
                        </button>
                    </div>
                )}

                {(step === "create" || step === "join") && (
                    <div className="bg-white p-8 rounded-3xl shadow-sm border border-slate-100 max-w-md mx-auto">
                        <button
                            onClick={() => setStep("choice")}
                            className="text-slate-400 text-sm font-medium hover:text-slate-600 mb-6 inline-flex items-center"
                        >
                            ← Back to options
                        </button>

                        <h3 className="text-2xl font-bold text-slate-900 mb-6">
                            {step === "create" ? "New Organization" : "Join Team"}
                        </h3>

                        <form onSubmit={step === "create" ? handleCreateOrg : handleJoinOrg} className="space-y-6">
                            {step === "create" ? (
                                <div>
                                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Organization Name</label>
                                    <div className="relative">
                                        <Building2 className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                                        <input
                                            type="text"
                                            required
                                            value={organizationName}
                                            onChange={(e) => setOrganizationName(e.target.value)}
                                            className="w-full pl-12 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:bg-white transition-all text-sm outline-none"
                                            placeholder="e.g. Acme Corp"
                                        />
                                    </div>
                                </div>
                            ) : (
                                <div>
                                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Invite Code</label>
                                    <div className="relative">
                                        <LogIn className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                                        <input
                                            type="text"
                                            required
                                            value={inviteCode}
                                            onChange={(e) => setInviteCode(e.target.value)}
                                            className="w-full pl-12 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:bg-white transition-all text-sm outline-none"
                                            placeholder="Enter your 8-digit code"
                                        />
                                    </div>
                                </div>
                            )}

                            <button
                                type="submit"
                                disabled={loading}
                                className="w-full py-3.5 bg-indigo-600 text-white rounded-xl font-bold text-sm hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 group shadow-lg shadow-indigo-100"
                            >
                                {loading ? "Processing..." : (
                                    <>
                                        {step === "create" ? "Create Workspace" : "Join Organization"}
                                        <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                                    </>
                                )}
                            </button>
                        </form>
                    </div>
                )}
            </div>
        </div>
    );
};

export default OnboardingPage;
