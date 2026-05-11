package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.request.AlertRuleCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AlertRuleUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AlertRuleDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.AlertRuleDtoMapper;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageAlertRuleServiceTest {

  @Mock private AlertRuleRepository repository;
  @Mock private AlertRuleDtoMapper mapper;

  @InjectMocks private ManageAlertRuleService service;

  @Test
  void createAlertRule_shouldSaveAndReturnDto() {
    Long workspaceId = 1L;
    AlertRuleCreateCommand command =
        new AlertRuleCreateCommand(
            "Latency High",
            AlertRuleType.RESPONSE_TIME_EXCEEDED,
            ComparisonOperator.GT,
            500.0,
            List.of(1L),
            false);
    AlertRule rule = AlertRule.builder().name("Latency High").workspaceId(workspaceId).build();
    AlertRuleDto dto =
        new AlertRuleDto(
            1L,
            "Latency High",
            AlertRuleType.RESPONSE_TIME_EXCEEDED,
            ComparisonOperator.GT,
            500.0,
            workspaceId,
            true,
            List.of(1L),
            false);

    when(mapper.toDomain(command)).thenReturn(rule);
    when(repository.save(any())).thenReturn(rule);
    when(mapper.toDto(rule)).thenReturn(dto);

    AlertRuleDto result = service.createAlertRule(workspaceId, command);

    assertNotNull(result);
    assertEquals(workspaceId, result.workspaceId());
  }

  @Test
  void getAlertRulesByWorkspace_shouldReturnPagedResponse() {
    Long workspaceId = 1L;
    int page = 0;
    int size = 10;
    AlertRule rule = AlertRule.builder().id(1L).workspaceId(workspaceId).build();
    AlertRuleDto dto =
        new AlertRuleDto(
            1L,
            "R",
            AlertRuleType.RESPONSE_TIME_EXCEEDED,
            ComparisonOperator.GT,
            500.0,
            workspaceId,
            true,
            List.of(1L),
            false);

    when(repository.findByWorkspaceId(workspaceId, page, size)).thenReturn(List.of(rule));
    when(repository.countByWorkspaceId(workspaceId)).thenReturn(1L);
    when(mapper.toDto(rule)).thenReturn(dto);

    PagedResponseDto<AlertRuleDto> result =
        service.getAlertRulesByWorkspace(workspaceId, page, size);

    assertEquals(1, result.items().size());
    assertEquals(1, result.totalItems());
  }

  @Test
  void updateAlertRule_shouldSaveAndReturnDto() {
    Long id = 1L;
    Long workspaceId = 1L;
    AlertRuleUpdateCommand command =
        new AlertRuleUpdateCommand(
            id,
            "Updated",
            AlertRuleType.RESPONSE_TIME_EXCEEDED,
            ComparisonOperator.GT,
            1000.0,
            true,
            List.of(2L),
            false);
    AlertRule rule = AlertRule.builder().id(id).workspaceId(workspaceId).build();
    AlertRuleDto dto =
        new AlertRuleDto(
            id,
            "Updated",
            AlertRuleType.RESPONSE_TIME_EXCEEDED,
            ComparisonOperator.GT,
            1000.0,
            workspaceId,
            true,
            List.of(2L),
            false);

    when(repository.findById(id)).thenReturn(Optional.of(rule));
    when(repository.save(any())).thenReturn(rule);
    when(mapper.toDto(rule)).thenReturn(dto);

    AlertRuleDto result = service.updateAlertRule(workspaceId, command);
    assertNotNull(result);
    assertEquals("Updated", result.name());
  }

  @Test
  void deleteAlertRule_shouldCallRepository() {
    Long workspaceId = 1L;
    when(repository.findById(1L))
        .thenReturn(Optional.of(AlertRule.builder().workspaceId(workspaceId).build()));
    service.deleteAlertRule(workspaceId, 1L);
    verify(repository).deleteById(1L);
  }

  @Test
  void updateAlertRule_shouldThrowException_whenNotFound() {
    Long workspaceId = 1L;
    AlertRuleUpdateCommand command =
        new AlertRuleUpdateCommand(
            1L,
            "U",
            AlertRuleType.RESPONSE_TIME_EXCEEDED,
            ComparisonOperator.GT,
            100.0,
            true,
            List.of(),
            false);
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class, () -> service.updateAlertRule(workspaceId, command));
  }

  @Test
  void deleteAlertRule_shouldThrowException_whenNotFound() {
    Long workspaceId = 1L;
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.deleteAlertRule(workspaceId, 1L));
  }
}
