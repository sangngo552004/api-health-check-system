package com.example.apihealthchecksystem.application.mapper;

import com.example.apihealthchecksystem.application.dto.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.EndpointDto;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EndpointDtoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isActive", constant = "true")
  @Mapping(target = "createdBy", ignore = true) // Sẽ gán từ Security Context
  MonitoredEndpoint toDomain(EndpointCreateCommand command);

  @Mapping(target = "id", source = "endpoint.id")
  @Mapping(target = "name", source = "endpoint.name")
  @Mapping(target = "url", source = "endpoint.url")
  @Mapping(target = "method", source = "endpoint.method")
  @Mapping(target = "environment", source = "endpoint.environment")
  @Mapping(target = "checkType", source = "endpoint.checkType")
  @Mapping(target = "isActive", source = "endpoint.isActive")
  @Mapping(target = "createdAt", source = "endpoint.createdAt")
  @Mapping(target = "updatedAt", source = "endpoint.updatedAt")
  @Mapping(target = "headers", source = "endpoint.headers")
  @Mapping(target = "requestBody", source = "endpoint.requestBody")
  @Mapping(target = "expectedStatusCode", source = "policy.expectedStatusCode")
  @Mapping(target = "intervalSeconds", source = "policy.intervalSeconds")
  @Mapping(target = "timeoutMillis", source = "policy.timeoutMillis")
  @Mapping(target = "retryCount", source = "policy.retryCount")
  @Mapping(target = "failureThreshold", source = "policy.failureThreshold")
  @Mapping(target = "latencyThresholdMillis", source = "policy.latencyThresholdMillis")
  EndpointDto toDto(MonitoredEndpoint endpoint, CheckPolicy policy);
}
