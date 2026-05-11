package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WorkspaceCreateCommand(
    @NotBlank String name, String description, @NotBlank String slug) {}
