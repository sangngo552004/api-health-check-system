package com.example.apihealthchecksystem.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record IncidentDto(
    Long id,
    Long endpointId,
    String endpointName,
    Long workspaceId,
    LocalDateTime startedAt,
    LocalDateTime resolvedAt,
    String status,
    String reason,
    Integer failureCount,
    String severity,
    String rootCause,
    List<Long> failingResultIds) {}
