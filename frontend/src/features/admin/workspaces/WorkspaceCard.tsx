import React from "react";
import { Pencil, Search, Trash2, Users } from "lucide-react";
import {
  AdminUserDto,
  WorkspaceDto,
  WorkspaceMemberDto,
} from "../../../types/workspace.types";
import { StatusBadge } from "../components/adminUi";
import {
  inputStyle,
  primaryButton,
  secondaryButton,
} from "../components/adminStyles";

export const WorkspaceCard: React.FC<{
  workspace: WorkspaceDto;
  isExpanded: boolean;
  activeMembers: WorkspaceMemberDto[];
  availableUsers: AdminUserDto[];
  memberSearch: string;
  selectedMemberId: number | null;
  onToggleMembers: (workspaceId: number) => void;
  onEdit: (workspace: WorkspaceDto) => void;
  onDelete: (workspaceId: number) => void;
  onMemberSearchChange: (value: string) => void;
  onSelectMember: (userId: number | null) => void;
  onAddMember: () => void;
  onRemoveMember: (workspaceId: number, userId: number) => void;
}> = ({
  workspace,
  isExpanded,
  activeMembers,
  availableUsers,
  memberSearch,
  selectedMemberId,
  onToggleMembers,
  onEdit,
  onDelete,
  onMemberSearchChange,
  onSelectMember,
  onAddMember,
  onRemoveMember,
}) => {
  const workspaceMemberIds = new Set(activeMembers.map((member) => member.userId));
  const filteredUsers = availableUsers.filter((user) => {
    if (workspaceMemberIds.has(user.id)) {
      return false;
    }

    if (!memberSearch.trim()) {
      return true;
    }

    const keyword = memberSearch.trim().toLowerCase();
    return [user.username, user.email || "", user.phoneNumber || ""].some((value) =>
      value.toLowerCase().includes(keyword),
    );
  });

  const selectedUser =
    selectedMemberId != null
      ? availableUsers.find((user) => user.id === selectedMemberId) ?? null
      : null;

  return (
    <div
      style={{
        border: "1px solid var(--card-border)",
        borderRadius: "18px",
        padding: "18px",
        background: "rgba(255,255,255,0.02)",
      }}
    >
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        gap: "12px",
        flexWrap: "wrap",
      }}
    >
      <div>
        <div style={{ fontWeight: 700, fontSize: "1.05rem" }}>
          {workspace.name}
        </div>
        <div
          style={{
            color: "var(--text-muted)",
            fontSize: "0.9rem",
            marginTop: "4px",
            display: "flex",
            gap: "8px",
            flexWrap: "wrap",
          }}
        >
          <span>slug: {workspace.slug}</span>
          <StatusBadge active={workspace.isActive} />
        </div>
        {workspace.description && (
          <div style={{ color: "var(--text-secondary)", marginTop: "8px" }}>
            {workspace.description}
          </div>
        )}
      </div>

      <div style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}>
        <button
          type="button"
          onClick={() => onToggleMembers(workspace.id)}
          style={secondaryButton}
        >
          <Users size={16} />
          {isExpanded ? "Ẩn thành viên" : "Quản lý thành viên"}
        </button>
        <button
          type="button"
          onClick={() => onEdit(workspace)}
          style={secondaryButton}
        >
          <Pencil size={16} />
          Sửa
        </button>
        <button
          type="button"
          onClick={() => onDelete(workspace.id)}
          style={{ ...secondaryButton, color: "#f87171" }}
        >
          <Trash2 size={16} />
          Xóa
        </button>
      </div>
    </div>

    {isExpanded && (
        <div
          style={{
            marginTop: "16px",
            borderTop: "1px solid var(--card-border)",
            paddingTop: "16px",
            display: "grid",
            gap: "14px",
          }}
        >
          <div
            style={{
              display: "grid",
              gap: "12px",
              padding: "16px",
              borderRadius: "16px",
              background: "var(--bg-secondary)",
              border: "1px solid var(--card-border)",
            }}
          >
            <div style={{ fontWeight: 700 }}>Thêm thành viên vào workspace</div>
            <div style={{ position: "relative" }}>
              <Search
                size={16}
                style={{
                  position: "absolute",
                  top: "50%",
                  left: "12px",
                  transform: "translateY(-50%)",
                  color: "var(--text-muted)",
                }}
              />
              <input
                value={memberSearch}
                onChange={(e) => onMemberSearchChange(e.target.value)}
                placeholder="Tìm theo tên, email hoặc số điện thoại"
                style={{ ...inputStyle, paddingLeft: "38px" }}
              />
            </div>
            <div
              style={{
                display: "grid",
                gap: "10px",
                maxHeight: "220px",
                overflowY: "auto",
              }}
            >
              {filteredUsers.slice(0, 8).map((user) => (
                <button
                  key={user.id}
                  type="button"
                  onClick={() => onSelectMember(user.id)}
                  style={{
                    textAlign: "left",
                    padding: "12px 14px",
                    borderRadius: "12px",
                    border:
                      selectedMemberId === user.id
                        ? "1px solid var(--accent-color)"
                        : "1px solid var(--card-border)",
                    background:
                      selectedMemberId === user.id
                        ? "var(--accent-bg)"
                        : "var(--card-bg)",
                    color: "var(--text-primary)",
                    cursor: "pointer",
                  }}
                >
                  <div style={{ fontWeight: 600 }}>{user.username}</div>
                  <div
                    style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}
                  >
                    {user.email || "Chưa có email"}
                  </div>
                </button>
              ))}
              {filteredUsers.length === 0 && (
                <div style={{ color: "var(--text-muted)" }}>
                  Không còn user phù hợp để thêm.
                </div>
              )}
            </div>
            {selectedUser && (
              <div
                style={{
                  padding: "12px 14px",
                  borderRadius: "12px",
                  border: "1px solid var(--accent-hover)",
                  background: "var(--accent-bg)",
                }}
              >
                Đã chọn: <strong>{selectedUser.username}</strong>
                <span style={{ color: "var(--text-muted)" }}>
                  {" "}
                  · {selectedUser.email || "Chưa có email"}
                </span>
              </div>
            )}
            <div style={{ display: "flex", justifyContent: "flex-end" }}>
              <button
                onClick={onAddMember}
                type="button"
                style={primaryButton}
                disabled={selectedMemberId == null}
              >
                Thêm thành viên
              </button>
            </div>
          </div>
        <div style={{ display: "grid", gap: "10px" }}>
          {activeMembers.map((member) => (
            <div
              key={member.userId}
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                background: "var(--bg-secondary)",
                borderRadius: "12px",
                padding: "12px 14px",
                gap: "12px",
                flexWrap: "wrap",
              }}
            >
              <div>
                <div style={{ fontWeight: 600 }}>{member.username}</div>
                <div
                  style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}
                >
                  {member.email}
                </div>
              </div>
              <button
                type="button"
                onClick={() => onRemoveMember(workspace.id, member.userId)}
                style={{ ...secondaryButton, color: "#f87171" }}
              >
                Xóa khỏi workspace
              </button>
            </div>
          ))}
          {activeMembers.length === 0 && (
            <div style={{ color: "var(--text-muted)" }}>
              Workspace chưa có member.
            </div>
          )}
        </div>
      </div>
    )}
    </div>
  );
};
