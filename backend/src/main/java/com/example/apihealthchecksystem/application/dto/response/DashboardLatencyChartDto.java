package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record DashboardLatencyChartDto(
    Long workspaceId, List<DashboardLatencySeriesDto> series) {}
