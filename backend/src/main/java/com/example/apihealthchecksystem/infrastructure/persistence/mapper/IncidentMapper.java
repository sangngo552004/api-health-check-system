package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.IncidentJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface IncidentMapper {

  Incident toDomain(IncidentJpaEntity entity);

  IncidentJpaEntity toEntity(Incident domain);
}
