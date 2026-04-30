package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.CheckPolicyJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CheckPolicyMapper {
  @Mapping(source = "endpoint.id", target = "endpointId")
  CheckPolicy toDomain(CheckPolicyJpaEntity entity);

  @Mapping(source = "endpointId", target = "endpoint.id")
  CheckPolicyJpaEntity toJpaEntity(CheckPolicy domain);
}
