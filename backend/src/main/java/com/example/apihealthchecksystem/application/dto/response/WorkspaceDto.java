package com.example.apihealthchecksystem.application.dto.response;

import java.time.LocalDateTime;

public record WorkspaceDto(
    Long id,
    String name,
    String description,
    String slug,
    Long ownerId,
    Boolean isActive,
    LocalDateTime createdAt) {}
