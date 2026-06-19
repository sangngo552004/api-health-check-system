package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;

public interface ManageEndpointUseCase {
  EndpointDto createEndpoint(Long workspaceId, Long currentUserId, EndpointCreateCommand command);

  EndpointDto updateEndpoint(Long workspaceId, EndpointUpdateCommand command);

  EndpointDto getEndpoint(Long workspaceId, Long id);

  PagedResponseDto<EndpointDto> getEndpointsByWorkspace(
      Long workspaceId,
      String search,
      String environment,
      String status,
      String method,
      String checkType,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir);

  void deleteEndpoint(Long workspaceId, Long id);
}
