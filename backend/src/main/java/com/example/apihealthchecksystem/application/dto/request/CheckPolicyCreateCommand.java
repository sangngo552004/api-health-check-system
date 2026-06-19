package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CheckPolicyCreateCommand(
    @NotBlank String name,
    @Min(5) Integer intervalSeconds,
    @Min(100) Integer timeoutMillis,
    @Min(0) Integer retryCount,
    Integer degradedResponseTimeMillis,
    Integer expectedStatusCode,
    String expectedResponseBody,
    String responseRegex) {}
