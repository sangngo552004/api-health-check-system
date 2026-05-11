package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkspaceUpdateCommand(
    @NotNull Long id, @NotBlank String name, String description, Boolean isActive) {}
