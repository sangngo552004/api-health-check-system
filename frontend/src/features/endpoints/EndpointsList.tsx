import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useToast } from "../../context/useToast";
import { useEndpointStore } from "../../store/useEndpointStore";
import { endpointsApi } from "../../services/api/endpoints.api";
import {
  EndpointDto,
  EndpointStatus,
  EndpointUpdateCommand,
} from "../../types/endpoint.types";
import { getErrorMessage } from "../../utils/error";
import { EndpointForm, EndpointFormData } from "./EndpointForm";
import { EndpointsTable } from "./EndpointsTable";
import { EndpointsToolbar } from "./EndpointsToolbar";

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
  const { showToast } = useToast();
  const [searchTerm, setSearchTerm] = useState("");
  const [environmentFilter, setEnvironmentFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [methodFilter, setMethodFilter] = useState("");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEndpoint, setEditingEndpoint] =
    useState<EndpointUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchEndpoints({
      page: 0,
      size: 100,
      search: searchTerm.trim() || undefined,
      environment: environmentFilter || undefined,
      status: statusFilter || undefined,
      method: methodFilter || undefined,
      sortBy: "name",
      sortDir,
    });
  }, [
    environmentFilter,
    fetchEndpoints,
    methodFilter,
    searchTerm,
    sortDir,
    statusFilter,
  ]);

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

  const handleAdd = () => {
    setEditingEndpoint(null);
    setIsFormOpen(true);
  };

  const handleEdit = async (endpoint: EndpointDto) => {
    try {
      const fullEndpoint = await endpointsApi.getEndpointById(endpoint.id);
      setEditingEndpoint({
        id: fullEndpoint.id,
        name: fullEndpoint.name,
        url: fullEndpoint.url,
        method: fullEndpoint.method,
        environment: fullEndpoint.environment as
          | "PRODUCTION"
          | "STAGING"
          | "DEVELOPMENT",
        checkType: fullEndpoint.checkType,
        isActive: fullEndpoint.isActive,
        policyId: fullEndpoint.policyId,
        alertRuleIds: fullEndpoint.alertRuleIds,
        tags: fullEndpoint.tags,
        headers: fullEndpoint.headers,
        requestBody: fullEndpoint.requestBody,
      });
      setIsFormOpen(true);
    } catch (error) {
      showToast({
        title: "Không tải được endpoint",
        description: getErrorMessage(error),
        variant: "error",
      });
    }
  };

  const handleDelete = async (endpointId: number) => {
    if (!window.confirm("Ban co chac muon xoa Endpoint nay?")) {
      return;
    }

    try {
      await deleteEndpoint(endpointId);
      showToast({
        title: "Xóa endpoint thành công",
        description: "Endpoint đã được xóa khỏi danh sách giám sát.",
        variant: "success",
      });
    } catch (error) {
      showToast({
        title: "Xóa endpoint thất bại",
        description: getErrorMessage(error),
        variant: "error",
      });
    }
  };

  const handleFormSubmit = async (data: EndpointFormData) => {
    setSubmitting(true);
    try {
      if (editingEndpoint) {
        await updateEndpoint(editingEndpoint.id, {
          id: editingEndpoint.id,
          name: data.name,
          url: data.url,
          method: data.method,
          environment: data.environment,
          checkType: data.checkType,
          isActive: data.isActive,
          policyId: data.policyId,
          alertRuleIds: data.alertRuleIds,
          tags: data.tags,
          headers: data.headers,
          requestBody: data.requestBody,
        });
        showToast({
          title: "Cập nhật endpoint thành công",
          description: `Endpoint ${data.name} đã được cập nhật.`,
          variant: "success",
        });
      } else {
        await createEndpoint({
          name: data.name,
          url: data.url,
          method: data.method,
          environment: data.environment,
          checkType: data.checkType,
          policyId: data.policyId,
          alertRuleIds: data.alertRuleIds,
          tags: data.tags,
          headers: data.headers,
          requestBody: data.requestBody,
        });
        showToast({
          title: "Tạo endpoint thành công",
          description: `Endpoint ${data.name} đã được thêm vào giám sát.`,
          variant: "success",
        });
      }
      setIsFormOpen(false);
    } catch (error) {
      showToast({
        title: editingEndpoint
          ? "Cập nhật endpoint thất bại"
          : "Tạo endpoint thất bại",
        description: getErrorMessage(error),
        variant: "error",
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <EndpointsToolbar
        title={t("endpoints.title", "Endpoint giám sát")}
        subtitle={t("endpoints.subtitle", "Quản lý danh sách giám sát")}
        addLabel={t("endpoints.addBtn", "Thêm endpoint")}
        searchPlaceholder={t(
          "endpoints.search",
          "Tìm kiếm theo tên hoặc URL...",
        )}
        searchTerm={searchTerm}
        onSearchTermChange={setSearchTerm}
        onCreate={handleAdd}
        filters={
          <>
            <select
              value={environmentFilter}
              onChange={(event) => setEnvironmentFilter(event.target.value)}
              style={filterStyle}
            >
              <option value="">Tất cả môi trường</option>
              <option value="PRODUCTION">Production</option>
              <option value="STAGING">Staging</option>
              <option value="DEVELOPMENT">Development</option>
            </select>
            <select
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
              style={filterStyle}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="UP">Up</option>
              <option value="DEGRADED">Degraded</option>
              <option value="DOWN">Down</option>
              <option value="UNKNOWN">Unknown</option>
            </select>
            <select
              value={methodFilter}
              onChange={(event) => setMethodFilter(event.target.value)}
              style={filterStyle}
            >
              <option value="">Tất cả method</option>
              <option value="GET">GET</option>
              <option value="POST">POST</option>
              <option value="PUT">PUT</option>
              <option value="DELETE">DELETE</option>
              <option value="PATCH">PATCH</option>
              <option value="HEAD">HEAD</option>
            </select>
            <select
              value={sortDir}
              onChange={(event) =>
                setSortDir(event.target.value as "asc" | "desc")
              }
              style={filterStyle}
            >
              <option value="desc">Giảm dần</option>
              <option value="asc">Tăng dần</option>
            </select>
          </>
        }
      />

      <EndpointsTable
        loading={loading}
        endpoints={endpoints}
        getStatusColor={getStatusColor}
        getStatusBg={getStatusBg}
        onEdit={(endpoint) => void handleEdit(endpoint)}
        onDelete={(endpointId) => void handleDelete(endpointId)}
      />

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

const filterStyle: React.CSSProperties = {
  minWidth: "170px",
  padding: "10px 14px",
  background: "var(--bg-secondary)",
  border: "1px solid var(--card-border)",
  borderRadius: "10px",
  color: "var(--text-primary)",
};
