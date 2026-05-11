package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface WorkspaceMapper {

  Workspace toDomain(WorkspaceJpaEntity entity);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  WorkspaceJpaEntity toEntity(Workspace domain);
}
