package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactGroup {
  private Long id;
  private String name;
  private String description;
  private List<Long> userIds;
  private List<String> emailAddresses;
  private List<String> webhookUrls;
  private Boolean isActive;
  private Long createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
