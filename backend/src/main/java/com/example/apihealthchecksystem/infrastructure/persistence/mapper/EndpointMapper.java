package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.MonitoredEndpointJpaEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = {CheckPolicyMapper.class})
public interface EndpointMapper {
  MonitoredEndpoint toDomain(MonitoredEndpointJpaEntity entity);

  MonitoredEndpointJpaEntity toJpaEntity(MonitoredEndpoint domain);

  // MapStruct automatically maps nested properties if there are available mappers,
  // but for bidirectional associations in JPA, we need to set the parent reference on the child.
  @AfterMapping
  default void linkPolicy(@MappingTarget MonitoredEndpointJpaEntity entity) {
    if (entity.getPolicy() != null) {
      entity.getPolicy().setEndpoint(entity);
    }
  }
}
