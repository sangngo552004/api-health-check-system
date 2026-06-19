package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record WorkspaceDashboardStatsDto(
    Long workspaceId,
    long totalEndpoints,
    long upEndpoints,
    long downEndpoints,
    long degradedEndpoints,
    long openIncidentsCount,
    List<IncidentSummaryDto> activeIncidents,
    List<DashboardLatencySeriesDto> latencySeries) {}
