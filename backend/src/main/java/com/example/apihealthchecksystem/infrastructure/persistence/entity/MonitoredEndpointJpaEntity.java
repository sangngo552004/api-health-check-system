package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "monitored_endpoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoredEndpointJpaEntity extends BaseJpaEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 1024)
  private String url;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private HttpMethod method;

  @Column(length = 50)
  private String environment;

  @Enumerated(EnumType.STRING)
  @Column(name = "check_type", nullable = false, length = 50)
  private CheckType checkType;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "status")
  private String status;

  @Column(name = "policy_id")
  private Long policyId;

  @ElementCollection
  @CollectionTable(name = "endpoint_headers", joinColumns = @JoinColumn(name = "endpoint_id"))
  @MapKeyColumn(name = "header_key")
  @Column(name = "header_value")
  private Map<String, String> headers;

  @Column(name = "request_body", columnDefinition = "TEXT")
  private String requestBody;

  @Column(name = "created_by")
  private Long createdBy;

  @ElementCollection
  @CollectionTable(name = "endpoint_tags", joinColumns = @JoinColumn(name = "endpoint_id"))
  @Column(name = "tag")
  private List<String> tags;

  @ElementCollection
  @CollectionTable(name = "endpoint_alert_rules", joinColumns = @JoinColumn(name = "endpoint_id"))
  @Column(name = "alert_rule_id")
  private List<Long> alertRuleIds;

  @ElementCollection
  @CollectionTable(
      name = "endpoint_contact_groups",
      joinColumns = @JoinColumn(name = "endpoint_id"))
  @Column(name = "contact_group_id")
  private List<Long> contactGroupIds;
}
