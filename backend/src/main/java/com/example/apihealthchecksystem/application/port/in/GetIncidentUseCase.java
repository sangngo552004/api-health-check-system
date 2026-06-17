package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;

public interface GetIncidentUseCase {
  PagedResponseDto<IncidentDto> getIncidents(
      Long workspaceId, String status, Long endpointId, int page, int size);

  IncidentDto getIncidentById(Long workspaceId, Long incidentId);
}
