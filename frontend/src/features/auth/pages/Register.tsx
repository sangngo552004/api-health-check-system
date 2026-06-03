import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import {
  Shield,
  Lock,
  User as UserIcon,
  Mail,
  Phone,
  AlertCircle,
  CheckCircle,
} from "lucide-react";

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !email || !password) {
      setError("Vui lòng điền đầy đủ các thông tin bắt buộc (*).");
      return;
    }

    setError(null);
    setLoading(true);

    // Simulate registration
    setTimeout(() => {
      setLoading(false);
      setSuccess(true);
      setTimeout(() => {
        navigate("/login");
      }, 3500);
    }, 1200);
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
        className="card"
        style={{
          width: "100%",
          maxWidth: "460px",
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
            Đăng ký tài khoản
          </h2>
          <p style={{ color: "#94a3b8", fontSize: "0.9rem", margin: 0 }}>
            Tạo tài khoản mới để tham gia giám sát API
          </p>
        </div>

        {success ? (
          <div
            style={{
              background: "rgba(34, 197, 94, 0.15)",
              border: "1px solid rgba(34, 197, 94, 0.3)",
              borderRadius: "16px",
              padding: "24px",
              color: "#4ade80",
              textAlign: "center",
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              gap: "12px",
            }}
          >
            <CheckCircle size={48} />
            <h3 style={{ margin: 0, color: "#fff", fontSize: "1.2rem" }}>
              Đăng ký thành công!
            </h3>
            <p
              style={{
                color: "#cbd5e1",
                fontSize: "0.875rem",
                lineHeight: 1.6,
                margin: 0,
              }}
            >
              Hệ thống đã ghi nhận yêu cầu. Vì đây là môi trường local testing,
              bạn đang được chuyển hướng về trang đăng nhập để sử dụng tài khoản
              thử nghiệm có sẵn.
            </p>
          </div>
        ) : (
          <>
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
              style={{ display: "flex", flexDirection: "column", gap: "16px" }}
            >
              <div>
                <label
                  style={{
                    display: "block",
                    color: "#94b3fd",
                    fontSize: "0.75rem",
                    textTransform: "uppercase",
                    letterSpacing: "0.08em",
                    marginBottom: "6px",
                    fontWeight: 600,
                  }}
                >
                  Tên tài khoản *
                </label>
                <div style={{ position: "relative" }}>
                  <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Nhập tên đăng nhập..."
                    disabled={loading}
                    required
                    style={{
                      width: "100%",
                      padding: "12px 16px 12px 42px",
                      background: "rgba(15, 23, 42, 0.6)",
                      border: "1px solid rgba(191, 219, 254, 0.15)",
                      borderRadius: "12px",
                      color: "#fff",
                      fontSize: "0.95rem",
                      outline: "none",
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
                    marginBottom: "6px",
                    fontWeight: 600,
                  }}
                >
                  Email liên hệ *
                </label>
                <div style={{ position: "relative" }}>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="example@healthcheck.com"
                    disabled={loading}
                    required
                    style={{
                      width: "100%",
                      padding: "12px 16px 12px 42px",
                      background: "rgba(15, 23, 42, 0.6)",
                      border: "1px solid rgba(191, 219, 254, 0.15)",
                      borderRadius: "12px",
                      color: "#fff",
                      fontSize: "0.95rem",
                      outline: "none",
                    }}
                  />
                  <Mail
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
                    marginBottom: "6px",
                    fontWeight: 600,
                  }}
                >
                  Số điện thoại
                </label>
                <div style={{ position: "relative" }}>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="09xx xxx xxx"
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
                    }}
                  />
                  <Phone
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
                    marginBottom: "6px",
                    fontWeight: 600,
                  }}
                >
                  Mật khẩu tài khoản *
                </label>
                <div style={{ position: "relative" }}>
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Tối thiểu 6 ký tự..."
                    disabled={loading}
                    required
                    style={{
                      width: "100%",
                      padding: "12px 16px 12px 42px",
                      background: "rgba(15, 23, 42, 0.6)",
                      border: "1px solid rgba(191, 219, 254, 0.15)",
                      borderRadius: "12px",
                      color: "#fff",
                      fontSize: "0.95rem",
                      outline: "none",
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
                  background:
                    "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
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
                  marginTop: "12px",
                  transition: "all 0.2s",
                  opacity: loading ? 0.75 : 1,
                }}
              >
                {loading ? "Đang đăng ký..." : "Đăng ký tài khoản"}
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
              Đã có tài khoản?{" "}
              <Link
                to="/login"
                style={{
                  color: "#4facfe",
                  textDecoration: "none",
                  fontWeight: 600,
                }}
              >
                Đăng nhập ngay
              </Link>
            </div>
          </>
        )}
      </div>
    </div>
  );
};
