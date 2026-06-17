import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useEndpointStore } from "../../store/useEndpointStore";
import {
  EndpointCreateCommand,
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
  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEndpoint, setEditingEndpoint] =
    useState<EndpointUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchEndpoints(0, 100);
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
    (endpoint) =>
      endpoint.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      endpoint.url.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAdd = () => {
    setEditingEndpoint(null);
    setIsFormOpen(true);
  };

  const handleEdit = (endpoint: EndpointDto) => {
    setEditingEndpoint({
      id: endpoint.id,
      name: endpoint.name,
      url: endpoint.url,
      method: endpoint.method,
      environment: endpoint.environment,
      checkType: endpoint.checkType,
      isActive: endpoint.isActive,
      policyId: endpoint.policyId,
      alertRuleIds: endpoint.alertRuleIds,
      tags: endpoint.tags,
      headers: endpoint.headers,
    });
    setIsFormOpen(true);
  };

  const handleDelete = async (endpointId: number) => {
    if (!window.confirm("Ban co chac muon xoa Endpoint nay?")) {
      return;
    }

    await deleteEndpoint(endpointId);
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
      alert("Co loi xay ra: " + getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <EndpointsToolbar
        title={t("endpoints.title", "Monitored Endpoints")}
        subtitle={t("endpoints.subtitle", "Quan ly muc giam sat")}
        addLabel={t("endpoints.addBtn", "Them Endpoint")}
        searchPlaceholder={t(
          "endpoints.search",
          "Tim kiem theo ten hoac URL...",
        )}
        searchTerm={searchTerm}
        onSearchTermChange={setSearchTerm}
        onCreate={handleAdd}
      />

      <EndpointsTable
        loading={loading}
        endpoints={filteredEndpoints}
        getStatusColor={getStatusColor}
        getStatusBg={getStatusBg}
        onEdit={handleEdit}
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
