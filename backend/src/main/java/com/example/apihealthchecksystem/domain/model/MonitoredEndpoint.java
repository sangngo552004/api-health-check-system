package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitoredEndpoint {
  private Long id;
  private String name;
  private String url;
  private HttpMethod method;
  private String environment;
  private CheckType checkType;
  private Boolean isActive;
  private EndpointStatus status;
  private List<String> tags;
  private Long policyId;
  private List<Long> alertRuleIds;
  private List<Long> contactGroupIds;
  private Map<String, String> headers;
  private String requestBody;
  private Long createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
