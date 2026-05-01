package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity extends BaseJpaEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(unique = true)
  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Column(name = "is_active")
  private Boolean isActive;
}
