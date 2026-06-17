package com.example.apihealthchecksystem.application.dto.request;

import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminUserUpdateCommand(
    @NotBlank @Size(max = 100) String username,
    @Email @Size(max = 255) String email,
    @Size(max = 20) String phoneNumber,
    @Size(min = 6, max = 255) String password,
    @NotNull UserRole role,
    Boolean isActive,
    Boolean requiresPasswordChange) {}
