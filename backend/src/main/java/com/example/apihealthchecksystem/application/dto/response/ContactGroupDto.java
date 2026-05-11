package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record ContactGroupDto(
    Long id,
    String name,
    String description,
    Long workspaceId,
    Boolean isActive,
    List<Long> userIds,
    List<String> emailAddresses,
    List<String> webhookUrls) {}
