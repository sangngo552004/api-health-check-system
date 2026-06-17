package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.GetIncidentUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {
  private final GetIncidentUseCase incidentUseCase;

  @GetMapping
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<PagedResponseDto<IncidentDto>> getIncidents(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Long endpointId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.success(
        incidentUseCase.getIncidents(workspaceId, status, endpointId, page, size));
  }

  @GetMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<IncidentDto> getIncidentById(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(incidentUseCase.getIncidentById(workspaceId, id));
  }
}
