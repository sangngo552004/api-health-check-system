package com.example.apihealthchecksystem.infrastructure.security;

import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workspaceSecurity")
@RequiredArgsConstructor
public class WorkspaceSecurityEvaluator {

  private final WorkspaceMemberJpaRepository workspaceMemberRepository;

  public boolean isMember(Long workspaceId, Long userId) {
    if (workspaceId == null || userId == null) {
      return false;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return true; // System Admin bypass
    }

    return workspaceMemberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, userId);
  }

  public boolean isAdmin(Long workspaceId, Long userId) {
    if (workspaceId == null || userId == null) {
      return false;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return true; // System Admin bypass
    }

    // Lấy thành viên và kiểm tra xem role có phải ADMIN không
    return workspaceMemberRepository
        .findById(
            new com.example.apihealthchecksystem.infrastructure.persistence.entity
                .WorkspaceMemberId(workspaceId, userId))
        .map(member -> "ADMIN".equals(member.getRole()))
        .orElse(false);
  }
}
