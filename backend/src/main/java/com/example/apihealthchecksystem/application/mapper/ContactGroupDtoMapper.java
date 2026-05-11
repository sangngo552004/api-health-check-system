package com.example.apihealthchecksystem.application.mapper;

import com.example.apihealthchecksystem.application.dto.request.ContactGroupCreateCommand;
import com.example.apihealthchecksystem.application.dto.response.ContactGroupDto;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ContactGroupDtoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", constant = "true")
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  ContactGroup toDomain(ContactGroupCreateCommand command);

  ContactGroupDto toDto(ContactGroup group);
}
