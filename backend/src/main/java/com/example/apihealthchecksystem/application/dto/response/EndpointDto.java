package com.example.apihealthchecksystem.application.dto.response;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record EndpointDto(
    Long id,
    String name,
    String url,
    HttpMethod method,
    String environment,
    CheckType checkType,
    Long workspaceId,
    Long policyId,
    Integer expectedStatusCode,
    Boolean isActive,
    EndpointStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastCheckedAt,
    LocalDateTime nextRunAt,
    List<Long> alertRuleIds,
    List<String> tags,
    Map<String, String> headers,
    String requestBody,
    Integer intervalSeconds,
    Integer timeoutMillis,
    Integer retryCount,
    Integer degradedResponseTimeMillis) {}
