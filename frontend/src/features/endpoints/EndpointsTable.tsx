import React from "react";
import {
  Activity,
  BellRing,
  Clock3,
  Edit2,
  Globe,
  ShieldCheck,
  Trash2,
} from "lucide-react";
import { EndpointDto, EndpointStatus } from "../../types/endpoint.types";

const dateFormatter = new Intl.DateTimeFormat("vi-VN", {
  dateStyle: "short",
  timeStyle: "short",
});

const formatDateTime = (value?: string) => {
  if (!value) {
    return "Chua co";
  }

  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) {
    return value;
  }

  return dateFormatter.format(parsed);
};

const formatEnvironment = (value?: string) => {
  switch (value) {
    case "PRODUCTION":
      return "Production";
    case "STAGING":
      return "Staging";
    case "DEVELOPMENT":
      return "Development";
    default:
      return value || "Chua ro";
  }
};

const truncateUrl = (value: string) =>
  value.length > 52 ? `${value.slice(0, 52)}...` : value;

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
    <div style={{ overflowX: "auto" }}>
    <table
      style={{
        width: "100%",
        borderCollapse: "collapse",
        textAlign: "left",
        tableLayout: "auto",
        minWidth: "1120px",
      }}
    >
      <colgroup>
        <col style={{ width: "42%" }} />
        <col style={{ width: "16%" }} />
        <col style={{ width: "22%" }} />
        <col style={{ width: "14%" }} />
        <col style={{ width: "6%" }} />
      </colgroup>
      <thead>
        <tr
          style={{
            borderBottom: "1px solid var(--card-border)",
            background: "var(--bg-secondary)",
          }}
        >
          <th style={thStyle}>Tên Endpoint</th>
          <th style={thStyle}>Sức khỏe</th>
          <th style={thStyle}>Cấu hình</th>
          <th style={thStyle}>Lịch chạy</th>
          <th style={{ ...thStyle, textAlign: "right" }}>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        {loading ? (
          <tr>
            <td colSpan={5} style={emptyCellStyle}>
              <Activity
                size={24}
                className="spin"
                style={{ margin: "0 auto 12px" }}
              />
              Đang tải...
            </td>
          </tr>
        ) : endpoints.length === 0 ? (
          <tr>
            <td colSpan={5} style={emptyCellStyle}>
              Không tìm thấy endpoint nào.
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
              <td style={{ ...cellStyle, paddingRight: "36px" }}>
                <div
                  style={{ display: "flex", alignItems: "flex-start", gap: "12px" }}
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
                      style={{
                        fontSize: "0.8rem",
                        color: "var(--text-muted)",
                        lineHeight: 1.5,
                        wordBreak: "break-all",
                      }}
                      title={endpoint.url}
                    >
                      {endpoint.method} • {truncateUrl(endpoint.url)}
                    </div>
                    <div
                      style={{
                        display: "flex",
                        gap: "8px",
                        flexWrap: "wrap",
                        marginTop: "8px",
                      }}
                    >
                      <span style={chipStyle("var(--bg-secondary)", "var(--text-secondary)")}>
                        {endpoint.checkType}
                      </span>
                      <span style={chipStyle("var(--bg-secondary)", "var(--text-secondary)")}>
                        {formatEnvironment(endpoint.environment)}
                      </span>
                      {endpoint.tags.slice(0, 2).map((tag) => (
                        <span
                          key={tag}
                          style={chipStyle("var(--accent-bg)", "var(--accent-color)")}
                        >
                          #{tag}
                        </span>
                      ))}
                      {endpoint.tags.length > 2 && (
                        <span style={chipStyle("var(--bg-secondary)", "var(--text-muted)")}>
                          +{endpoint.tags.length - 2} thẻ
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </td>
              <td style={{ ...cellStyle, ...separatedCellStyle }}>
                <div style={{ display: "grid", gap: "10px" }}>
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
                      width: "fit-content",
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
                  <span
                    style={{
                      display: "inline-flex",
                      alignItems: "center",
                      gap: "8px",
                      color: endpoint.isActive
                        ? "var(--success-color)"
                        : "var(--text-muted)",
                      fontSize: "0.85rem",
                      fontWeight: 600,
                    }}
                  >
                    <ShieldCheck size={16} />
                    {endpoint.isActive ? "Đang theo dõi" : "Tạm dừng"}
                  </span>
                </div>
              </td>
              <td style={{ ...cellStyle, ...separatedCellStyle }}>
                <div style={{ display: "grid", gap: "10px" }}>
                  <div style={configItemStyle}>
                    <Clock3 size={16} />
                    <div>
                      <div style={configLabelStyle}>Check policy</div>
                      <div style={configValueStyle}>
                        #{endpoint.policyId} · {endpoint.intervalSeconds ?? "-"}s /{" "}
                        {endpoint.timeoutMillis ?? "-"}ms
                      </div>
                    </div>
                  </div>
                  <div style={configItemStyle}>
                    <BellRing size={16} />
                    <div>
                      <div style={configLabelStyle}>Alert rules</div>
                      <div style={configValueStyle}>
                        {endpoint.alertRuleIds.length} rule
                        {endpoint.alertRuleIds.length === 1 ? "" : "s"} · Retry{" "}
                        {endpoint.retryCount ?? "-"}
                        {endpoint.expectedStatusCode
                          ? ` · HTTP ${endpoint.expectedStatusCode}`
                          : ""}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
              <td style={{ ...cellStyle, ...separatedCellStyle }}>
                <div style={{ display: "grid", gap: "8px" }}>
                  <div>
                    <div style={configLabelStyle}>Lần kiểm tra cuối</div>
                    <div style={configValueStyle}>
                      {formatDateTime(endpoint.lastCheckedAt)}
                    </div>
                  </div>
                  <div>
                    <div style={configLabelStyle}>Lần chạy tiếp theo</div>
                    <div style={configValueStyle}>
                      {formatDateTime(endpoint.nextRunAt)}
                    </div>
                  </div>
                </div>
              </td>
              <td
                style={{
                  ...cellStyle,
                  ...separatedCellStyle,
                  textAlign: "right",
                  whiteSpace: "nowrap",
                }}
              >
                <div
                  style={{
                    display: "flex",
                    gap: "8px",
                    justifyContent: "flex-end",
                  }}
                >
                  <button
                    onClick={() => onEdit(endpoint)}
                    style={iconButton("var(--accent-color)")}
                    title="Chỉnh sửa"
                  >
                    <Edit2 size={18} />
                  </button>
                  <button
                    onClick={() => onDelete(endpoint.id)}
                    style={iconButton("var(--error-color)")}
                    title="Xóa"
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
  </div>
);

const thStyle: React.CSSProperties = {
  padding: "16px 24px",
  color: "var(--text-muted)",
  fontWeight: 600,
  fontSize: "0.85rem",
  whiteSpace: "nowrap",
};

const emptyCellStyle: React.CSSProperties = {
  padding: "40px",
  textAlign: "center",
  color: "var(--text-muted)",
};

const cellStyle: React.CSSProperties = {
  padding: "18px 24px",
  verticalAlign: "top",
};

const separatedCellStyle: React.CSSProperties = {
  borderLeft: "1px solid rgba(148, 163, 184, 0.08)",
};

const chipStyle = (
  background: string,
  color: string,
): React.CSSProperties => ({
  display: "inline-flex",
  alignItems: "center",
  padding: "4px 8px",
  borderRadius: "999px",
  background,
  color,
  fontSize: "0.75rem",
  fontWeight: 600,
});

const configItemStyle: React.CSSProperties = {
  display: "flex",
  gap: "10px",
  alignItems: "flex-start",
  color: "var(--text-secondary)",
};

const configLabelStyle: React.CSSProperties = {
  fontSize: "0.75rem",
  color: "var(--text-muted)",
  marginBottom: "2px",
};

const configValueStyle: React.CSSProperties = {
  fontSize: "0.84rem",
  color: "var(--text-primary)",
  fontWeight: 600,
};

const iconButton = (color: string): React.CSSProperties => ({
  background: "none",
  border: "none",
  color,
  cursor: "pointer",
  padding: "6px",
});
