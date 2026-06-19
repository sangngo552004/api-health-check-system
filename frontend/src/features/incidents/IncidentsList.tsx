import React, { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useWorkspace } from "../../context/useWorkspace";
import { api } from "../../services/api";
import { useIncidentStore } from "../../store/useIncidentStore";
import { PagedResponseDto } from "../../types/common.types";
import { EndpointDto } from "../../types/endpoint.types";
import { IncidentDto, IncidentStatus } from "../../types/incident.types";
import { IncidentDetailPanel } from "./IncidentDetailPanel";
import { IncidentFiltersCard } from "./IncidentFiltersCard";
import { IncidentListPanel } from "./IncidentListPanel";
import { IncidentSummaryCards } from "./IncidentSummaryCards";

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
    replaceIncident,
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
        const page = await api.get<PagedResponseDto<EndpointDto>>("/endpoints", {
          params: { page: 0, size: 100 },
        });
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
        Vui lòng chọn workspace trước khi xem sự cố.
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
          <p className="eyebrow">Theo dõi vận hành</p>
          <h1
            style={{ fontSize: "2rem", fontWeight: 700, margin: "0 0 8px 0" }}
          >
            Quản lý sự cố
          </h1>
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

      <IncidentSummaryCards
        open={summary.open}
        resolved={summary.resolved}
        critical={summary.critical}
        totalItems={totalItems}
      />

      <IncidentFiltersCard
        endpointOptions={endpointOptions}
        selectedEndpointId={selectedEndpointId}
        statusFilter={statusFilter}
        onStatusChange={(value) => {
          const next = new URLSearchParams(searchParams);
          if (value) {
            next.set("status", value);
          } else {
            next.delete("status");
          }
          setSearchParams(next);
        }}
        onEndpointChange={(value) => {
          const next = new URLSearchParams(searchParams);
          if (value) {
            next.set("endpointId", value);
          } else {
            next.delete("endpointId");
          }
          setSearchParams(next);
        }}
      />

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
          gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))",
          alignItems: "start",
          gap: "24px",
        }}
      >
        <IncidentListPanel
          incidents={incidents}
          totalItems={totalItems}
          loading={loading}
          selectedIncidentId={selectedIncident?.id}
          onSelect={(incident) => {
            const next = new URLSearchParams(searchParams);
            next.set("incidentId", String(incident.id));
            setSearchParams(next);
          }}
        />

        <IncidentDetailPanel
          incident={selectedIncident}
          loading={detailLoading}
          onIncidentUpdated={(incident: IncidentDto) => replaceIncident(incident)}
        />
      </div>
    </div>
  );
};
