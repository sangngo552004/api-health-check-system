package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberId implements Serializable {

  @Column(name = "workspace_id")
  private Long workspaceId;

  @Column(name = "user_id")
  private Long userId;
}
