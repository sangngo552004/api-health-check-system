package com.example.apihealthchecksystem.infrastructure.persistence.mapper;

import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.AlertRuleJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AlertRuleMapper {

  AlertRule toDomain(AlertRuleJpaEntity entity);

  AlertRuleJpaEntity toEntity(AlertRule domain);
}
