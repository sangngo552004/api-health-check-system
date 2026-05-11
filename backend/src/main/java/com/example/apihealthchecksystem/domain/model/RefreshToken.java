package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshToken {
  private Long id;
  private Long userId;
  private String token;
  private LocalDateTime expiryDate;
  private LocalDateTime createdAt;
}
