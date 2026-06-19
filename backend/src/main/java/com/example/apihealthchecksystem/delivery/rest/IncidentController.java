package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.IncidentRootCauseUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.IncidentHealthCheckResultDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.GetIncidentUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageIncidentUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {
  private final GetIncidentUseCase incidentUseCase;
  private final ManageIncidentUseCase manageIncidentUseCase;

  @GetMapping
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<PagedResponseDto<IncidentDto>> getIncidents(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String severity,
      @RequestParam(required = false) Long endpointId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "startedAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    return ApiResponse.success(
        incidentUseCase.getIncidents(
            workspaceId, search, status, severity, endpointId, page, size, sortBy, sortDir));
  }

  @GetMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<IncidentDto> getIncidentById(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(incidentUseCase.getIncidentById(workspaceId, id));
  }

  @GetMapping("/{id}/results")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<List<IncidentHealthCheckResultDto>> getIncidentResults(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(incidentUseCase.getIncidentResults(workspaceId, id));
  }

  @PatchMapping("/{id}/root-cause")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<IncidentDto> updateIncidentRootCause(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @PathVariable Long id,
      @Valid @RequestBody IncidentRootCauseUpdateCommand command) {
    return ApiResponse.success(manageIncidentUseCase.updateRootCause(workspaceId, id, command));
  }
}
