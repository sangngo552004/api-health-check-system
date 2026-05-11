package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import com.example.apihealthchecksystem.infrastructure.config.JpaAuditingConfig;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.AlertRuleMapperImpl;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.AlertRuleJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({AlertRuleMapperImpl.class, JpaAuditingConfig.class})
class AlertRuleRepositoryAdapterIT {

  @Autowired private AlertRuleJpaRepository jpaRepository;
  @Autowired private AlertRuleMapperImpl mapper;

  private AlertRuleRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new AlertRuleRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  void save_shouldPersistRule() {
    AlertRule rule =
        AlertRule.builder()
            .name("High Latency")
            .ruleType(AlertRuleType.RESPONSE_TIME_EXCEEDED)
            .operator(ComparisonOperator.GT)
            .thresholdValue(1000.0)
            .isActive(true)
            .workspaceId(1L)
            .build();

    AlertRule saved = adapter.save(rule);

    assertNotNull(saved.getId());

    Optional<AlertRule> found = adapter.findById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals(AlertRuleType.RESPONSE_TIME_EXCEEDED, found.get().getRuleType());
  }

  @Test
  void findByWorkspaceId_shouldReturnResults() {
    Long wsId = 1L;
    adapter.save(
        AlertRule.builder()
            .name("R1")
            .workspaceId(wsId)
            .ruleType(AlertRuleType.RESPONSE_TIME_EXCEEDED)
            .operator(ComparisonOperator.GT)
            .thresholdValue(100.0)
            .isActive(true)
            .build());

    List<AlertRule> results = adapter.findByWorkspaceId(wsId, 0, 10);
    assertEquals(1, results.size());
  }
}
