package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import java.util.List;
import java.util.Optional;

public interface AlertRuleRepository {
  AlertRule save(AlertRule alertRule);

  Optional<AlertRule> findById(Long id);

  List<AlertRule> findAllByIds(List<Long> ids);

  List<AlertRule> findAll();

  PageResult<AlertRule> searchByWorkspace(
      Long workspaceId,
      String search,
      AlertRuleType ruleType,
      ComparisonOperator operator,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir);

  void deleteById(Long id);
}
