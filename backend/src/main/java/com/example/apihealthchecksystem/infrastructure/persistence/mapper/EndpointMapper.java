package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.MonitoredEndpointJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EndpointMapper {

  @Mapping(target = "alertRuleIds", ignore = true) // Sẽ xử lý riêng ở Repository nếu cần
  MonitoredEndpoint toDomain(MonitoredEndpointJpaEntity entity);

  MonitoredEndpointJpaEntity toEntity(MonitoredEndpoint domain);
}
