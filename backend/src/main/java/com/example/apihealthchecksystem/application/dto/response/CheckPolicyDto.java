package com.example.apihealthchecksystem.application.dto.response;

public record CheckPolicyDto(
    Long id,
    String name,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer failureThreshold,
    Integer latencyThresholdMillis,
    Long workspaceId,
    Integer expectedStatusCode,
    String expectedResponseBody,
    String responseRegex) {}
