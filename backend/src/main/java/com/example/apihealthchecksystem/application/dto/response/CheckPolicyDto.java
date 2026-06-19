package com.example.apihealthchecksystem.application.dto.response;

public record CheckPolicyDto(
    Long id,
    String name,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer degradedResponseTimeMillis,
    Long workspaceId,
    Integer expectedStatusCode,
    String expectedResponseBody,
    String responseRegex) {}
