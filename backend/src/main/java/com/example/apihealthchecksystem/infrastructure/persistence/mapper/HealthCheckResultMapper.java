package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.HealthCheckResultJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface HealthCheckResultMapper {

  @Mapping(source = "status", target = "status", qualifiedByName = "stringToCheckStatus")
  HealthCheckResult toDomain(HealthCheckResultJpaEntity entity);

  @Mapping(source = "status", target = "status", qualifiedByName = "checkStatusToString")
  HealthCheckResultJpaEntity toEntity(HealthCheckResult domain);

  @Named("stringToCheckStatus")
  default CheckStatus stringToCheckStatus(String status) {
    if (status == null) {
      return null;
    }
    return CheckStatus.valueOf(status);
  }

  @Named("checkStatusToString")
  default String checkStatusToString(CheckStatus status) {
    if (status == null) {
      return null;
    }
    return status.name();
  }
}
