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
  @Mapping(target = "policy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isActive", constant = "true")
  @Mapping(target = "expectedStatusCode", source = "expectedStatusCode", defaultValue = "200")
  MonitoredEndpoint toDomain(EndpointCreateCommand command);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "endpointId", ignore = true)
  @Mapping(target = "intervalSeconds", source = "intervalSeconds", defaultValue = "60")
  @Mapping(target = "timeoutMillis", source = "timeoutMillis", defaultValue = "5000")
  @Mapping(target = "retryCount", source = "retryCount", defaultValue = "3")
  @Mapping(target = "failureThreshold", source = "failureThreshold", defaultValue = "3")
  @Mapping(
      target = "latencyThresholdMillis",
      source = "latencyThresholdMillis",
      defaultValue = "2000")
  CheckPolicy toPolicyDomain(EndpointCreateCommand command);

  @Mapping(target = "id", source = "endpoint.id")
  @Mapping(target = "name", source = "endpoint.name")
  @Mapping(target = "url", source = "endpoint.url")
  @Mapping(target = "method", source = "endpoint.method")
  @Mapping(target = "environment", source = "endpoint.environment")
  @Mapping(target = "checkType", source = "endpoint.checkType")
  @Mapping(target = "expectedStatusCode", source = "endpoint.expectedStatusCode")
  @Mapping(target = "isActive", source = "endpoint.isActive")
  @Mapping(target = "createdAt", source = "endpoint.createdAt")
  @Mapping(target = "updatedAt", source = "endpoint.updatedAt")
  @Mapping(target = "intervalSeconds", source = "policy.intervalSeconds")
  @Mapping(target = "timeoutMillis", source = "policy.timeoutMillis")
  @Mapping(target = "retryCount", source = "policy.retryCount")
  @Mapping(target = "failureThreshold", source = "policy.failureThreshold")
  @Mapping(target = "latencyThresholdMillis", source = "policy.latencyThresholdMillis")
  EndpointDto toDto(MonitoredEndpoint endpoint, CheckPolicy policy);
}
