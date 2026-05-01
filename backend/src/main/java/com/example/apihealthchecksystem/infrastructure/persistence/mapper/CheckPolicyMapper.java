package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.CheckPolicyJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CheckPolicyMapper {

  CheckPolicy toDomain(CheckPolicyJpaEntity entity);

  CheckPolicyJpaEntity toEntity(CheckPolicy domain);
}
