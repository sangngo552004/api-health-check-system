import React from "react";
import {
  Activity,
  Edit2,
  Globe,
  PauseCircle,
  PlayCircle,
  Trash2,
} from "lucide-react";
import { EndpointDto, EndpointStatus } from "../../types/endpoint.types";

export const EndpointsTable: React.FC<{
  loading: boolean;
  endpoints: EndpointDto[];
  getStatusColor: (status: EndpointStatus) => string;
  getStatusBg: (status: EndpointStatus) => string;
  onEdit: (endpoint: EndpointDto) => void;
  onDelete: (endpointId: number) => void;
}> = ({
  loading,
  endpoints,
  getStatusColor,
  getStatusBg,
  onEdit,
  onDelete,
}) => (
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
          <th style={thStyle}>Ten Endpoint</th>
          <th style={thStyle}>Trang thai</th>
          <th style={thStyle}>Moi truong</th>
          <th style={{ ...thStyle, textAlign: "right" }}>Thao tac</th>
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
              Dang tai...
            </td>
          </tr>
        ) : endpoints.length === 0 ? (
          <tr>
            <td colSpan={4} style={emptyCellStyle}>
              Khong tim thay endpoint nao.
            </td>
          </tr>
        ) : (
          endpoints.map((endpoint) => (
            <tr
              key={endpoint.id}
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
                      borderRadius: "10px",
                      background: "var(--accent-bg)",
                      color: "var(--accent-color)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <Globe size={20} />
                  </div>
                  <div>
                    <div
                      style={{
                        fontWeight: 600,
                        color: "var(--text-primary)",
                        marginBottom: "4px",
                      }}
                    >
                      {endpoint.name}
                    </div>
                    <div
                      style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}
                    >
                      {endpoint.method} • {endpoint.url}
                    </div>
                  </div>
                </div>
              </td>
              <td style={{ padding: "16px 24px" }}>
                <span
                  style={{
                    display: "inline-flex",
                    alignItems: "center",
                    gap: "6px",
                    padding: "4px 10px",
                    borderRadius: "20px",
                    fontSize: "0.75rem",
                    fontWeight: 700,
                    color: getStatusColor(endpoint.status),
                    background: getStatusBg(endpoint.status),
                  }}
                >
                  <span
                    style={{
                      width: "6px",
                      height: "6px",
                      borderRadius: "50%",
                      background: getStatusColor(endpoint.status),
                    }}
                  />
                  {endpoint.status}
                </span>
              </td>
              <td style={{ padding: "16px 24px" }}>
                <span
                  style={{
                    fontSize: "0.85rem",
                    color: "var(--text-secondary)",
                    background: "var(--bg-secondary)",
                    padding: "4px 8px",
                    borderRadius: "6px",
                  }}
                >
                  {endpoint.environment}
                </span>
              </td>
              <td style={{ padding: "16px 24px", textAlign: "right" }}>
                <div
                  style={{
                    display: "flex",
                    gap: "8px",
                    justifyContent: "flex-end",
                  }}
                >
                  <button
                    style={iconButton("var(--text-muted)")}
                    title="Tam dung / Chay"
                  >
                    {endpoint.isActive ? (
                      <PauseCircle size={18} />
                    ) : (
                      <PlayCircle size={18} />
                    )}
                  </button>
                  <button
                    onClick={() => onEdit(endpoint)}
                    style={iconButton("var(--accent-color)")}
                    title="Chinh sua"
                  >
                    <Edit2 size={18} />
                  </button>
                  <button
                    onClick={() => onDelete(endpoint.id)}
                    style={iconButton("var(--error-color)")}
                    title="Xoa"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
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

const iconButton = (color: string): React.CSSProperties => ({
  background: "none",
  border: "none",
  color,
  cursor: "pointer",
  padding: "6px",
});
