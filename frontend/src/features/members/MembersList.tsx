import React, { useEffect, useState } from "react";
import { useMemberStore } from "../../store/useMemberStore";
import { useWorkspace } from "../../context/useWorkspace";
import { useAuth } from "../../context/useAuth";
import {
  UserPlus,
  Search,
  Shield,
  UserIcon,
  Trash2,
  Activity,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import { MemberForm } from "./MemberForm";
import { WorkspaceRole } from "../../types/workspace.types";
import { getErrorMessage } from "../../utils/error";

export const MembersList: React.FC = () => {
  const { members, loading, fetchMembers, addMember, removeMember } =
    useMemberStore();
  const { activeWorkspace } = useWorkspace();
  const { user: currentUser } = useAuth();
  const { t } = useTranslation();

  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (activeWorkspace) {
      void fetchMembers(activeWorkspace.id);
    }
  }, [activeWorkspace, fetchMembers]);

  const filteredMembers = members.filter(
    (m) =>
      m.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
      m.email.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAddSubmit = async (userId: number, role: WorkspaceRole) => {
    if (!activeWorkspace) return;
    setSubmitting(true);
    try {
      await addMember(activeWorkspace.id, userId, role);
      setIsFormOpen(false);
    } catch (error) {
      alert("Không thể thêm thành viên: " + getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemove = (userId: number) => {
    if (!activeWorkspace) return;
    if (window.confirm("Bạn có chắc muốn xóa thành viên này khỏi Workspace?")) {
      removeMember(activeWorkspace.id, userId);
    }
  };

  if (!activeWorkspace) {
    return (
      <div style={{ color: "var(--text-muted)" }}>Vui lòng chọn Workspace.</div>
    );
  }

  // Check if current user is admin of this workspace (assuming ADMIN role check or if they are the creator)
  // For UI safety, we just show buttons, but Backend @PreAuthorize("@workspaceSecurity.isAdmin(...)") will protect it.

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      {/* Header Section */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-end",
          marginBottom: "32px",
        }}
      >
        <div>
          <p className="eyebrow">
            {t("members.subtitle", "Cấu hình Workspace")}
          </p>
          <h1
            style={{
              color: "var(--text-primary)",
              fontSize: "2rem",
              fontWeight: 700,
              margin: "8px 0 0 0",
            }}
          >
            {t("members.title", "Workspace Members")}
          </h1>
        </div>
        <button
          onClick={() => setIsFormOpen(true)}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            background: "linear-gradient(135deg, #a855f7 0%, #c084fc 100%)",
            border: "none",
            color: "#fff",
            padding: "12px 20px",
            borderRadius: "12px",
            fontWeight: 600,
            cursor: "pointer",
            boxShadow: "0 4px 15px rgba(168, 85, 247, 0.3)",
            transition: "all 0.2s",
          }}
          onMouseOver={(e) =>
            (e.currentTarget.style.transform = "translateY(-2px)")
          }
          onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
        >
          <UserPlus size={18} />
          {t("members.addBtn", "Mời thành viên")}
        </button>
      </div>

      {/* Toolbar */}
      <div
        className="card"
        style={{
          marginBottom: "24px",
          padding: "16px 24px",
          display: "flex",
          gap: "16px",
        }}
      >
        <div style={{ position: "relative", flex: 1, maxWidth: "400px" }}>
          <Search
            size={18}
            style={{
              position: "absolute",
              left: "14px",
              top: "50%",
              transform: "translateY(-50%)",
              color: "var(--text-muted)",
            }}
          />
          <input
            type="text"
            placeholder={t("members.search", "Tìm kiếm theo tên hoặc Email...")}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: "100%",
              padding: "10px 16px 10px 42px",
              background: "var(--bg-secondary)",
              border: "1px solid var(--card-border)",
              borderRadius: "10px",
              color: "var(--text-primary)",
              outline: "none",
            }}
          />
        </div>
      </div>

      {/* Table */}
      <div className="card" style={{ padding: 0, overflow: "hidden" }}>
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            textAlign: "left",
          }}
        >
          <thead>
            <tr
              style={{
                borderBottom: "1px solid var(--card-border)",
                background: "var(--bg-secondary)",
              }}
            >
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Người dùng
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Email
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Vai trò
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Ngày tham gia
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                  textAlign: "right",
                }}
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td
                  colSpan={5}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  <Activity
                    size={24}
                    className="spin"
                    style={{ margin: "0 auto 12px" }}
                  />
                  Đang tải danh sách...
                </td>
              </tr>
            ) : filteredMembers.length === 0 ? (
              <tr>
                <td
                  colSpan={5}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  Chưa có thành viên nào.
                </td>
              </tr>
            ) : (
              filteredMembers.map((m) => (
                <tr
                  key={m.userId}
                  style={{
                    borderBottom: "1px solid var(--card-border)",
                    transition: "background 0.2s",
                  }}
                >
                  <td style={{ padding: "16px 24px" }}>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
                    >
                      <div
                        style={{
                          width: "40px",
                          height: "40px",
                          borderRadius: "50%",
                          background: "var(--accent-bg)",
                          color: "var(--accent-color)",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                        }}
                      >
                        <UserIcon size={20} />
                      </div>
                      <div>
                        <div
                          style={{
                            fontWeight: 600,
                            color: "var(--text-primary)",
                            marginBottom: "4px",
                          }}
                        >
                          {m.username}
                          {m.userId === currentUser?.id && (
                            <span
                              style={{
                                marginLeft: "8px",
                                fontSize: "0.7rem",
                                background: "rgba(56, 189, 248, 0.15)",
                                color: "#38bdf8",
                                padding: "2px 6px",
                                borderRadius: "4px",
                              }}
                            >
                              Bạn
                            </span>
                          )}
                        </div>
                        <div
                          style={{
                            fontSize: "0.8rem",
                            color: "var(--text-muted)",
                          }}
                        >
                          ID: {m.userId}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td
                    style={{
                      padding: "16px 24px",
                      color: "var(--text-secondary)",
                    }}
                  >
                    {m.email}
                  </td>
                  <td style={{ padding: "16px 24px" }}>
                    <span
                      style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: "6px",
                        padding: "4px 10px",
                        borderRadius: "20px",
                        fontSize: "0.75rem",
                        fontWeight: 700,
                        color:
                          m.role === "ADMIN"
                            ? "#a855f7"
                            : "var(--text-secondary)",
                        background:
                          m.role === "ADMIN"
                            ? "rgba(168, 85, 247, 0.15)"
                            : "var(--bg-secondary)",
                      }}
                    >
                      {m.role === "ADMIN" && <Shield size={12} />}
                      {m.role}
                    </span>
                  </td>
                  <td
                    style={{
                      padding: "16px 24px",
                      color: "var(--text-muted)",
                      fontSize: "0.9rem",
                    }}
                  >
                    {new Date(m.joinedAt).toLocaleDateString("vi-VN")}
                  </td>
                  <td style={{ padding: "16px 24px", textAlign: "right" }}>
                    {m.userId !== currentUser?.id && (
                      <button
                        onClick={() => handleRemove(m.userId)}
                        style={{
                          background: "none",
                          border: "none",
                          color: "var(--error-color)",
                          cursor: "pointer",
                          padding: "6px",
                        }}
                        title="Xóa thành viên"
                      >
                        <Trash2 size={18} />
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {isFormOpen && (
        <MemberForm
          loading={submitting}
          onSubmit={handleAddSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};
