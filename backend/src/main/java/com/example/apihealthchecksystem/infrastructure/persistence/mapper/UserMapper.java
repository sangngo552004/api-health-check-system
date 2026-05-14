package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toDomain(UserJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return User.builder()
        .id(entity.getId())
        .username(entity.getUsername())
        .email(entity.getEmail())
        .phoneNumber(entity.getPhoneNumber())
        .passwordHash(entity.getPasswordHash())
        .role(entity.getRole())
        .isActive(entity.getIsActive())
        .requiresPasswordChange(entity.getRequiresPasswordChange())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public UserJpaEntity toEntity(User domain) {
    if (domain == null) {
      return null;
    }
    UserJpaEntity entity = new UserJpaEntity();
    entity.setId(domain.getId());
    entity.setUsername(domain.getUsername());
    entity.setEmail(domain.getEmail());
    entity.setPhoneNumber(domain.getPhoneNumber());
    entity.setPasswordHash(domain.getPasswordHash());
    entity.setRole(domain.getRole());
    entity.setIsActive(domain.getIsActive());
    entity.setRequiresPasswordChange(domain.getRequiresPasswordChange());
    return entity;
  }
}
