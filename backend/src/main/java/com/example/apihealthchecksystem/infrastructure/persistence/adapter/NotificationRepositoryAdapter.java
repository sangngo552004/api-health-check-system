package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.NotificationRepository;
import com.example.apihealthchecksystem.domain.model.Notification;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.NotificationMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.NotificationJpaRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {
  private final NotificationJpaRepository jpaRepository;
  private final NotificationMapper mapper;

  @Override
  public Notification save(Notification notification) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
  }

  @Override
  public List<Notification> findByIncidentId(Long incidentId) {
    return jpaRepository.findByIncidentIdOrderByIdDesc(incidentId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }
}
