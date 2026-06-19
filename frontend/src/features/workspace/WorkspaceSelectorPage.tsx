import React from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, Briefcase } from "lucide-react";
import { useWorkspace } from "../../context/useWorkspace";

export const WorkspaceSelectorPage: React.FC = () => {
  const { workspaces, loadingWorkspaces, selectWorkspace } = useWorkspace();
  const navigate = useNavigate();

  return (
    <div
      style={{
        minHeight: "100vh",
        padding: "40px",
        background: "var(--bg-primary)",
      }}
    >
      <div style={{ maxWidth: "900px", margin: "0 auto" }}>
        <p className="eyebrow">Workspace access</p>
        <h1
          style={{
            fontSize: "2.4rem",
            margin: "8px 0 14px",
            color: "var(--text-primary)",
          }}
        >
          Chọn workspace để bắt đầu làm việc
        </h1>
        <p style={{ color: "var(--text-secondary)", marginBottom: "24px" }}>
          Tài khoản user chỉ thao tác tài nguyên bên trong các workspace được
          gán.
        </p>
        <div
          className="card"
          style={{ padding: "24px", display: "grid", gap: "16px" }}
        >
          {loadingWorkspaces && <div>Đang tải workspaces...</div>}
          {!loadingWorkspaces && workspaces.length === 0 && (
            <div style={{ color: "var(--text-muted)" }}>
              Bạn chưa được gán vào workspace nào. Hãy liên hệ ADMIN.
            </div>
          )}
          {workspaces.map((workspace) => (
            <button
              key={workspace.id}
              onClick={() => {
                selectWorkspace(workspace.id);
                navigate("/app");
              }}
              style={{
                textAlign: "left",
                padding: "18px 20px",
                borderRadius: "14px",
                border: "1px solid var(--card-border)",
                background: "var(--bg-secondary)",
                color: "var(--text-primary)",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                cursor: "pointer",
              }}
            >
              <div
                style={{ display: "flex", gap: "14px", alignItems: "center" }}
              >
                <div
                  style={{
                    width: "42px",
                    height: "42px",
                    borderRadius: "12px",
                    background: "var(--accent-bg)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "var(--accent-color)",
                  }}
                >
                  <Briefcase size={20} />
                </div>
                <div>
                  <div style={{ fontWeight: 700 }}>{workspace.name}</div>
                  <div
                    style={{ color: "var(--text-muted)", fontSize: "0.9rem" }}
                  >
                    {workspace.description || "Workspace không có mô tả"}
                  </div>
                </div>
              </div>
              <ArrowRight size={18} />
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
