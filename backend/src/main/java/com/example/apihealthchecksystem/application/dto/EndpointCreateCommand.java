package com.example.apihealthchecksystem.application.dto;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;

public record EndpointCreateCommand(
    String name,
    String url,
    HttpMethod method,
    String environment,
    CheckType checkType,
    Integer expectedStatusCode,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer failureThreshold,
    Integer latencyThresholdMillis) {}
