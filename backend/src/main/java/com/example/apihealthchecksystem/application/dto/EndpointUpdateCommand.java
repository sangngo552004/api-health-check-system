package com.example.apihealthchecksystem.application.dto;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EndpointUpdateCommand(
    @NotNull(message = "validation.endpoint.id.not_null") Long id,
    @NotBlank(message = "validation.endpoint.name.not_blank") String name,
    @NotBlank(message = "validation.endpoint.url.not_blank")
        @Pattern(regexp = "^(http|https)://.*$", message = "validation.endpoint.url.invalid")
        String url,
    @NotNull(message = "validation.endpoint.method.not_null") HttpMethod method,
    String environment,
    @NotNull(message = "validation.endpoint.checkType.not_null") CheckType checkType,
    Integer expectedStatusCode,
    Boolean isActive,
    @NotNull(message = "validation.endpoint.intervalSeconds.not_null")
        @Min(value = 5, message = "validation.endpoint.intervalSeconds.min")
        Integer intervalSeconds,
    @NotNull(message = "validation.endpoint.timeoutMillis.not_null")
        @Min(value = 100, message = "validation.endpoint.timeoutMillis.min")
        Integer timeoutMillis,
    @NotNull(message = "validation.endpoint.retryCount.not_null")
        @Min(value = 0, message = "validation.endpoint.retryCount.min")
        Integer retryCount,
    @NotNull(message = "validation.endpoint.failureThreshold.not_null")
        @Min(value = 1, message = "validation.endpoint.failureThreshold.min")
        Integer failureThreshold,
    Integer latencyThresholdMillis) {}
