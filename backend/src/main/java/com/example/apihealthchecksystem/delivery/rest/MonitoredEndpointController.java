package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.ManageEndpointUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import com.example.apihealthchecksystem.delivery.rest.common.security.CurrentUserId;
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
@RequestMapping("/api/v1/endpoints")
@RequiredArgsConstructor
public class MonitoredEndpointController {

  private final ManageEndpointUseCase endpointUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@workspaceSecurity.isAdmin(#workspaceId, authentication.principal.id)")
  public ApiResponse<EndpointDto> createEndpoint(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @CurrentUserId Long currentUserId,
      @Valid @RequestBody EndpointCreateCommand command) {
    return ApiResponse.success(endpointUseCase.createEndpoint(workspaceId, currentUserId, command));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@workspaceSecurity.isAdmin(#workspaceId, authentication.principal.id)")
  public ApiResponse<EndpointDto> updateEndpoint(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @PathVariable Long id,
      @Valid @RequestBody EndpointUpdateCommand command) {
    // Ensure command matches the path variable
    EndpointUpdateCommand withId =
        new EndpointUpdateCommand(
            id,
            command.name(),
            command.url(),
            command.method(),
            command.environment(),
            command.checkType(),
            command.isActive(),
            command.policyId(),
            command.alertRuleIds(),
            command.tags(),
            command.headers(),
            command.requestBody());
    return ApiResponse.success(endpointUseCase.updateEndpoint(workspaceId, withId));
  }

  @GetMapping("/{id}")
  @PreAuthorize("@workspaceSecurity.isMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<EndpointDto> getEndpoint(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(endpointUseCase.getEndpoint(workspaceId, id));
  }

  @GetMapping
  @PreAuthorize("@workspaceSecurity.isMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<PagedResponseDto<EndpointDto>> getEndpoints(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.success(endpointUseCase.getEndpointsByWorkspace(workspaceId, page, size));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("@workspaceSecurity.isAdmin(#workspaceId, authentication.principal.id)")
  public void deleteEndpoint(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    endpointUseCase.deleteEndpoint(workspaceId, id);
  }
}
