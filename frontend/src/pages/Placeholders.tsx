import React from "react";
import { useWorkspace } from "../context/useWorkspace";
import { Globe, AlertTriangle, Users } from "lucide-react";
import { useTranslation } from "react-i18next";

export const EndpointsPlaceholder: React.FC = () => {
  const { activeWorkspace } = useWorkspace();
  const { t } = useTranslation();
  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <div style={{ marginBottom: "32px" }}>
        <p className="eyebrow">
          {t("endpoints.subtitle", "Quản lý mục giám sát")}
        </p>
        <h1
          style={{
            color: "var(--text-primary)",
            fontSize: "2rem",
            fontWeight: 700,
            margin: "8px 0 0 0",
          }}
        >
          {t("endpoints.title", "Monitored Endpoints")}
        </h1>
      </div>

      <div
        className="card"
        style={{
          padding: "32px",
          background: "var(--bg-secondary)",
          display: "flex",
          flexDirection: "column",
          gap: "16px",
        }}
      >
        <div
          style={{
            display: "inline-flex",
            alignItems: "center",
            justifyContent: "center",
            width: "48px",
            height: "48px",
            borderRadius: "16px",
            background: "rgba(34, 197, 94, 0.15)",
            color: "var(--success-color)",
          }}
        >
          <Globe size={24} />
        </div>
        <h2 style={{ fontSize: "1.4rem", fontWeight: 600, margin: 0 }}>
          {t("endpoints.listTitle", "Danh sách Endpoint của {{workspace}}", {
            workspace: activeWorkspace?.name || "Workspace",
          })}
        </h2>
        <p
          style={{
            color: "var(--text-secondary)",
            fontSize: "0.95rem",
            lineHeight: 1.6,
            margin: 0,
            maxWidth: "640px",
          }}
        >
          {t(
            "endpoints.placeholderDesc",
            "Tính năng CRUD Endpoint đang được phát triển. Tích hợp Zod và Zustand sẽ nằm tại đây.",
          )}
        </p>
      </div>
    </div>
  );
};

export const IncidentsPlaceholder: React.FC = () => {
  const { t } = useTranslation();
  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <div style={{ marginBottom: "32px" }}>
        <p className="eyebrow">{t("incidents.subtitle", "Lịch sử cảnh báo")}</p>
        <h1
          style={{
            color: "var(--text-primary)",
            fontSize: "2rem",
            fontWeight: 700,
            margin: "8px 0 0 0",
          }}
        >
          {t("incidents.title", "Incidents & Alerts")}
        </h1>
      </div>

      <div
        className="card"
        style={{
          padding: "32px",
          background: "var(--bg-secondary)",
          display: "flex",
          flexDirection: "column",
          gap: "16px",
        }}
      >
        <div
          style={{
            display: "inline-flex",
            alignItems: "center",
            justifyContent: "center",
            width: "48px",
            height: "48px",
            borderRadius: "16px",
            background: "rgba(239, 68, 68, 0.15)",
            color: "var(--error-color)",
          }}
        >
          <AlertTriangle size={24} />
        </div>
        <h2 style={{ fontSize: "1.4rem", fontWeight: 600, margin: 0 }}>
          {t("incidents.listTitle", "Sự cố & Nhật ký cảnh báo hệ thống")}
        </h2>
        <p
          style={{
            color: "var(--text-secondary)",
            fontSize: "0.95rem",
            lineHeight: 1.6,
            margin: 0,
            maxWidth: "640px",
          }}
        >
          {t(
            "incidents.desc",
            "Nhật ký ghi nhận các lần sập cổng/cảnh báo quá tải dựa trên các Strategy HTTP/TCP đã cài đặt.",
          )}
        </p>
      </div>
    </div>
  );
};

export const MembersPlaceholder: React.FC = () => {
  const { t } = useTranslation();
  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <div style={{ marginBottom: "32px" }}>
        <p className="eyebrow">{t("members.subtitle", "Cấu hình Workspace")}</p>
        <h1
          style={{
            color: "var(--text-primary)",
            fontSize: "2rem",
            fontWeight: 700,
            margin: "8px 0 0 0",
          }}
        >
          {t("members.title", "Workspace Members")}
        </h1>
      </div>

      <div
        className="card"
        style={{
          padding: "32px",
          background: "var(--bg-secondary)",
          display: "flex",
          flexDirection: "column",
          gap: "16px",
        }}
      >
        <div
          style={{
            display: "inline-flex",
            alignItems: "center",
            justifyContent: "center",
            width: "48px",
            height: "48px",
            borderRadius: "16px",
            background: "rgba(168, 85, 247, 0.15)",
            color: "#a855f7",
          }}
        >
          <Users size={24} />
        </div>
        <h2 style={{ fontSize: "1.4rem", fontWeight: 600, margin: 0 }}>
          {t("members.listTitle", "Danh sách thành viên & Quyền hạn")}
        </h2>
        <p
          style={{
            color: "var(--text-secondary)",
            fontSize: "0.95rem",
            lineHeight: 1.6,
            margin: 0,
            maxWidth: "640px",
          }}
        >
          {t(
            "members.desc",
            "Quản lý phân quyền nội bộ nhóm nhằm cách ly an toàn tài nguyên.",
          )}
        </p>
      </div>
    </div>
  );
};
