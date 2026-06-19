package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactGroup {
  private Long id;
  private String name;
  private String description;
  private List<String> emailAddresses;
  private Boolean isActive;
  private Long createdBy;
  private Long workspaceId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
