package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
    @NotBlank(message = "validation.refresh.token.not_blank") String refreshToken) {}
