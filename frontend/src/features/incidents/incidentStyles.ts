import type React from "react";
import { IncidentHealthCheckResultDto, IncidentStatus } from "../../types/incident.types";

export const severityColor: Record<string, string> = {
  CRITICAL: "var(--error-color)",
  WARNING: "var(--warning-color)",
  INFO: "var(--accent-color)",
};

export const statusBadge: Record<
  IncidentStatus,
  { label: string; bg: string; color: string }
> = {
  OPEN: {
    label: "Đang mở",
    bg: "rgba(239, 68, 68, 0.15)",
    color: "var(--error-color)",
  },
  RESOLVED: {
    label: "Đã phục hồi",
    bg: "rgba(16, 185, 129, 0.15)",
    color: "var(--success-color)",
  },
  CLOSED: {
    label: "Đã đóng",
    bg: "rgba(148, 163, 184, 0.15)",
    color: "var(--text-secondary)",
  },
};

export const inputStyle: React.CSSProperties = {
  width: "100%",
  padding: "12px 14px",
  borderRadius: "12px",
  border: "1px solid var(--card-border)",
  background: "var(--bg-secondary)",
  color: "var(--text-primary)",
};

export const formatDateTime = (value: string | null | undefined) => {
  if (!value) {
    return "N/A";
  }

  return new Date(value).toLocaleString("vi-VN", {
    hour12: false,
  });
};

export const getResultAccent = (result: IncidentHealthCheckResultDto) => {
  if (result.status === "DOWN" || result.success === false) {
    return {
      border: "rgba(239, 68, 68, 0.22)",
      chipBg: "rgba(239, 68, 68, 0.14)",
      chipColor: "var(--error-color)",
    };
  }

  if (result.status === "DEGRADED") {
    return {
      border: "rgba(245, 158, 11, 0.24)",
      chipBg: "rgba(245, 158, 11, 0.14)",
      chipColor: "var(--warning-color)",
    };
  }

  return {
    border: "rgba(56, 189, 248, 0.2)",
    chipBg: "rgba(56, 189, 248, 0.14)",
    chipColor: "var(--accent-color)",
  };
};
