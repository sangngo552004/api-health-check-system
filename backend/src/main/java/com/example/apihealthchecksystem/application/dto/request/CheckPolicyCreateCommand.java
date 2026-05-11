package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckPolicyCreateCommand(
    @NotBlank String name,
    @NotNull @Min(5) Integer intervalSeconds,
    @NotNull @Min(100) Integer timeoutMillis,
    @NotNull @Min(0) Integer retryCount,
    @NotNull @Min(1) Integer failureThreshold,
    Integer latencyThresholdMillis,
    Integer expectedStatusCode,
    String expectedResponseBody,
    String responseRegex) {}
