import React from "react";
import { inputStyle, secondaryButton } from "./adminStyles";

export const LabeledField: React.FC<{
  label: string;
  children: React.ReactNode;
}> = ({ label, children }) => (
  <label style={{ display: "grid", gap: "8px" }}>
    <span
      style={{
        fontSize: "0.85rem",
        fontWeight: 600,
        color: "var(--text-secondary)",
      }}
    >
      {label}
    </span>
    {children}
  </label>
);

export const CheckboxField: React.FC<{
  label: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}> = ({ label, checked, onChange }) => (
  <label
    style={{
      display: "flex",
      alignItems: "center",
      gap: "10px",
      color: "var(--text-secondary)",
    }}
  >
    <input
      type="checkbox"
      checked={checked}
      onChange={(e) => onChange(e.target.checked)}
    />
    {label}
  </label>
);

export const RoleBadge: React.FC<{ role: "SUPER_ADMIN" | "USER" }> = ({
  role,
}) => (
  <span
    style={{
      display: "inline-flex",
      alignItems: "center",
      padding: "6px 10px",
      borderRadius: "999px",
      fontSize: "0.78rem",
      fontWeight: 700,
      color: role === "SUPER_ADMIN" ? "#fb923c" : "#38bdf8",
      background:
        role === "SUPER_ADMIN"
          ? "rgba(249,115,22,0.12)"
          : "rgba(56,189,248,0.12)",
    }}
  >
    {role}
  </span>
);

export const StatusBadge: React.FC<{ active: boolean }> = ({ active }) => (
  <span
    style={{
      display: "inline-flex",
      alignItems: "center",
      padding: "6px 10px",
      borderRadius: "999px",
      fontSize: "0.78rem",
      fontWeight: 700,
      color: active ? "#34d399" : "#f87171",
      background: active ? "rgba(16,185,129,0.12)" : "rgba(239,68,68,0.12)",
    }}
  >
    {active ? "Active" : "Inactive"}
  </span>
);

export const PaginationBar: React.FC<{
  page: number;
  totalPages: number;
  size: number;
  onPageChange: (page: number) => void;
  onSizeChange: (size: number) => void;
}> = ({ page, totalPages, size, onPageChange, onSizeChange }) => {
  const canGoPrev = page > 0;
  const canGoNext = page + 1 < totalPages;

  return (
    <div
      style={{
        marginTop: "20px",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        gap: "12px",
        flexWrap: "wrap",
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "10px",
          color: "var(--text-muted)",
        }}
      >
        <span>Rows per page</span>
        <select
          value={String(size)}
          onChange={(e) => onSizeChange(Number(e.target.value))}
          style={{ ...inputStyle, width: "92px" }}
        >
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="50">50</option>
        </select>
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
        <span style={{ color: "var(--text-muted)" }}>
          Page {totalPages === 0 ? 0 : page + 1} / {totalPages}
        </span>
        <button
          type="button"
          disabled={!canGoPrev}
          onClick={() => onPageChange(page - 1)}
          style={secondaryButton}
        >
          Trước
        </button>
        <button
          type="button"
          disabled={!canGoNext}
          onClick={() => onPageChange(page + 1)}
          style={secondaryButton}
        >
          Sau
        </button>
      </div>
    </div>
  );
};
