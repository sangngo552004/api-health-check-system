package com.example.apihealthchecksystem.application.dto;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

public record EndpointUpdateCommand(
    @NotNull Long id,
    @NotBlank String name,
    @NotBlank @Pattern(regexp = "^(http|https)://.*$") String url,
    @NotNull HttpMethod method,
    String environment,
    @NotNull CheckType checkType,
    Boolean isActive,
    @NotNull Long policyId,
    List<Long> alertRuleIds,
    List<String> tags,
    Map<String, String> headers,
    String requestBody) {}
