package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.AlertRule;
import java.util.List;
import java.util.Optional;

public interface AlertRuleRepository {
  AlertRule save(AlertRule alertRule);

  Optional<AlertRule> findById(Long id);

  List<AlertRule> findAll();

  List<AlertRule> findByWorkspaceId(Long workspaceId, int page, int size);

  long countByWorkspaceId(Long workspaceId);

  void deleteById(Long id);
}
