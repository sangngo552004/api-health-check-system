import React from "react";
import { Pencil, Trash2, Users } from "lucide-react";
import {
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
  memberUserId: string;
  onToggleMembers: (workspaceId: number) => void;
  onEdit: (workspace: WorkspaceDto) => void;
  onDelete: (workspaceId: number) => void;
  onMemberUserIdChange: (value: string) => void;
  onAddMember: () => void;
  onRemoveMember: (workspaceId: number, userId: number) => void;
}> = ({
  workspace,
  isExpanded,
  activeMembers,
  memberUserId,
  onToggleMembers,
  onEdit,
  onDelete,
  onMemberUserIdChange,
  onAddMember,
  onRemoveMember,
}) => (
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
          <span>owner: {workspace.ownerId}</span>
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
          {isExpanded ? "Ẩn members" : "Quản lý members"}
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
        <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
          <input
            value={memberUserId}
            onChange={(e) => onMemberUserIdChange(e.target.value)}
            placeholder="User ID cần thêm"
            style={{ ...inputStyle, flex: 1, minWidth: "220px" }}
          />
          <button onClick={onAddMember} type="button" style={primaryButton}>
            Thêm member
          </button>
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
                  {member.email} | ID {member.userId}
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
