package com.example.apihealthchecksystem.application.dto;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.Map;

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
    Map<String, String> headers,
    String requestBody,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer failureThreshold,
    Integer latencyThresholdMillis) {}
