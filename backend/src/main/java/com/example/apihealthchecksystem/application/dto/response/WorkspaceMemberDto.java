package com.example.apihealthchecksystem.application.dto.response;

import com.example.apihealthchecksystem.domain.valueobject.WorkspaceRole;
import java.time.LocalDateTime;

public record WorkspaceMemberDto(
    Long userId, String username, String email, WorkspaceRole role, LocalDateTime joinedAt) {}
