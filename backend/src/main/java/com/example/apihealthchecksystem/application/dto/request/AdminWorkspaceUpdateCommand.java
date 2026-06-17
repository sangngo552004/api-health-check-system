package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminWorkspaceUpdateCommand(
    @NotBlank @Size(max = 255) String name,
    String description,
    @NotBlank @Size(max = 255) String slug,
    @NotNull Long ownerId,
    Boolean isActive) {}
