package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.NotificationJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
  List<NotificationJpaEntity> findByIncidentIdOrderByIdDesc(Long incidentId);
}
