import React, { useEffect, useState } from "react";
import { useEndpointStore } from "../../store/useEndpointStore";
import {
  Globe,
  Plus,
  Search,
  Edit2,
  Trash2,
  Activity,
  PlayCircle,
  PauseCircle,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import {
  EndpointDto,
  EndpointStatus,
  EndpointCreateCommand,
  EndpointUpdateCommand,
} from "../../types/endpoint.types";
import { EndpointForm, EndpointFormData } from "./EndpointForm";
import { getErrorMessage } from "../../utils/error";

export const EndpointsList: React.FC = () => {
  const {
    endpoints,
    loading,
    fetchEndpoints,
    createEndpoint,
    updateEndpoint,
    deleteEndpoint,
  } = useEndpointStore();
  const { t } = useTranslation();
  const [searchTerm, setSearchTerm] = useState("");

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEndpoint, setEditingEndpoint] =
    useState<EndpointUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchEndpoints(0, 100); // Tải tạm 100 items cho nhẹ
  }, [fetchEndpoints]);

  const getStatusColor = (status: EndpointStatus) => {
    switch (status) {
      case "UP":
        return "var(--success-color)";
      case "DOWN":
        return "var(--error-color)";
      case "DEGRADED":
        return "var(--warning-color)";
      default:
        return "var(--text-muted)";
    }
  };

  const getStatusBg = (status: EndpointStatus) => {
    switch (status) {
      case "UP":
        return "rgba(16, 185, 129, 0.15)";
      case "DOWN":
        return "rgba(239, 68, 68, 0.15)";
      case "DEGRADED":
        return "rgba(245, 158, 11, 0.15)";
      default:
        return "rgba(148, 163, 184, 0.15)";
    }
  };

  const filteredEndpoints = endpoints.filter(
    (ep) =>
      ep.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      ep.url.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAdd = () => {
    setEditingEndpoint(null);
    setIsFormOpen(true);
  };

  const handleEdit = (ep: EndpointDto) => {
    setEditingEndpoint({
      id: ep.id,
      name: ep.name,
      url: ep.url,
      method: ep.method,
      environment: ep.environment,
      checkType: ep.checkType,
      isActive: ep.isActive,
      policyId: ep.policyId,
      alertRuleIds: ep.alertRuleIds,
      tags: ep.tags,
      headers: ep.headers,
    });
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (data: EndpointFormData) => {
    setSubmitting(true);
    try {
      const payload: Omit<EndpointCreateCommand, "alertRuleIds" | "headers"> = {
        ...data,
        policyId: data.policyId ?? undefined,
      };

      if (editingEndpoint) {
        await updateEndpoint(editingEndpoint.id, {
          ...payload,
          id: editingEndpoint.id,
          alertRuleIds: [],
          headers: {},
        });
      } else {
        await createEndpoint({ ...payload, alertRuleIds: [], headers: {} });
      }
      setIsFormOpen(false);
    } catch (error) {
      alert("Có lỗi xảy ra: " + getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      {/* Header Section */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-end",
          marginBottom: "32px",
        }}
      >
        <div>
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
        <button
          onClick={handleAdd}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
            border: "none",
            color: "#fff",
            padding: "12px 20px",
            borderRadius: "12px",
            fontWeight: 600,
            cursor: "pointer",
            boxShadow: "0 4px 15px rgba(79, 172, 254, 0.3)",
            transition: "all 0.2s",
          }}
          onMouseOver={(e) =>
            (e.currentTarget.style.transform = "translateY(-2px)")
          }
          onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
        >
          <Plus size={18} />
          {t("endpoints.addBtn", "Thêm Endpoint")}
        </button>
      </div>

      {/* Toolbar */}
      <div
        className="card"
        style={{
          marginBottom: "24px",
          padding: "16px 24px",
          display: "flex",
          gap: "16px",
        }}
      >
        <div style={{ position: "relative", flex: 1, maxWidth: "400px" }}>
          <Search
            size={18}
            style={{
              position: "absolute",
              left: "14px",
              top: "50%",
              transform: "translateY(-50%)",
              color: "var(--text-muted)",
            }}
          />
          <input
            type="text"
            placeholder={t("endpoints.search", "Tìm kiếm theo tên hoặc URL...")}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: "100%",
              padding: "10px 16px 10px 42px",
              background: "var(--bg-secondary)",
              border: "1px solid var(--card-border)",
              borderRadius: "10px",
              color: "var(--text-primary)",
              outline: "none",
            }}
          />
        </div>
      </div>

      {/* Table */}
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
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                {t("endpoints.colName", "Tên Endpoint")}
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                {t("endpoints.colStatus", "Trạng thái")}
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                {t("endpoints.colEnv", "Môi trường")}
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                  textAlign: "right",
                }}
              >
                {t("endpoints.colAction", "Thao tác")}
              </th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td
                  colSpan={4}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  <Activity
                    size={24}
                    className="spin"
                    style={{ margin: "0 auto 12px" }}
                  />
                  {t("common.loading")}
                </td>
              </tr>
            ) : filteredEndpoints.length === 0 ? (
              <tr>
                <td
                  colSpan={4}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  {t("endpoints.empty", "Không tìm thấy endpoint nào.")}
                </td>
              </tr>
            ) : (
              filteredEndpoints.map((ep) => (
                <tr
                  key={ep.id}
                  style={{
                    borderBottom: "1px solid var(--card-border)",
                    transition: "background 0.2s",
                  }}
                >
                  <td style={{ padding: "16px 24px" }}>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
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
                          {ep.name}
                        </div>
                        <div
                          style={{
                            fontSize: "0.8rem",
                            color: "var(--text-muted)",
                          }}
                        >
                          {ep.method} • {ep.url}
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
                        color: getStatusColor(ep.status),
                        background: getStatusBg(ep.status),
                      }}
                    >
                      <span
                        style={{
                          width: "6px",
                          height: "6px",
                          borderRadius: "50%",
                          background: getStatusColor(ep.status),
                        }}
                      />
                      {ep.status}
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
                      {ep.environment}
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
                        style={{
                          background: "none",
                          border: "none",
                          color: "var(--text-muted)",
                          cursor: "pointer",
                          padding: "6px",
                        }}
                        title="Tạm dừng / Chạy"
                      >
                        {ep.isActive ? (
                          <PauseCircle size={18} />
                        ) : (
                          <PlayCircle size={18} />
                        )}
                      </button>
                      <button
                        onClick={() => handleEdit(ep)}
                        style={{
                          background: "none",
                          border: "none",
                          color: "var(--accent-color)",
                          cursor: "pointer",
                          padding: "6px",
                        }}
                        title="Chỉnh sửa"
                      >
                        <Edit2 size={18} />
                      </button>
                      <button
                        onClick={() => {
                          if (
                            window.confirm("Bạn có chắc muốn xoá Endpoint này?")
                          ) {
                            deleteEndpoint(ep.id);
                          }
                        }}
                        style={{
                          background: "none",
                          border: "none",
                          color: "var(--error-color)",
                          cursor: "pointer",
                          padding: "6px",
                        }}
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

      {isFormOpen && (
        <EndpointForm
          initialData={editingEndpoint}
          loading={submitting}
          onSubmit={handleFormSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};
