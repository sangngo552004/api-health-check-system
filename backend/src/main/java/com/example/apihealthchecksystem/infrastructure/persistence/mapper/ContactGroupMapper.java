package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.ContactGroupJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ContactGroupMapper {

  ContactGroup toDomain(ContactGroupJpaEntity entity);

  ContactGroupJpaEntity toEntity(ContactGroup domain);
}
