package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;

public record IncidentListView(
    Long id,
    Long endpointId,
    String endpointName,
    Long workspaceId,
    LocalDateTime startedAt,
    LocalDateTime resolvedAt,
    IncidentStatus status,
    String reason,
    Integer failureCount,
    IncidentSeverity severity,
    String rootCause) {}
