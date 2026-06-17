import React, { useEffect, useMemo, useState } from "react";
import {
  AlertTriangle,
  CheckCircle2,
  ChevronRight,
  Filter,
  Siren,
  Zap,
} from "lucide-react";
import { useSearchParams } from "react-router-dom";
import { useIncidentStore } from "../../store/useIncidentStore";
import { useWorkspace } from "../../context/useWorkspace";
import { PagedResponseDto } from "../../types/common.types";
import { EndpointDto } from "../../types/endpoint.types";
import { IncidentDto, IncidentStatus } from "../../types/incident.types";
import { api } from "../../services/api";

const severityColor: Record<string, string> = {
  CRITICAL: "var(--error-color)",
  WARNING: "var(--warning-color)",
  INFO: "var(--accent-color)",
};

const statusBadge: Record<
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

export const IncidentsList: React.FC = () => {
  const { activeWorkspace } = useWorkspace();
  const [searchParams, setSearchParams] = useSearchParams();
  const {
    incidents,
    selectedIncident,
    loading,
    detailLoading,
    error,
    totalItems,
    filters,
    fetchIncidents,
    fetchIncidentById,
    setFilters,
    clearSelectedIncident,
  } = useIncidentStore();

  const [endpointOptions, setEndpointOptions] = useState<EndpointDto[]>([]);

  const statusFilter =
    (searchParams.get("status") as IncidentStatus | null) ?? "";
  const endpointFilter = searchParams.get("endpointId");
  const selectedEndpointId = endpointFilter
    ? Number(endpointFilter)
    : undefined;

  useEffect(() => {
    setFilters({
      status: statusFilter || "",
      endpointId:
        selectedEndpointId !== undefined && !Number.isNaN(selectedEndpointId)
          ? selectedEndpointId
          : undefined,
    });
  }, [selectedEndpointId, setFilters, statusFilter]);

  useEffect(() => {
    void fetchIncidents(0, 12);
  }, [fetchIncidents, filters]);

  useEffect(() => {
    const loadEndpoints = async () => {
      if (!activeWorkspace) {
        setEndpointOptions([]);
        return;
      }

      try {
        const page = await api.get<PagedResponseDto<EndpointDto>>(
          "/endpoints",
          { params: { page: 0, size: 100 } },
        );
        setEndpointOptions(page.items);
      } catch {
        setEndpointOptions([]);
      }
    };

    void loadEndpoints();
  }, [activeWorkspace]);

  useEffect(() => {
    const incidentId = searchParams.get("incidentId");
    if (!incidentId) {
      clearSelectedIncident();
      return;
    }

    const parsedId = Number(incidentId);
    if (!Number.isNaN(parsedId)) {
      void fetchIncidentById(parsedId);
    }
  }, [clearSelectedIncident, fetchIncidentById, searchParams]);

  const summary = useMemo(
    () => ({
      open: incidents.filter((incident) => incident.status === "OPEN").length,
      resolved: incidents.filter((incident) => incident.status === "RESOLVED")
        .length,
      critical: incidents.filter((incident) => incident.severity === "CRITICAL")
        .length,
    }),
    [incidents],
  );

  if (!activeWorkspace) {
    return (
      <div style={{ color: "var(--text-muted)" }}>
        Vui lòng chọn workspace trước khi xem incidents.
      </div>
    );
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "24px" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-end",
          gap: "16px",
          flexWrap: "wrap",
        }}
      >
        <div>
          <p className="eyebrow">Runtime Incident Feed</p>
          <h1
            style={{ fontSize: "2rem", fontWeight: 700, margin: "0 0 8px 0" }}
          >
            Incidents - {activeWorkspace.name}
          </h1>
          <p style={{ margin: 0, color: "var(--text-secondary)" }}>
            Theo dõi sự cố đang mở, các lần phục hồi gần đây và chi tiết để
            trình bày khi demo.
          </p>
        </div>

        <button
          type="button"
          onClick={() => void fetchIncidents(0, 12)}
          style={{
            padding: "10px 16px",
            borderRadius: "12px",
            border: "1px solid var(--accent-hover)",
            background: "var(--accent-bg)",
            color: "var(--accent-color)",
            cursor: "pointer",
            fontWeight: 600,
          }}
        >
          Làm mới danh sách
        </button>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
          gap: "16px",
        }}
      >
        <SummaryCard
          title="Đang mở"
          value={summary.open}
          icon={<Siren size={20} />}
          tone="rgba(239, 68, 68, 0.12)"
          color="var(--error-color)"
        />
        <SummaryCard
          title="Đã phục hồi"
          value={summary.resolved}
          icon={<CheckCircle2 size={20} />}
          tone="rgba(16, 185, 129, 0.12)"
          color="var(--success-color)"
        />
        <SummaryCard
          title="Critical"
          value={summary.critical}
          icon={<Zap size={20} />}
          tone="rgba(245, 158, 11, 0.12)"
          color="var(--warning-color)"
        />
        <SummaryCard
          title="Tổng đang lọc"
          value={totalItems}
          icon={<AlertTriangle size={20} />}
          tone="rgba(56, 189, 248, 0.12)"
          color="var(--accent-color)"
        />
      </div>

      <div
        className="card"
        style={{ display: "flex", flexDirection: "column", gap: "16px" }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <Filter size={18} style={{ color: "var(--accent-color)" }} />
          <strong>Bộ lọc demo</strong>
        </div>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
            gap: "16px",
          }}
        >
          <label style={{ display: "grid", gap: "8px" }}>
            <span
              style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}
            >
              Trạng thái incident
            </span>
            <select
              value={statusFilter}
              onChange={(event) => {
                const next = new URLSearchParams(searchParams);
                if (event.target.value) {
                  next.set("status", event.target.value);
                } else {
                  next.delete("status");
                }
                setSearchParams(next);
              }}
              style={inputStyle}
            >
              <option value="">Tất cả</option>
              <option value="OPEN">Đang mở</option>
              <option value="RESOLVED">Đã phục hồi</option>
              <option value="CLOSED">Đã đóng</option>
            </select>
          </label>

          <label style={{ display: "grid", gap: "8px" }}>
            <span
              style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}
            >
              Endpoint
            </span>
            <select
              value={selectedEndpointId ?? ""}
              onChange={(event) => {
                const next = new URLSearchParams(searchParams);
                if (event.target.value) {
                  next.set("endpointId", event.target.value);
                } else {
                  next.delete("endpointId");
                }
                setSearchParams(next);
              }}
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

      {error && (
        <div
          className="card"
          style={{
            borderColor: "rgba(239, 68, 68, 0.25)",
            background: "rgba(239, 68, 68, 0.08)",
            color: "var(--error-color)",
          }}
        >
          {error}
        </div>
      )}

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "minmax(0, 1.4fr) minmax(320px, 0.9fr)",
          gap: "24px",
        }}
      >
        <div
          className="card"
          style={{ display: "flex", flexDirection: "column", gap: "16px" }}
        >
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              gap: "12px",
            }}
          >
            <div>
              <h2 style={{ margin: 0, fontSize: "1.2rem" }}>
                Danh sách incidents
              </h2>
              <p style={{ margin: "6px 0 0 0", color: "var(--text-muted)" }}>
                {loading
                  ? "Đang tải..."
                  : `${totalItems} incident phù hợp bộ lọc hiện tại`}
              </p>
            </div>
          </div>

          <div style={{ display: "grid", gap: "12px" }}>
            {incidents.map((incident) => (
              <IncidentRow
                key={incident.id}
                incident={incident}
                selected={selectedIncident?.id === incident.id}
                onSelect={(item) => {
                  const next = new URLSearchParams(searchParams);
                  next.set("incidentId", String(item.id));
                  setSearchParams(next);
                }}
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
                Chưa có incident nào khớp bộ lọc hiện tại.
              </div>
            )}
          </div>
        </div>

        <div
          className="card"
          style={{ display: "flex", flexDirection: "column", gap: "16px" }}
        >
          <div>
            <h2 style={{ margin: 0, fontSize: "1.2rem" }}>Chi tiết incident</h2>
            <p style={{ margin: "6px 0 0 0", color: "var(--text-muted)" }}>
              Chọn một incident để xem đầy đủ thông tin khi trình bày.
            </p>
          </div>

          {detailLoading ? (
            <div style={{ color: "var(--text-muted)" }}>
              Đang tải chi tiết incident...
            </div>
          ) : selectedIncident ? (
            <IncidentDetail incident={selectedIncident} />
          ) : (
            <div
              style={{
                border: "1px dashed var(--card-border)",
                borderRadius: "16px",
                padding: "28px",
                color: "var(--text-muted)",
              }}
            >
              Chưa chọn incident nào. Bạn có thể chọn từ danh sách bên trái để
              xem lý do, mức độ ảnh hưởng và thời điểm phục hồi.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const SummaryCard: React.FC<{
  title: string;
  value: number;
  icon: React.ReactNode;
  tone: string;
  color: string;
}> = ({ title, value, icon, tone, color }) => (
  <div
    className="card"
    style={{
      padding: "18px 20px",
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      gap: "16px",
    }}
  >
    <div>
      <div style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
        {title}
      </div>
      <div style={{ fontSize: "1.8rem", fontWeight: 700 }}>{value}</div>
    </div>
    <div
      style={{
        width: "44px",
        height: "44px",
        borderRadius: "14px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: tone,
        color,
      }}
    >
      {icon}
    </div>
  </div>
);

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
            Incident #{incident.id}
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
          Severity: {incident.severity || "N/A"}
        </span>
        <span>Fail count: {incident.failureCount ?? "N/A"}</span>
      </div>
    </button>
  );
};

const IncidentDetail: React.FC<{ incident: IncidentDto }> = ({ incident }) => {
  const badge = statusBadge[incident.status] ?? statusBadge.OPEN;
  return (
    <div style={{ display: "grid", gap: "16px" }}>
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
          <h3 style={{ margin: 0 }}>{incident.endpointName}</h3>
          <p style={{ margin: "6px 0 0 0", color: "var(--text-muted)" }}>
            Incident #{incident.id} thuộc workspace #{incident.workspaceId}
          </p>
        </div>
        <span
          style={{
            padding: "6px 12px",
            borderRadius: "999px",
            background: badge.bg,
            color: badge.color,
            fontWeight: 700,
          }}
        >
          {badge.label}
        </span>
      </div>

      <DetailItem
        label="Nguyên nhân"
        value={incident.reason || "Chưa có mô tả"}
      />
      <DetailItem label="Severity" value={incident.severity || "N/A"} />
      <DetailItem
        label="Failure count"
        value={String(incident.failureCount ?? "N/A")}
      />
      <DetailItem label="Bắt đầu" value={formatDateTime(incident.startedAt)} />
      <DetailItem
        label="Phục hồi"
        value={
          incident.resolvedAt
            ? formatDateTime(incident.resolvedAt)
            : "Chưa phục hồi"
        }
      />
      <DetailItem
        label="Root cause"
        value={incident.rootCause || "Chưa cập nhật"}
      />
      <DetailItem
        label="Failing result ids"
        value={
          incident.failingResultIds && incident.failingResultIds.length > 0
            ? incident.failingResultIds.join(", ")
            : "Chưa ghi nhận"
        }
      />
    </div>
  );
};

const DetailItem: React.FC<{ label: string; value: string }> = ({
  label,
  value,
}) => (
  <div
    style={{
      display: "grid",
      gap: "6px",
      paddingBottom: "14px",
      borderBottom: "1px solid var(--card-border)",
    }}
  >
    <span
      style={{
        fontSize: "0.8rem",
        color: "var(--text-muted)",
        textTransform: "uppercase",
      }}
    >
      {label}
    </span>
    <span style={{ color: "var(--text-primary)" }}>{value}</span>
  </div>
);

const inputStyle: React.CSSProperties = {
  width: "100%",
  padding: "12px 14px",
  borderRadius: "12px",
  border: "1px solid var(--card-border)",
  background: "rgba(255, 255, 255, 0.04)",
  color: "var(--text-primary)",
};

const formatDateTime = (value: string) =>
  new Date(value).toLocaleString("vi-VN", {
    hour12: false,
  });
