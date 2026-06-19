import React, { useEffect, useState } from "react";
import { Edit3, Save, ServerCrash } from "lucide-react";
import { useToast } from "../../context/useToast";
import { incidentsApi } from "../../services/api/incidents.api";
import {
  IncidentDto,
  IncidentHealthCheckResultDto,
} from "../../types/incident.types";
import { getErrorMessage } from "../../utils/error";
import {
  formInputStyle,
  formPrimaryButtonStyle,
  formSecondaryButtonStyle,
  formTextareaStyle,
} from "../shared/formStyles";
import {
  formatDateTime,
  getResultAccent,
  statusBadge,
} from "./incidentStyles";

const DetailMetric: React.FC<{ label: string; value: string }> = ({
  label,
  value,
}) => (
  <div
    style={{
      display: "grid",
      gap: "6px",
      padding: "14px 16px",
      borderRadius: "14px",
      border: "1px solid var(--card-border)",
      background: "rgba(255,255,255,0.02)",
    }}
  >
    <span
      style={{
        fontSize: "0.78rem",
        color: "var(--text-muted)",
        textTransform: "uppercase",
      }}
    >
      {label}
    </span>
    <span style={{ color: "var(--text-primary)" }}>{value}</span>
  </div>
);

const HealthResultCard: React.FC<{
  result: IncidentHealthCheckResultDto;
}> = ({ result }) => {
  const accent = getResultAccent(result);

  return (
    <div
      style={{
        display: "grid",
        gap: "12px",
        padding: "16px",
        borderRadius: "16px",
        border: `1px solid ${accent.border}`,
        background: "rgba(15, 23, 42, 0.18)",
      }}
    >
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
          <div style={{ fontWeight: 700 }}>Kết quả check #{result.id}</div>
          <div style={{ fontSize: "0.82rem", color: "var(--text-muted)" }}>
            {formatDateTime(result.checkedAt)}
          </div>
        </div>
        <span
          style={{
            padding: "5px 10px",
            borderRadius: "999px",
            background: accent.chipBg,
            color: accent.chipColor,
            fontWeight: 700,
            fontSize: "0.78rem",
          }}
        >
          {result.status || "UNKNOWN"}
        </span>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(140px, 1fr))",
          gap: "12px",
        }}
      >
        <DetailMetric
          label="HTTP code"
          value={result.httpStatusCode?.toString() || "N/A"}
        />
        <DetailMetric
          label="Response time"
          value={
            result.responseTimeMillis !== null
              ? `${result.responseTimeMillis} ms`
              : "N/A"
          }
        />
        <DetailMetric
          label="Success"
          value={result.success === null ? "N/A" : result.success ? "Yes" : "No"}
        />
        <DetailMetric label="Node" value={result.nodeId || "N/A"} />
      </div>

      <div style={{ display: "grid", gap: "10px" }}>
        <div>
          <div
            style={{
              fontSize: "0.78rem",
              color: "var(--text-muted)",
              textTransform: "uppercase",
              marginBottom: "6px",
            }}
          >
            Error message
          </div>
          <div
            style={{
              padding: "12px 14px",
              borderRadius: "12px",
              background: "rgba(255,255,255,0.04)",
              color: "var(--text-secondary)",
              whiteSpace: "pre-wrap",
              wordBreak: "break-word",
            }}
          >
            {result.errorMessage || "Không có lỗi kỹ thuật được ghi nhận."}
          </div>
        </div>

        {result.responsePayload && (
          <details
            style={{
              border: "1px solid var(--card-border)",
              borderRadius: "12px",
              padding: "10px 12px",
            }}
          >
            <summary style={{ cursor: "pointer", fontWeight: 600 }}>
              Xem response payload
            </summary>
            <pre
              style={{
                margin: "12px 0 0 0",
                whiteSpace: "pre-wrap",
                wordBreak: "break-word",
                color: "var(--text-secondary)",
                fontFamily: "monospace",
                fontSize: "0.82rem",
              }}
            >
              {result.responsePayload}
            </pre>
          </details>
        )}
      </div>
    </div>
  );
};

interface IncidentDetailPanelProps {
  incident: IncidentDto | null;
  loading: boolean;
  onIncidentUpdated: (incident: IncidentDto) => void;
}

export const IncidentDetailPanel: React.FC<IncidentDetailPanelProps> = ({
  incident,
  loading,
  onIncidentUpdated,
}) => {
  const { showToast } = useToast();
  const [rootCauseDraft, setRootCauseDraft] = useState("");
  const [editingRootCause, setEditingRootCause] = useState(false);
  const [savingRootCause, setSavingRootCause] = useState(false);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [resultsError, setResultsError] = useState<string | null>(null);
  const [results, setResults] = useState<IncidentHealthCheckResultDto[]>([]);

  useEffect(() => {
    setRootCauseDraft(incident?.rootCause || "");
    setEditingRootCause(false);
  }, [incident]);

  useEffect(() => {
    if (!incident) {
      setResults([]);
      setResultsError(null);
      setResultsLoading(false);
      return;
    }

    const loadResults = async () => {
      setResultsLoading(true);
      setResultsError(null);
      setResults([]);
      try {
        const data = await incidentsApi.getIncidentResults(incident.id);
        setResults(data);
      } catch (error) {
        setResultsError(
          getErrorMessage(error, "Không thể tải các kết quả check liên quan."),
        );
      } finally {
        setResultsLoading(false);
      }
    };

    void loadResults();
  }, [incident]);

  const handleSaveRootCause = async () => {
    if (!incident) {
      return;
    }

    setSavingRootCause(true);
    try {
      const updated = await incidentsApi.updateRootCause(
        incident.id,
        rootCauseDraft.trim() ? rootCauseDraft.trim() : null,
      );
      onIncidentUpdated(updated);
      setEditingRootCause(false);
      showToast({
        title: "Đã cập nhật nguyên nhân gốc",
        description: `Sự cố #${incident.id} đã được lưu ghi chú điều tra.`,
        variant: "success",
      });
    } catch (error) {
      showToast({
        title: "Không thể cập nhật nguyên nhân gốc",
        description: getErrorMessage(
          error,
          "Đã xảy ra lỗi khi lưu nguyên nhân gốc.",
        ),
        variant: "error",
      });
    } finally {
      setSavingRootCause(false);
    }
  };

  return (
    <div
      className="card"
      style={{ display: "flex", flexDirection: "column", gap: "16px" }}
    >
      <div>
        <h2 style={{ margin: 0, fontSize: "1.2rem" }}>Chi tiết sự cố</h2>
      </div>

      {loading ? (
        <div style={{ color: "var(--text-muted)" }}>
          Đang tải chi tiết sự cố...
        </div>
      ) : !incident ? (
        <div
          style={{
            border: "1px dashed var(--card-border)",
            borderRadius: "16px",
            padding: "24px",
            color: "var(--text-muted)",
          }}
        >
          Chưa chọn sự cố nào.
        </div>
      ) : (
        <>
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
                Sự cố #{incident.id} thuộc workspace #{incident.workspaceId}
              </p>
            </div>
            <span
              style={{
                padding: "6px 12px",
                borderRadius: "999px",
                background: (statusBadge[incident.status] ?? statusBadge.OPEN).bg,
                color: (statusBadge[incident.status] ?? statusBadge.OPEN).color,
                fontWeight: 700,
              }}
            >
              {(statusBadge[incident.status] ?? statusBadge.OPEN).label}
            </span>
          </div>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
              gap: "12px",
            }}
          >
            <DetailMetric label="Mức độ" value={incident.severity || "N/A"} />
            <DetailMetric
              label="Số lần lỗi"
              value={String(incident.failureCount ?? "N/A")}
            />
            <DetailMetric
              label="Bắt đầu"
              value={formatDateTime(incident.startedAt)}
            />
            <DetailMetric
              label="Phục hồi"
              value={
                incident.resolvedAt
                  ? formatDateTime(incident.resolvedAt)
                  : "Chưa phục hồi"
              }
            />
          </div>

          <div style={{ display: "grid", gap: "10px" }}>
            <span
              style={{
                fontSize: "0.8rem",
                color: "var(--text-muted)",
                textTransform: "uppercase",
              }}
            >
              Lý do mở sự cố
            </span>
            <div
              style={{
                padding: "14px 16px",
                borderRadius: "14px",
                border: "1px solid var(--card-border)",
                background: "rgba(255,255,255,0.02)",
                color: "var(--text-secondary)",
                lineHeight: 1.6,
              }}
            >
              {incident.reason || "Chưa có mô tả"}
            </div>
          </div>

          <div style={{ display: "grid", gap: "12px" }}>
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
                <div style={{ fontWeight: 700 }}>Nguyên nhân gốc</div>
                <div style={{ color: "var(--text-muted)", fontSize: "0.84rem" }}>
                  Ghi chú phần điều tra và nguyên nhân gốc rễ của sự cố.
                </div>
              </div>
              {!editingRootCause && (
                <button
                  type="button"
                  onClick={() => setEditingRootCause(true)}
                  style={{
                    ...formSecondaryButtonStyle,
                    display: "inline-flex",
                    alignItems: "center",
                    gap: "8px",
                  }}
                >
                  <Edit3 size={16} />
                  Chỉnh sửa
                </button>
              )}
            </div>

            {editingRootCause ? (
              <div style={{ display: "grid", gap: "12px" }}>
                <textarea
                  value={rootCauseDraft}
                  onChange={(event) => setRootCauseDraft(event.target.value)}
                  placeholder="Ví dụ: DB connection pool bị nghẽn sau deploy, cần tối ưu timeout và retry."
                  style={formTextareaStyle(false)}
                />
                <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
                  <button
                    type="button"
                    onClick={handleSaveRootCause}
                    disabled={savingRootCause}
                    style={{
                      ...formPrimaryButtonStyle,
                      opacity: savingRootCause ? 0.7 : 1,
                    }}
                  >
                    <Save size={16} />
                    {savingRootCause ? "Đang lưu..." : "Lưu nguyên nhân gốc"}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setRootCauseDraft(incident.rootCause || "");
                      setEditingRootCause(false);
                    }}
                    style={formSecondaryButtonStyle}
                  >
                    Hủy
                  </button>
                </div>
              </div>
            ) : (
              <textarea
                value={incident.rootCause || "Chưa cập nhật root cause."}
                readOnly
                style={{
                  ...formInputStyle(false),
                  minHeight: "104px",
                  resize: "none",
                  lineHeight: 1.6,
                  color: incident.rootCause
                    ? "var(--text-primary)"
                    : "var(--text-muted)",
                }}
              />
            )}
          </div>

          <div style={{ display: "grid", gap: "12px" }}>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "10px",
              }}
            >
              <ServerCrash
                size={18}
                style={{ color: "var(--accent-color)" }}
              />
              <div>
                <div style={{ fontWeight: 700 }}>Kết quả check gây ra lỗi</div>
              </div>
            </div>

            {resultsLoading ? (
              <div style={{ color: "var(--text-muted)" }}>
                Đang tải kết quả check...
              </div>
            ) : resultsError ? (
              <div
                style={{
                  border: "1px solid rgba(239, 68, 68, 0.24)",
                  borderRadius: "14px",
                  padding: "14px 16px",
                  background: "rgba(239, 68, 68, 0.08)",
                  color: "var(--error-color)",
                }}
              >
                {resultsError}
              </div>
            ) : results.length > 0 ? (
              <div style={{ display: "grid", gap: "12px" }}>
                {results.map((result) => (
                  <HealthResultCard key={result.id} result={result} />
                ))}
              </div>
            ) : (
              <div
                style={{
                  border: "1px dashed var(--card-border)",
                  borderRadius: "14px",
                  padding: "18px",
                  color: "var(--text-muted)",
                }}
              >
                Sự cố này chưa ghi nhận kết quả check liên quan.
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};
