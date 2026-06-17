package com.example.apihealthchecksystem.infrastructure.security;

import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workspaceSecurity")
@RequiredArgsConstructor
public class WorkspaceSecurityEvaluator {

  private final WorkspaceMemberJpaRepository workspaceMemberRepository;

  public boolean canAccessWorkspaceArea(Long workspaceId, Long userId) {
    if (workspaceId == null || userId == null) {
      return false;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || authentication.getAuthorities().stream()
            .noneMatch(authority -> "ROLE_USER".equals(authority.getAuthority()))) {
      return false;
    }

    return workspaceMemberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, userId);
  }
}
