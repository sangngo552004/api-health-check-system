package com.example.apihealthchecksystem.application.dto.response;

public record AdminUserDto(
    Long id, String username, String email, String phoneNumber, String role, Boolean isActive) {}
