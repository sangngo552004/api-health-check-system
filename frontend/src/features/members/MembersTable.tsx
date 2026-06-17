import React from "react";
import { Activity, Trash2, UserIcon } from "lucide-react";
import { WorkspaceMemberDto } from "../../types/workspace.types";

export const MembersTable: React.FC<{
  loading: boolean;
  members: WorkspaceMemberDto[];
  currentUserId?: number;
  onRemove: (userId: number) => void;
}> = ({ loading, members, currentUserId, onRemove }) => (
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
          <th style={thStyle}>Người dùng</th>
          <th style={thStyle}>Email</th>
          <th style={thStyle}>Ngày tham gia</th>
          <th style={{ ...thStyle, textAlign: "right" }}>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        {loading ? (
          <tr>
            <td colSpan={4} style={emptyCellStyle}>
              <Activity
                size={24}
                className="spin"
                style={{ margin: "0 auto 12px" }}
              />
              Đang tải danh sách...
            </td>
          </tr>
        ) : members.length === 0 ? (
          <tr>
            <td colSpan={4} style={emptyCellStyle}>
              Chưa có thành viên nào.
            </td>
          </tr>
        ) : (
          members.map((member) => (
            <tr
              key={member.userId}
              style={{
                borderBottom: "1px solid var(--card-border)",
                transition: "background 0.2s",
              }}
            >
              <td style={{ padding: "16px 24px" }}>
                <div
                  style={{ display: "flex", alignItems: "center", gap: "12px" }}
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
                      {member.username}
                      {member.userId === currentUserId && (
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
                      style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}
                    >
                      ID: {member.userId}
                    </div>
                  </div>
                </div>
              </td>
              <td
                style={{ padding: "16px 24px", color: "var(--text-secondary)" }}
              >
                {member.email}
              </td>
              <td
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontSize: "0.9rem",
                }}
              >
                {new Date(member.joinedAt).toLocaleDateString("vi-VN")}
              </td>
              <td style={{ padding: "16px 24px", textAlign: "right" }}>
                {member.userId !== currentUserId && (
                  <button
                    onClick={() => onRemove(member.userId)}
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
);

const thStyle: React.CSSProperties = {
  padding: "16px 24px",
  color: "var(--text-muted)",
  fontWeight: 600,
  fontSize: "0.85rem",
};

const emptyCellStyle: React.CSSProperties = {
  padding: "40px",
  textAlign: "center",
  color: "var(--text-muted)",
};
