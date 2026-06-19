package com.example.apihealthchecksystem.application.dto.response;

public record DashboardStatsSummaryDto(
    Long workspaceId,
    long totalEndpoints,
    long upEndpoints,
    long downEndpoints,
    long degradedEndpoints) {}
