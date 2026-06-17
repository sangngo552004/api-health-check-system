package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.Notification;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.NotificationJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface NotificationMapper {
  Notification toDomain(NotificationJpaEntity entity);

  NotificationJpaEntity toEntity(Notification domain);
}
