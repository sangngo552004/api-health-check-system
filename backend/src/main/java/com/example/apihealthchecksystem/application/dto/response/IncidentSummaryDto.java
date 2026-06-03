package com.example.apihealthchecksystem.application.dto.response;

import java.time.LocalDateTime;

public record IncidentSummaryDto(
    Long id,
    Long endpointId,
    String endpointName,
    LocalDateTime startedAt,
    String reason,
    String severity) {}
