package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
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
  private Integer expectedStatusCode;
  private Boolean isActive;
  private CheckPolicy policy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
