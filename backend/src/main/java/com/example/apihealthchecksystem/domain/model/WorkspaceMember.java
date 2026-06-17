package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceMember {
  private Long workspaceId;
  private Long userId;
  private LocalDateTime joinedAt;
}
