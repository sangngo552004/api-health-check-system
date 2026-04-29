package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  @Column(name = "expected_status_code")
  private Integer expectedStatusCode;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @OneToOne(
      mappedBy = "endpoint",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private CheckPolicyJpaEntity policy;

  public void setPolicy(CheckPolicyJpaEntity policy) {
    if (policy != null) {
      policy.setEndpoint(this);
    }
    this.policy = policy;
  }
}
