package com.example.apihealthchecksystem.application.dto;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;

public record EndpointDto(
    Long id,
    String name,
    String url,
    HttpMethod method,
    String environment,
    CheckType checkType,
    Integer expectedStatusCode,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer failureThreshold,
    Integer latencyThresholdMillis) {}
