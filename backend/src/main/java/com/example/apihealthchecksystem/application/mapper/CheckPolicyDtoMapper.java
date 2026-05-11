package com.example.apihealthchecksystem.application.mapper;

import com.example.apihealthchecksystem.application.dto.request.CheckPolicyCreateCommand;
import com.example.apihealthchecksystem.application.dto.response.CheckPolicyDto;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CheckPolicyDtoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  CheckPolicy toDomain(CheckPolicyCreateCommand command);

  CheckPolicyDto toDto(CheckPolicy policy);
}
