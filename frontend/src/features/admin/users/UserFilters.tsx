import React from "react";
import { Search } from "lucide-react";
import {
  inputStyle,
  primaryButton,
  secondaryButton,
} from "../components/adminStyles";
import { UserRoleFilter, UserStatusFilter } from "./userPage.types";

const sortOptions = [
  { value: "createdAt", label: "Mới nhất" },
  { value: "username", label: "Username" },
  { value: "email", label: "Email" },
  { value: "role", label: "Role" },
];

export const UserFilters: React.FC<{
  searchInput: string;
  role: UserRoleFilter;
  status: UserStatusFilter;
  sortBy: string;
  sortDir: "asc" | "desc";
  onSearchInputChange: (value: string) => void;
  onRoleChange: (value: UserRoleFilter) => void;
  onStatusChange: (value: UserStatusFilter) => void;
  onSortByChange: (value: string) => void;
  onSortDirChange: (value: "asc" | "desc") => void;
  onApply: () => void;
  onReset: () => void;
}> = ({
  searchInput,
  role,
  status,
  sortBy,
  sortDir,
  onSearchInputChange,
  onRoleChange,
  onStatusChange,
  onSortByChange,
  onSortDirChange,
  onApply,
  onReset,
}) => (
  <div
    className="card"
    style={{
      padding: "24px",
      marginBottom: "20px",
      display: "grid",
      gap: "14px",
    }}
  >
    <div
      style={{
        display: "grid",
        gridTemplateColumns: "minmax(260px, 2fr) repeat(5, minmax(120px, 1fr))",
        gap: "12px",
      }}
    >
      <div style={{ position: "relative" }}>
        <Search
          size={16}
          style={{
            position: "absolute",
            left: "12px",
            top: "50%",
            transform: "translateY(-50%)",
            color: "var(--text-muted)",
          }}
        />
        <input
          value={searchInput}
          onChange={(e) => onSearchInputChange(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              onApply();
            }
          }}
          placeholder="Tìm theo username, email, phone"
          style={{ ...inputStyle, paddingLeft: "36px" }}
        />
      </div>
      <select
        value={role}
        onChange={(e) => onRoleChange(e.target.value as UserRoleFilter)}
        style={inputStyle}
      >
        <option value="ALL">Tất cả role</option>
        <option value="ADMIN">ADMIN</option>
        <option value="USER">USER</option>
      </select>
      <select
        value={status}
        onChange={(e) => onStatusChange(e.target.value as UserStatusFilter)}
        style={inputStyle}
      >
        <option value="ALL">Tất cả trạng thái</option>
        <option value="ACTIVE">Active</option>
        <option value="INACTIVE">Inactive</option>
      </select>
      <select
        value={sortBy}
        onChange={(e) => onSortByChange(e.target.value)}
        style={inputStyle}
      >
        {sortOptions.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      <select
        value={sortDir}
        onChange={(e) => onSortDirChange(e.target.value as "asc" | "desc")}
        style={inputStyle}
      >
        <option value="desc">Giảm dần</option>
        <option value="asc">Tăng dần</option>
      </select>
      <div style={{ display: "flex", gap: "8px" }}>
        <button
          type="button"
          onClick={onApply}
          style={{ ...primaryButton, flex: 1, justifyContent: "center" }}
        >
          Lọc
        </button>
        <button type="button" onClick={onReset} style={secondaryButton}>
          Reset
        </button>
      </div>
    </div>
  </div>
);
