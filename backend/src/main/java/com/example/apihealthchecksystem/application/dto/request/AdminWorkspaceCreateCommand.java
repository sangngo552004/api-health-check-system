package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminWorkspaceCreateCommand(
    @NotBlank @Size(max = 255) String name,
    String description,
    @NotBlank @Size(max = 255) String slug,
    Boolean isActive) {}
