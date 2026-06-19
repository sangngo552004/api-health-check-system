package com.example.apihealthchecksystem.application.dto.response;

import java.time.LocalDateTime;

public record IncidentHealthCheckResultDto(
    Long id,
    LocalDateTime checkedAt,
    String status,
    Integer httpStatusCode,
    Long responseTimeMillis,
    String errorMessage,
    String responsePayload,
    Boolean success,
    String nodeId) {}
