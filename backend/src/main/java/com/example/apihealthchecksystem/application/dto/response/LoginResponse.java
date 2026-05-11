package com.example.apihealthchecksystem.application.dto.response;

public record LoginResponse(
    String accessToken, String refreshToken, String role, boolean requiresPasswordChange) {}
