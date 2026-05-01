package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Incident {
  private Long id;
  private Long endpointId;
  private LocalDateTime startedAt;
  private LocalDateTime resolvedAt;
  private IncidentStatus status;
  private String reason;
  private Integer failureCount;

  /** Mức độ nghiêm trọng của sự cố. */
  private IncidentSeverity severity;

  /** Nguyên nhân gốc rễ (do con người cập nhật sau khi điều tra). */
  private String rootCause;

  /** Danh sách ID các lần check thất bại dẫn đến incident này. Giúp truy vết vết (Traceability). */
  private List<Long> failingResultIds;

  public void resolve(LocalDateTime resolvedAt) {
    this.status = IncidentStatus.RESOLVED;
    this.resolvedAt = resolvedAt;
  }

  public boolean isOpen() {
    return IncidentStatus.OPEN.equals(this.status);
  }
}
