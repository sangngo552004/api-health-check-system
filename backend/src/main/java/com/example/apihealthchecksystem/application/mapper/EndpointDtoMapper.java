package com.example.apihealthchecksystem.application.mapper;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EndpointDtoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isActive", constant = "true")
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "contactGroupIds", ignore = true)
  MonitoredEndpoint toDomain(EndpointCreateCommand command);

  @Mapping(target = "id", source = "endpoint.id")
  @Mapping(target = "name", source = "endpoint.name")
  @Mapping(target = "url", source = "endpoint.url")
  @Mapping(target = "method", source = "endpoint.method")
  @Mapping(target = "environment", source = "endpoint.environment")
  @Mapping(target = "checkType", source = "endpoint.checkType")
  @Mapping(target = "isActive", source = "endpoint.isActive")
  @Mapping(target = "status", source = "endpoint.status")
  @Mapping(target = "createdAt", source = "endpoint.createdAt")
  @Mapping(target = "updatedAt", source = "endpoint.updatedAt")
  @Mapping(target = "lastCheckedAt", source = "endpoint.lastCheckedAt")
  @Mapping(target = "nextRunAt", source = "endpoint.nextRunAt")
  @Mapping(target = "policyId", source = "endpoint.policyId")
  @Mapping(target = "alertRuleIds", source = "endpoint.alertRuleIds")
  @Mapping(target = "tags", source = "endpoint.tags")
  @Mapping(target = "headers", source = "endpoint.headers")
  @Mapping(target = "requestBody", source = "endpoint.requestBody")
  @Mapping(target = "expectedStatusCode", source = "policy.expectedStatusCode")
  @Mapping(target = "intervalSeconds", source = "policy.intervalSeconds")
  @Mapping(target = "timeoutMillis", source = "policy.timeoutMillis")
  @Mapping(target = "retryCount", source = "policy.retryCount")
  @Mapping(target = "degradedResponseTimeMillis", source = "policy.degradedResponseTimeMillis")
  @Mapping(target = "workspaceId", source = "endpoint.workspaceId")
  EndpointDto toDto(MonitoredEndpoint endpoint, CheckPolicy policy);
}
