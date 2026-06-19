package com.example.apihealthchecksystem.infrastructure.security;

import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("workspaceSecurity")
@RequiredArgsConstructor
public class WorkspaceSecurityEvaluator {

  private final WorkspaceMemberJpaRepository workspaceMemberRepository;

  public boolean isWorkspaceMember(Long workspaceId, Long userId) {
    if (workspaceId == null || userId == null) {
      return false;
    }
    return workspaceMemberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, userId);
  }
}
