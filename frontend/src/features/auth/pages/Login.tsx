import React, { useState } from "react";
import { useAuth } from "../../../context/useAuth";
import { useNavigate } from "react-router-dom";
import {
  Shield,
  Lock,
  User as UserIcon,
  AlertCircle,
  Sparkles,
} from "lucide-react";
import { getErrorMessage } from "../../../utils/error";

export const Login: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      setError("Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
      return;
    }

    setError(null);
    setLoading(true);

    try {
      const response = await login({ username, password });
      navigate(
        response.role === "SUPER_ADMIN" ? "/admin/users" : "/select-workspace",
      );
    } catch (error) {
      setError(
        getErrorMessage(
          error,
          "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.",
        ),
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "100vh",
        width: "100%",
        padding: "24px",
        position: "relative",
        overflow: "hidden",
      }}
    >
      {/* Background glow effects */}
      <div
        style={{
          position: "absolute",
          top: "-10%",
          left: "30%",
          width: "500px",
          height: "500px",
          borderRadius: "50%",
          background:
            "radial-gradient(circle, rgba(79, 172, 254, 0.15) 0%, transparent 70%)",
          zIndex: 0,
        }}
      />
      <div
        style={{
          position: "absolute",
          bottom: "-10%",
          right: "20%",
          width: "600px",
          height: "600px",
          borderRadius: "50%",
          background:
            "radial-gradient(circle, rgba(0, 242, 254, 0.1) 0%, transparent 70%)",
          zIndex: 0,
        }}
      />

      <div
        className="card"
        style={{
          width: "100%",
          maxWidth: "440px",
          padding: "40px",
          zIndex: 1,
          animation: "fadeIn 0.6s ease-out",
        }}
      >
        <div style={{ textAlign: "center", marginBottom: "32px" }}>
          <div
            style={{
              display: "inline-flex",
              alignItems: "center",
              justifyContent: "center",
              width: "64px",
              height: "64px",
              borderRadius: "20px",
              background:
                "linear-gradient(135deg, rgba(79, 172, 254, 0.2), rgba(0, 242, 254, 0.2))",
              border: "1px solid rgba(79, 172, 254, 0.3)",
              marginBottom: "16px",
              color: "#4facfe",
            }}
          >
            <Shield size={32} />
          </div>
          <h2
            style={{
              fontSize: "1.8rem",
              fontWeight: 700,
              margin: "0 0 8px 0",
              color: "#fff",
            }}
          >
            API Health Check
          </h2>
          <p style={{ color: "#94a3b8", fontSize: "0.9rem", margin: 0 }}>
            Hệ thống giám sát trạng thái dịch vụ thời gian thực
          </p>
        </div>

        {error && (
          <div
            style={{
              display: "flex",
              gap: "12px",
              alignItems: "center",
              background: "rgba(239, 68, 68, 0.15)",
              border: "1px solid rgba(239, 68, 68, 0.3)",
              borderRadius: "12px",
              padding: "12px 16px",
              marginBottom: "24px",
              color: "#f87171",
              fontSize: "0.875rem",
            }}
          >
            <AlertCircle size={20} style={{ flexShrink: 0 }} />
            <span>{error}</span>
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div>
            <label
              style={{
                display: "block",
                color: "#94b3fd",
                fontSize: "0.75rem",
                textTransform: "uppercase",
                letterSpacing: "0.08em",
                marginBottom: "8px",
                fontWeight: 600,
              }}
            >
              Tên tài khoản
            </label>
            <div style={{ position: "relative" }}>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Nhập tên đăng nhập..."
                disabled={loading}
                style={{
                  width: "100%",
                  padding: "12px 16px 12px 42px",
                  background: "rgba(15, 23, 42, 0.6)",
                  border: "1px solid rgba(191, 219, 254, 0.15)",
                  borderRadius: "12px",
                  color: "#fff",
                  fontSize: "0.95rem",
                  outline: "none",
                  transition: "all 0.2s",
                }}
                onFocus={(e) => {
                  e.target.style.border = "1px solid #4facfe";
                  e.target.style.boxShadow = "0 0 10px rgba(79, 172, 254, 0.2)";
                }}
                onBlur={(e) => {
                  e.target.style.border = "1px solid rgba(191, 219, 254, 0.15)";
                  e.target.style.boxShadow = "none";
                }}
              />
              <UserIcon
                size={18}
                style={{
                  position: "absolute",
                  left: "14px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "#64748b",
                }}
              />
            </div>
          </div>

          <div>
            <label
              style={{
                display: "block",
                color: "#94b3fd",
                fontSize: "0.75rem",
                textTransform: "uppercase",
                letterSpacing: "0.08em",
                marginBottom: "8px",
                fontWeight: 600,
              }}
            >
              Mật khẩu
            </label>
            <div style={{ position: "relative" }}>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Nhập mật khẩu..."
                disabled={loading}
                style={{
                  width: "100%",
                  padding: "12px 16px 12px 42px",
                  background: "rgba(15, 23, 42, 0.6)",
                  border: "1px solid rgba(191, 219, 254, 0.15)",
                  borderRadius: "12px",
                  color: "#fff",
                  fontSize: "0.95rem",
                  outline: "none",
                  transition: "all 0.2s",
                }}
                onFocus={(e) => {
                  e.target.style.border = "1px solid #4facfe";
                  e.target.style.boxShadow = "0 0 10px rgba(79, 172, 254, 0.2)";
                }}
                onBlur={(e) => {
                  e.target.style.border = "1px solid rgba(191, 219, 254, 0.15)";
                  e.target.style.boxShadow = "none";
                }}
              />
              <Lock
                size={18}
                style={{
                  position: "absolute",
                  left: "14px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "#64748b",
                }}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{
              width: "100%",
              padding: "14px",
              background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
              border: "none",
              borderRadius: "12px",
              color: "#fff",
              fontSize: "1rem",
              fontWeight: 600,
              cursor: loading ? "not-allowed" : "pointer",
              boxShadow: "0 4px 15px rgba(79, 172, 254, 0.3)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "8px",
              transition: "all 0.2s",
              opacity: loading ? 0.75 : 1,
            }}
            onMouseOver={(e) => {
              if (!loading) {
                e.currentTarget.style.transform = "translateY(-1px)";
                e.currentTarget.style.boxShadow =
                  "0 6px 20px rgba(79, 172, 254, 0.4)";
              }
            }}
            onMouseOut={(e) => {
              if (!loading) {
                e.currentTarget.style.transform = "none";
                e.currentTarget.style.boxShadow =
                  "0 4px 15px rgba(79, 172, 254, 0.3)";
              }
            }}
          >
            {loading ? "Đang xác thực..." : "Đăng nhập"}
          </button>
        </form>

        <div
          style={{
            marginTop: "24px",
            textAlign: "center",
            fontSize: "0.85rem",
            color: "#64748b",
          }}
        >
          Bản demo hiện dùng tài khoản seed để đảm bảo luồng thi ổn định và
          không phụ thuộc vào register API.
        </div>

        {/* Demo Account Box */}
        <div
          style={{
            marginTop: "32px",
            padding: "16px",
            background: "rgba(255, 255, 255, 0.03)",
            border: "1px dashed rgba(255, 255, 255, 0.1)",
            borderRadius: "12px",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "6px",
              fontSize: "0.8rem",
              color: "#a3e635",
              fontWeight: 600,
              marginBottom: "8px",
            }}
          >
            <Sparkles size={14} />
            <span>Tài khoản thử nghiệm hệ thống</span>
          </div>
          <div
            style={{
              fontSize: "0.775rem",
              color: "#94a3b8",
              display: "grid",
              gap: "4px",
            }}
          >
            <div>
              <strong>Admin:</strong> admin / password123 (Quản trị hệ thống)
            </div>
            <div>
              <strong>User:</strong> viewer / password123 (Chọn workspace để làm
              việc)
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
