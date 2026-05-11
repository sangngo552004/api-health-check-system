package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.WorkspaceRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceMember {
  private Long workspaceId;
  private Long userId;
  private String username;
  private String email;
  private WorkspaceRole role;
  private LocalDateTime joinedAt;
}
