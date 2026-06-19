package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.IncidentRootCauseUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.IncidentDto;

public interface ManageIncidentUseCase {
  IncidentDto updateRootCause(
      Long workspaceId, Long incidentId, IncidentRootCauseUpdateCommand command);
}
