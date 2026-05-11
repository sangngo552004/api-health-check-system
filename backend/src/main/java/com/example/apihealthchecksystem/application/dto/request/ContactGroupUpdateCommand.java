package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ContactGroupUpdateCommand(
    @NotNull Long id,
    @NotBlank String name,
    String description,
    Boolean isActive,
    List<Long> userIds,
    List<String> emailAddresses,
    List<String> webhookUrls) {}
