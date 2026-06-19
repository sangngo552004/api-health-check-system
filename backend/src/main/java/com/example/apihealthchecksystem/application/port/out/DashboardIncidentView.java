package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import java.time.LocalDateTime;

public record DashboardIncidentView(
    Long id,
    Long endpointId,
    String endpointName,
    LocalDateTime startedAt,
    String reason,
    IncidentSeverity severity) {}
