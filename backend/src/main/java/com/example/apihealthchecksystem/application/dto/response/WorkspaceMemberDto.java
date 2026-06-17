package com.example.apihealthchecksystem.application.dto.response;

import java.time.LocalDateTime;

public record WorkspaceMemberDto(
    Long userId, String username, String email, LocalDateTime joinedAt) {}
