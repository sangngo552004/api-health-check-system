package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Workspace {
  private Long id;
  private String name;
  private String description;
  private String slug;
  private Long ownerId;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
