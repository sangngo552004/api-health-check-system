package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.IncidentHealthCheckResultDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import java.util.List;

public interface GetIncidentUseCase {
  PagedResponseDto<IncidentDto> getIncidents(
      Long workspaceId,
      String search,
      String status,
      String severity,
      Long endpointId,
      int page,
      int size,
      String sortBy,
      String sortDir);

  IncidentDto getIncidentById(Long workspaceId, Long incidentId);

  List<IncidentHealthCheckResultDto> getIncidentResults(Long workspaceId, Long incidentId);
}
