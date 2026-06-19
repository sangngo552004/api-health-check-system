package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckPolicyUpdateCommand(
    @NotNull Long id,
    @NotBlank String name,
    @Min(5) Integer intervalSeconds,
    @Min(100) Integer timeoutMillis,
    @Min(0) Integer retryCount,
    Integer degradedResponseTimeMillis,
    Integer expectedStatusCode,
    String expectedResponseBody,
    String responseRegex) {}
