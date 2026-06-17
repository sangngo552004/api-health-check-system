package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.CheckPolicyCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.CheckPolicyUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.CheckPolicyDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.ManageCheckPolicyUseCase;
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
@RequestMapping("/api/v1/check-policies")
@RequiredArgsConstructor
public class CheckPolicyController {

  private final ManageCheckPolicyUseCase useCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<CheckPolicyDto> create(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @Valid @RequestBody CheckPolicyCreateCommand command) {
    return ApiResponse.success(useCase.createPolicy(workspaceId, command));
  }

  @GetMapping
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<PagedResponseDto<CheckPolicyDto>> getByWorkspace(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.success(useCase.getPoliciesByWorkspace(workspaceId, page, size));
  }

  @GetMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<CheckPolicyDto> getById(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(useCase.getPolicy(workspaceId, id));
  }

  @PutMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<CheckPolicyDto> update(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @PathVariable Long id,
      @Valid @RequestBody CheckPolicyUpdateCommand command) {
    CheckPolicyUpdateCommand withId =
        new CheckPolicyUpdateCommand(
            id,
            command.name(),
            command.intervalSeconds(),
            command.timeoutMillis(),
            command.retryCount(),
            command.failureThreshold(),
            command.latencyThresholdMillis(),
            command.expectedStatusCode(),
            command.expectedResponseBody(),
            command.responseRegex());
    return ApiResponse.success(useCase.updatePolicy(workspaceId, withId));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public void delete(@RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    useCase.deletePolicy(workspaceId, id);
  }
}
