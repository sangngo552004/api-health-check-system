package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record DashboardActiveIncidentsDto(
    Long workspaceId, long openIncidentsCount, List<IncidentSummaryDto> incidents) {}
