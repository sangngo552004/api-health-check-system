import React from "react";
import { Search } from "lucide-react";
import {
  inputStyle,
  primaryButton,
  secondaryButton,
} from "../components/adminStyles";
import { WorkspaceStatusFilter } from "./workspacePage.types";

const workspaceSortOptions = [
  { value: "createdAt", label: "Mới nhất" },
  { value: "name", label: "Tên workspace" },
  { value: "slug", label: "Slug" },
];

export const WorkspaceFilters: React.FC<{
  searchInput: string;
  status: WorkspaceStatusFilter;
  sortBy: string;
  sortDir: "asc" | "desc";
  onSearchInputChange: (value: string) => void;
  onStatusChange: (value: WorkspaceStatusFilter) => void;
  onSortByChange: (value: string) => void;
  onSortDirChange: (value: "asc" | "desc") => void;
  onApply: () => void;
  onReset: () => void;
}> = ({
  searchInput,
  status,
  sortBy,
  sortDir,
  onSearchInputChange,
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
        gridTemplateColumns: "minmax(260px, 2fr) repeat(4, minmax(120px, 1fr))",
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
          placeholder="Tìm theo tên, slug hoặc mô tả"
          style={{ ...inputStyle, paddingLeft: "36px" }}
        />
      </div>
      <select
        value={status}
        onChange={(e) =>
          onStatusChange(e.target.value as WorkspaceStatusFilter)
        }
        style={inputStyle}
      >
        <option value="ALL">Tất cả trạng thái</option>
        <option value="ACTIVE">Đang hoạt động</option>
        <option value="INACTIVE">Ngưng hoạt động</option>
      </select>
      <select
        value={sortBy}
        onChange={(e) => onSortByChange(e.target.value)}
        style={inputStyle}
      >
        {workspaceSortOptions.map((option) => (
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
