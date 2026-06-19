import React from "react";
import { ChevronRight } from "lucide-react";
import { IncidentDto } from "../../types/incident.types";
import { formatDateTime, severityColor, statusBadge } from "./incidentStyles";

const IncidentRow: React.FC<{
  incident: IncidentDto;
  selected: boolean;
  onSelect: (incident: IncidentDto) => void;
}> = ({ incident, selected, onSelect }) => {
  const badge = statusBadge[incident.status] ?? statusBadge.OPEN;
  const severity = incident.severity
    ? (severityColor[incident.severity] ?? "var(--accent-color)")
    : "var(--text-muted)";

  return (
    <button
      type="button"
      onClick={() => onSelect(incident)}
      style={{
        textAlign: "left",
        width: "100%",
        padding: "18px",
        borderRadius: "16px",
        border: selected
          ? "1px solid rgba(56, 189, 248, 0.35)"
          : "1px solid var(--card-border)",
        background: selected
          ? "rgba(56, 189, 248, 0.08)"
          : "rgba(255, 255, 255, 0.02)",
        color: "inherit",
        cursor: "pointer",
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          gap: "12px",
          marginBottom: "10px",
        }}
      >
        <div>
          <div style={{ fontWeight: 700 }}>{incident.endpointName}</div>
          <div style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>
            Sự cố #{incident.id}
          </div>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <span
            style={{
              padding: "4px 10px",
              borderRadius: "999px",
              background: badge.bg,
              color: badge.color,
              fontSize: "0.75rem",
              fontWeight: 700,
            }}
          >
            {badge.label}
          </span>
          <ChevronRight size={16} style={{ color: "var(--text-muted)" }} />
        </div>
      </div>

      <div style={{ color: "var(--text-secondary)", marginBottom: "10px" }}>
        {incident.reason || "Không có mô tả chi tiết"}
      </div>

      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: "10px 14px",
          fontSize: "0.8rem",
          color: "var(--text-muted)",
        }}
      >
        <span>Mở lúc: {formatDateTime(incident.startedAt)}</span>
        <span style={{ color: severity }}>
          Mức độ: {incident.severity || "N/A"}
        </span>
        <span>Số lần lỗi: {incident.failureCount ?? "N/A"}</span>
      </div>
    </button>
  );
};

interface IncidentListPanelProps {
  incidents: IncidentDto[];
  totalItems: number;
  loading: boolean;
  selectedIncidentId?: number;
  onSelect: (incident: IncidentDto) => void;
}

export const IncidentListPanel: React.FC<IncidentListPanelProps> = ({
  incidents,
  totalItems,
  loading,
  selectedIncidentId,
  onSelect,
}) => (
  <div
    className="card"
    style={{ display: "flex", flexDirection: "column", gap: "16px" }}
  >
    <div>
      <h2 style={{ margin: 0, fontSize: "1.2rem" }}>Danh sách sự cố</h2>
      <p style={{ margin: "6px 0 0 0", color: "var(--text-muted)" }}>
        {loading ? "Đang tải..." : `${totalItems} sự cố phù hợp bộ lọc hiện tại`}
      </p>
    </div>

    <div style={{ display: "grid", gap: "12px" }}>
      {incidents.map((incident) => (
        <IncidentRow
          key={incident.id}
          incident={incident}
          selected={selectedIncidentId === incident.id}
          onSelect={onSelect}
        />
      ))}

      {!loading && incidents.length === 0 && (
        <div
          style={{
            border: "1px dashed var(--card-border)",
            borderRadius: "16px",
            padding: "28px",
            textAlign: "center",
            color: "var(--text-muted)",
          }}
        >
          Chưa có sự cố nào khớp bộ lọc hiện tại.
        </div>
      )}
    </div>
  </div>
);
