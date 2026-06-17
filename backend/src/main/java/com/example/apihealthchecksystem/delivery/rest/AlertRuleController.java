package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.AlertRuleCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AlertRuleUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AlertRuleDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.ManageAlertRuleUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alert-rules")
@RequiredArgsConstructor
public class AlertRuleController {

  private final ManageAlertRuleUseCase useCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<AlertRuleDto> createAlertRule(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @Valid @RequestBody AlertRuleCreateCommand command) {
    return ApiResponse.success(useCase.createAlertRule(workspaceId, command));
  }

  @GetMapping
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<PagedResponseDto<AlertRuleDto>> getAlertRules(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.success(useCase.getAlertRulesByWorkspace(workspaceId, page, size));
  }

  @GetMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<AlertRuleDto> getAlertRule(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(useCase.getAlertRule(workspaceId, id));
  }

  @PutMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<AlertRuleDto> updateAlertRule(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @PathVariable Long id,
      @Valid @RequestBody AlertRuleUpdateCommand command) {
    AlertRuleUpdateCommand withId =
        new AlertRuleUpdateCommand(
            id,
            command.name(),
            command.ruleType(),
            command.operator(),
            command.thresholdValue(),
            command.isActive(),
            command.contactGroupIds(),
            command.overrideDefaultContacts());
    return ApiResponse.success(useCase.updateAlertRule(workspaceId, withId));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public void deleteAlertRule(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    useCase.deleteAlertRule(workspaceId, id);
  }
}
