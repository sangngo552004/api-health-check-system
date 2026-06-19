package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.MonitoredEndpointJpaEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true))
public interface EndpointMapper {

  @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
  MonitoredEndpoint toDomain(MonitoredEndpointJpaEntity entity);

  @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
  MonitoredEndpointJpaEntity toEntity(MonitoredEndpoint domain);

  @Named("stringToStatus")
  default EndpointStatus stringToStatus(String status) {
    if (status == null) {
      return EndpointStatus.UP;
    }
    try {
      return EndpointStatus.valueOf(status);
    } catch (IllegalArgumentException e) {
      return EndpointStatus.UP;
    }
  }

  @Named("statusToString")
  default String statusToString(EndpointStatus status) {
    return status == null ? null : status.name();
  }
}
