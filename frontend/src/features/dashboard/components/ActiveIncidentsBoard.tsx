import React from "react";
import { Zap, CheckCircle, Clock } from "lucide-react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { IncidentSummaryDto } from "../types";

export const ActiveIncidentsBoard: React.FC<{
  incidents: IncidentSummaryDto[];
  count: number;
}> = ({ incidents, count }) => {
  const { t } = useTranslation();

  return (
    <div className="card" style={{ display: "flex", flexDirection: "column" }}>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "12px",
          marginBottom: "24px",
        }}
      >
        <div
          style={{
            width: "40px",
            height: "40px",
            borderRadius: "10px",
            background: "rgba(239, 68, 68, 0.15)",
            color: "#ef4444",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Zap size={20} />
        </div>
        <div>
          <h2
            style={{ fontSize: "1.2rem", fontWeight: 600, margin: "0 0 4px 0" }}
          >
            {t("dashboard.incidentsTitle", "Sự cố đang mở")}
          </h2>
          <p
            style={{
              color: "var(--text-muted)",
              fontSize: "0.85rem",
              margin: 0,
            }}
          >
            {count} {t("dashboard.incidentsDesc", "sự cố cần chú ý")}
          </p>
        </div>
        <Link
          to="/app/incidents?status=OPEN"
          style={{
            marginLeft: "auto",
            color: "var(--accent-color)",
            fontSize: "0.85rem",
            fontWeight: 600,
            textDecoration: "none",
          }}
        >
          Xem tất cả
        </Link>
      </div>

      <div
        style={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          gap: "16px",
          overflowY: "auto",
        }}
      >
        {incidents && incidents.length > 0 ? (
          incidents.map((incident) => (
            <div
              key={incident.id}
              style={{
                background: "rgba(128, 128, 128, 0.05)",
                border: "1px solid var(--card-border)",
                borderRadius: "12px",
                padding: "16px",
                borderLeft: `4px solid ${incident.severity === "CRITICAL" ? "var(--error-color)" : "var(--warning-color)"}`,
              }}
            >
              <Link
                to={`/app/incidents?incidentId=${incident.id}&endpointId=${incident.endpointId}&status=OPEN`}
                style={{
                  color: "inherit",
                  textDecoration: "none",
                  display: "block",
                }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "flex-start",
                    marginBottom: "8px",
                  }}
                >
                  <span style={{ fontSize: "0.9rem", fontWeight: 600 }}>
                    {incident.endpointName}
                  </span>
                  <span
                    style={{
                      fontSize: "0.7rem",
                      padding: "2px 8px",
                      borderRadius: "12px",
                      background:
                        incident.severity === "CRITICAL"
                          ? "rgba(239, 68, 68, 0.2)"
                          : "rgba(245, 158, 11, 0.2)",
                      color:
                        incident.severity === "CRITICAL"
                          ? "#ef4444"
                          : "#f59e0b",
                      fontWeight: 600,
                    }}
                  >
                    {incident.severity}
                  </span>
                </div>
                <p
                  style={{
                    color: "var(--text-secondary)",
                    fontSize: "0.85rem",
                    margin: "0 0 8px 0",
                    lineHeight: 1.4,
                  }}
                >
                  {incident.reason}
                </p>
                <div
                  style={{
                    fontSize: "0.75rem",
                    color: "var(--text-muted)",
                    display: "flex",
                    alignItems: "center",
                    gap: "6px",
                  }}
                >
                  <Clock size={12} />
                  {new Date(incident.startedAt).toLocaleString("vi-VN")}
                </div>
              </Link>
            </div>
          ))
        ) : (
          <div
            style={{
              textAlign: "center",
              padding: "40px 0",
              color: "var(--text-muted)",
            }}
          >
            <CheckCircle
              size={40}
              style={{
                color: "var(--success-color)",
                opacity: 0.5,
                marginBottom: "16px",
              }}
            />
            <div>{t("dashboard.allGood", "Hệ thống hoạt động ổn định.")}</div>
          </div>
        )}
      </div>
    </div>
  );
};
