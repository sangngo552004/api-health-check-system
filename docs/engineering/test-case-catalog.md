# Test Case Catalog

Tài liệu này là bộ `test case` ở mức dự án, dùng để:

- xác định những gì cần test
- đối chiếu với coverage hiện có
- làm guide khi viết thêm test
- làm tài liệu kiểm thử cho báo cáo

Tài liệu này không thay thế test automation hiện có, mà đóng vai trò `test design` và `coverage map`.

## 1. Mục tiêu của bộ test case

- kiểm tra business rule cốt lõi của hệ thống giám sát
- kiểm tra đúng boundary của Clean Architecture
- kiểm tra contract API quan trọng
- kiểm tra persistence adapter hoạt động đúng
- xác định các khoảng trống test còn thiếu

## 2. Phân loại test trong dự án

### 2.1. Architecture tests

Mục tiêu:

- đảm bảo dependency direction giữa các layer không bị phá

Hiện có:

- `CleanArchitectureTest`

### 2.2. Domain tests

Mục tiêu:

- kiểm tra rule thuần, không phụ thuộc framework

Hiện có:

- `DomainEventTest`
- `IncidentEvaluationServiceTest`

Lưu ý:

- code hiện tại cũng đã có `IncidentAnalyzer`, nên có thể bổ sung test cho analyzer nếu muốn phản ánh đúng hơn business rule mới

### 2.3. Use case tests

Mục tiêu:

- kiểm tra orchestration của application layer

Hiện có:

- `ManageEndpointServiceTest`
- `ManageCheckPolicyServiceTest`
- `ManageAlertRuleServiceTest`
- `ManageContactGroupServiceTest`
- `ManageWorkspaceServiceTest`
- `GetDashboardStatsServiceTest`

### 2.4. Integration tests

Mục tiêu:

- kiểm tra adapter và persistence với framework thật

Hiện có:

- `AlertRuleRepositoryAdapterIT`
- `CheckPolicyRepositoryAdapterIT`
- `ContactGroupRepositoryAdapterIT`
- `EndpointRepositoryAdapterIT`
- `WorkspaceRepositoryAdapterIT`

### 2.5. Infrastructure behavior tests

Mục tiêu:

- kiểm tra hành vi của component hạ tầng cụ thể

Hiện có:

- `TcpHealthCheckExecutorTest`

### 2.6. System/spec tests

Mục tiêu:

- đảm bảo app boot được
- đảm bảo OpenAPI đồng bộ với runtime

Hiện có:

- `ApiHealthCheckSystemApplicationTests`
- `SwaggerGeneratorTest`

## 3. Test case theo module nghiệp vụ

## 3.1. Authentication

### TC-AUTH-01. Login thành công với credential hợp lệ

Mục tiêu:

- backend trả access token và refresh token hợp lệ

Kết quả mong đợi:

- `success=true`
- có `accessToken`
- có `refreshToken`

Trạng thái:

- `nên có`
- cần rà lại coverage hiện tại nếu chưa có test trực tiếp

### TC-AUTH-02. Login thất bại với credential sai

Mục tiêu:

- backend từ chối xác thực sai

Kết quả mong đợi:

- trả lỗi phù hợp

Trạng thái:

- `nên có thêm nếu chưa có`

### TC-AUTH-03. Refresh token hợp lệ

Mục tiêu:

- đổi refresh token lấy access token mới

Trạng thái:

- `nên có thêm nếu chưa có`

## 3.2. Workspace

### TC-WS-01. Tạo workspace thành công bởi SUPER_ADMIN

Mục tiêu:

- xác nhận role global `SUPER_ADMIN` mới được tạo workspace

### TC-WS-02. User thường không được tạo workspace nếu không phải SUPER_ADMIN

### TC-WS-03. Lấy danh sách workspace của user

### TC-WS-04. Thêm member vào workspace

### TC-WS-05. Xóa member khỏi workspace

### TC-WS-06. Không cho truy cập workspace khác

Coverage hiện có:

- đã có use case test cho `ManageWorkspaceService`
- có adapter integration test cho workspace repository

## 3.3. Endpoint management

### TC-END-01. Tạo endpoint thành công với policy hợp lệ trong cùng workspace

### TC-END-02. Tạo endpoint thất bại khi policy không tồn tại

### TC-END-03. Tạo endpoint thất bại khi policy thuộc workspace khác

### TC-END-04. Update endpoint thành công

### TC-END-05. Update endpoint thất bại khi endpoint thuộc workspace khác

### TC-END-06. Lấy danh sách endpoint theo workspace

### TC-END-07. Xóa endpoint thành công

Coverage hiện có:

- `ManageEndpointServiceTest`
- `EndpointRepositoryAdapterIT`

## 3.4. Check policy

### TC-POL-01. Tạo policy thành công

### TC-POL-02. Update policy thành công

### TC-POL-03. Xóa policy thành công

### TC-POL-04. Không tìm thấy policy khi id sai

### TC-POL-05. Default timeout/failure threshold hoạt động đúng

Coverage hiện có:

- `ManageCheckPolicyServiceTest`
- `CheckPolicyRepositoryAdapterIT`

## 3.5. Alert rule

### TC-ALT-01. Tạo alert rule thành công

### TC-ALT-02. Update alert rule thành công

### TC-ALT-03. Delete alert rule thành công

### TC-ALT-04. List alert rule theo workspace

Coverage hiện có:

- `ManageAlertRuleServiceTest`
- `AlertRuleRepositoryAdapterIT`

## 3.6. Contact group

### TC-CG-01. Tạo contact group thành công

### TC-CG-02. Update contact group thành công

### TC-CG-03. Delete contact group thành công

### TC-CG-04. Lấy danh sách contact group theo workspace

Coverage hiện có:

- `ManageContactGroupServiceTest`
- `ContactGroupRepositoryAdapterIT`

## 3.7. Monitoring engine

### TC-MON-01. Scheduler quét tất cả endpoint active

### TC-MON-02. Endpoint inactive không bị monitor

### TC-MON-03. Endpoint không có policy bị bỏ qua

### TC-MON-04. Chọn đúng `HealthCheckExecutor` theo `CheckType`

### TC-MON-05. Lưu `HealthCheckResult` sau mỗi lần check

### TC-MON-06. Cập nhật trạng thái endpoint sau khi check

### TC-MON-07. Khi không có executor phù hợp thì log lỗi và bỏ qua an toàn

Trạng thái:

- `nên tăng coverage`
- đây là khu vực có giá trị cao cho test

## 3.8. HTTP health check

### TC-HTTP-01. Trả `UP` khi status code đúng và response hợp lệ

### TC-HTTP-02. Trả `DOWN` khi status code sai

### TC-HTTP-03. Trả `DOWN` khi body không chứa expected content

### TC-HTTP-04. Trả `DOWN` khi regex không match

### TC-HTTP-05. Trả `DEGRADED` khi latency vượt ngưỡng

### TC-HTTP-06. Trả `DOWN` khi exception kết nối xảy ra

Trạng thái:

- `nên bổ sung`

## 3.9. TCP health check

### TC-TCP-01. Kết nối thành công tới host:port hợp lệ

### TC-TCP-02. Parse được URL kiểu `tcp://host:port`

### TC-TCP-03. Trả `DEGRADED` khi latency cao

### TC-TCP-04. Trả `DOWN` khi không kết nối được

Coverage hiện có:

- `TcpHealthCheckExecutorTest`

## 3.10. Incident lifecycle

### TC-INC-01. Mở incident khi số lần fail liên tiếp đạt `failureThreshold`

### TC-INC-02. Không mở incident nếu chưa đạt ngưỡng

### TC-INC-03. Không mở incident chồng khi đã có incident mở

### TC-INC-04. Đóng incident khi endpoint phục hồi

### TC-INC-05. Severity được xác định đúng theo số lần fail

### TC-INC-06. Phát event sau khi mở incident

### TC-INC-07. Phát event sau khi đóng incident

Trạng thái:

- `nên tăng coverage`

## 3.11. Dashboard

### TC-DB-01. Trả đúng tổng số endpoint

### TC-DB-02. Trả đúng số endpoint UP

### TC-DB-03. Trả đúng số endpoint DOWN

### TC-DB-04. Trả đúng số endpoint DEGRADED

### TC-DB-05. Trả đúng số incident đang mở

### TC-DB-06. Trả lịch sử latency theo endpoint

Coverage hiện có:

- `GetDashboardStatsServiceTest`

## 3.12. Notification flow

### TC-NOTI-01. Khi incident mở, listener gọi `NotificationPort.sendIncidentAlert`

### TC-NOTI-02. Khi incident đóng, listener gọi `NotificationPort.sendRecoveryAlert`

### TC-NOTI-03. Nếu không tìm thấy incident hoặc endpoint, listener fail an toàn

Trạng thái:

- `nên bổ sung`

## 4. Test case về kiến trúc và convention

### TC-ARCH-01. Delivery không phụ thuộc ngược vào infrastructure như business API

### TC-ARCH-02. Application không dùng trực tiếp JPA repository

### TC-ARCH-03. Domain không phụ thuộc Spring Web/Security/JPA

### TC-ARCH-04. Use case transaction boundary vẫn được áp ở application layer

Hiện tại:

- mới có `CleanArchitectureTest` ở mức layer tổng quát
- có thể mở rộng thêm các rule chi tiết hơn

## 5. Test case về frontend

Frontend hiện chưa có automation test rõ ràng, nhưng nên có catalog để biết sau này cần test gì.

### TC-FE-01. Login screen gọi API đúng và lưu session đúng

### TC-FE-02. App tự refresh session khi có refresh token

### TC-FE-03. WorkspaceContext chọn active workspace đúng

### TC-FE-04. Dashboard render stats và chart đúng dữ liệu API

### TC-FE-05. Endpoints screen CRUD hoạt động đúng với store

### TC-FE-06. Policies screen CRUD hoạt động đúng

### TC-FE-07. Alerts screen CRUD hoạt động đúng

### TC-FE-08. Contacts screen CRUD hoạt động đúng

### TC-FE-09. Members screen add/remove member hoạt động đúng

### TC-FE-10. Khi API trả `401/403`, client logout đúng cách

Trạng thái:

- `chưa có automation coverage rõ`

## 6. Coverage gap ưu tiên nên lấp trước

Nếu chỉ có thời gian bổ sung một ít test nữa, nên ưu tiên:

1. `MonitorEndpointService` end-to-end orchestration ở mức unit test
2. `IncidentAnalyzer` / incident lifecycle rule
3. `IncidentEventListener`
4. `HTTP health check executor`
5. auth flow quan trọng

## 7. Checklist khi thêm tính năng mới

Sau khi thêm feature mới, hãy hỏi:

- logic mới thuộc domain hay use case
- đã có test đúng tầng chưa
- đã có case success và failure path chưa
- nếu đổi API thì OpenAPI sync test có cần cập nhật không
- nếu feature mới là partial thì docs progress/status đã phản ánh chưa

## 8. Liên kết với tài liệu khác

- chiến lược test và CI: [testing-and-quality.md](testing-and-quality.md)
- code quality và CI: [code-quality.md](code-quality.md)
- tiến độ và backlog: [../project/progress-checklist.md](../project/progress-checklist.md)
- quy ước layer: [../architecture/layer-coding-guidelines.md](../architecture/layer-coding-guidelines.md)
- guide cho agent: [ai-agent-guide.md](ai-agent-guide.md)
