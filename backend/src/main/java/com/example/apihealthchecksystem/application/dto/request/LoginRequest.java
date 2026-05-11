package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "validation.login.username.not_blank") String username,
    @NotBlank(message = "validation.login.password.not_blank") String password) {}
