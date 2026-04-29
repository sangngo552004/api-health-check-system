package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.EndpointDto;
import com.example.apihealthchecksystem.application.dto.EndpointUpdateCommand;
import java.util.List;

public interface ManageEndpointUseCase {
  EndpointDto createEndpoint(EndpointCreateCommand command);

  EndpointDto updateEndpoint(EndpointUpdateCommand command);

  EndpointDto getEndpoint(Long id);

  List<EndpointDto> getAllEndpoints();

  void deleteEndpoint(Long id);
}
