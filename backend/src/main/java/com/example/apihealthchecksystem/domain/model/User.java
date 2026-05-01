package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
  private Long id;
  private String username;
  private String email;
  private String phoneNumber;
  private String passwordHash;
  private UserRole role;
  private Boolean isActive;
  private LocalDateTime createdAt;
}
