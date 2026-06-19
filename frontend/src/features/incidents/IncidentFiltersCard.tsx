import React from "react";
import { Filter } from "lucide-react";
import { EndpointDto } from "../../types/endpoint.types";
import { IncidentStatus } from "../../types/incident.types";
import { inputStyle } from "./incidentStyles";

interface IncidentFiltersCardProps {
  endpointOptions: EndpointDto[];
  selectedEndpointId?: number;
  statusFilter: IncidentStatus | "";
  onStatusChange: (value: string) => void;
  onEndpointChange: (value: string) => void;
}

export const IncidentFiltersCard: React.FC<IncidentFiltersCardProps> = ({
  endpointOptions,
  selectedEndpointId,
  statusFilter,
  onStatusChange,
  onEndpointChange,
}) => (
  <div
    className="card"
    style={{ display: "flex", flexDirection: "column", gap: "16px" }}
  >
    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
      <Filter size={18} style={{ color: "var(--accent-color)" }} />
      <strong>Bộ lọc sự cố</strong>
    </div>
    <div
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
        gap: "16px",
      }}
    >
      <label style={{ display: "grid", gap: "8px" }}>
        <span style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
          Trạng thái sự cố
        </span>
        <select
          value={statusFilter}
          onChange={(event) => onStatusChange(event.target.value)}
          style={inputStyle}
        >
          <option value="">Tất cả</option>
          <option value="OPEN">Đang mở</option>
          <option value="RESOLVED">Đã phục hồi</option>
          <option value="CLOSED">Đã đóng</option>
        </select>
      </label>

      <label style={{ display: "grid", gap: "8px" }}>
        <span style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
          Endpoint
        </span>
        <select
          value={selectedEndpointId ?? ""}
          onChange={(event) => onEndpointChange(event.target.value)}
          style={inputStyle}
        >
          <option value="">Tất cả endpoint</option>
          {endpointOptions.map((endpoint) => (
            <option key={endpoint.id} value={endpoint.id}>
              {endpoint.name}
            </option>
          ))}
        </select>
      </label>
    </div>
  </div>
);
