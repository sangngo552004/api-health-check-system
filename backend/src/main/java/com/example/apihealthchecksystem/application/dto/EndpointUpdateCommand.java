package com.example.apihealthchecksystem.application.dto;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;

public record EndpointUpdateCommand(
    Long id,
    String name,
    String url,
    HttpMethod method,
    String environment,
    CheckType checkType,
    Integer expectedStatusCode,
    Boolean isActive,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer failureThreshold,
    Integer latencyThresholdMillis) {}
