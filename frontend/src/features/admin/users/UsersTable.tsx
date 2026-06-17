import React from "react";
import { Pencil, Trash2 } from "lucide-react";
import { AdminUserDto } from "../../../types/workspace.types";
import { RoleBadge, StatusBadge } from "../components/adminUi";
import { iconButton, tdStyle, thStyle } from "../components/adminStyles";

export const UsersTable: React.FC<{
  users: AdminUserDto[];
  onEdit: (user: AdminUserDto) => void;
  onDelete: (user: AdminUserDto) => void;
}> = ({ users, onEdit, onDelete }) => (
  <div style={{ overflowX: "auto" }}>
    <table
      style={{ width: "100%", borderCollapse: "collapse", minWidth: "760px" }}
    >
      <thead>
        <tr style={{ borderBottom: "1px solid var(--card-border)" }}>
          <th style={thStyle}>ID</th>
          <th style={thStyle}>Username</th>
          <th style={thStyle}>Email</th>
          <th style={thStyle}>Phone</th>
          <th style={thStyle}>Role</th>
          <th style={thStyle}>Status</th>
          <th style={thStyle}>Hành động</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr
            key={user.id}
            style={{ borderBottom: "1px solid var(--card-border)" }}
          >
            <td style={tdStyle}>{user.id}</td>
            <td style={tdStyle}>{user.username}</td>
            <td style={tdStyle}>{user.email || "-"}</td>
            <td style={tdStyle}>{user.phoneNumber || "-"}</td>
            <td style={tdStyle}>
              <RoleBadge role={user.role} />
            </td>
            <td style={tdStyle}>
              <StatusBadge active={user.isActive ?? false} />
            </td>
            <td style={tdStyle}>
              <div style={{ display: "flex", gap: "8px" }}>
                <button
                  type="button"
                  onClick={() => onEdit(user)}
                  style={iconButton}
                  title="Sửa"
                >
                  <Pencil size={16} />
                </button>
                <button
                  type="button"
                  onClick={() => onDelete(user)}
                  style={{ ...iconButton, color: "#f87171" }}
                  title="Xóa"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
