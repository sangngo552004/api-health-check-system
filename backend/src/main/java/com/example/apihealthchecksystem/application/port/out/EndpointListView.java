package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;

public record EndpointListView(
    Long id,
    String name,
    String url,
    HttpMethod method,
    String environment,
    CheckType checkType,
    Long workspaceId,
    Long policyId,
    Boolean isActive,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastCheckedAt,
    LocalDateTime nextRunAt,
    String requestBody) {}
