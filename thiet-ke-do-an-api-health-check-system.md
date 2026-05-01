# Thiết kế đồ án: API Health Check System

## 1. Tổng quan đề tài

### 1.1. Tên đề tài
`API Health Check System` - Hệ thống giám sát sức khỏe API và dịch vụ.

### 1.2. Bối cảnh
Trong các hệ thống phần mềm hiện đại, một ứng dụng thường phụ thuộc vào nhiều API hoặc dịch vụ bên ngoài. Khi một dịch vụ gặp sự cố, thời gian phản hồi tăng cao hoặc trả về mã lỗi bất thường, toàn bộ hệ thống có thể bị ảnh hưởng. Vì vậy, cần có một hệ thống trung tâm để theo dõi tình trạng hoạt động của các API, ghi nhận lịch sử, phát hiện bất thường và cảnh báo kịp thời.

### 1.3. Mục tiêu đồ án
Xây dựng một hệ thống cho phép:

- Quản lý danh sách các API cần theo dõi.
- Tự động kiểm tra trạng thái của API theo chu kỳ.
- Ghi nhận kết quả kiểm tra như thời gian phản hồi, mã trạng thái, trạng thái thành công hoặc thất bại.
- Hiển thị tình trạng hiện tại và lịch sử trên dashboard.
- Gửi cảnh báo khi API gặp lỗi liên tục hoặc vượt ngưỡng cấu hình.
- Thể hiện rõ tư duy về kiến trúc phần mềm và áp dụng design pattern một cách hợp lý.

### 1.4. Mục tiêu học thuật
Đồ án không chỉ tập trung vào việc "chạy được", mà còn nhấn mạnh:

- Thiết kế kiến trúc rõ ràng, tách biệt trách nhiệm.
- Áp dụng nguyên lý `SOLID`.
- Áp dụng các `design pattern` đúng bối cảnh.
- Dễ mở rộng thêm loại health check, kênh cảnh báo, hoặc giao diện hiển thị.
- Dễ kiểm thử và dễ bảo trì.

## 2. Phạm vi hệ thống

### 2.1. Phạm vi bản chính
Phiên bản chính của đồ án sẽ bao gồm:

- Quản lý endpoint/API cần giám sát.
- Cấu hình phương thức kiểm tra, chu kỳ kiểm tra, timeout, số lần retry.
- Bộ lập lịch tự động chạy health check.
- Lưu kết quả kiểm tra vào cơ sở dữ liệu.
- Dashboard hiển thị trạng thái tổng quan và lịch sử.
- Cơ chế cảnh báo khi dịch vụ gặp lỗi.
- REST API phục vụ frontend dashboard.

### 2.2. Phạm vi mở rộng để tăng điểm
Nếu còn thời gian, có thể bổ sung:

- Hỗ trợ nhiều loại kiểm tra: `HTTP`, `TCP`, `Database`.
- Quản lý rule cảnh báo linh hoạt.
- Phân quyền người dùng: quản trị viên và người xem.
- Biểu đồ theo thời gian thực hoặc gần thời gian thực.
- Xuất báo cáo thống kê.
- Theo dõi nhiều môi trường: `dev`, `staging`, `production`.

## 3. Bài toán nghiệp vụ

### 3.1. Đối tượng sử dụng

- `Quản trị viên`: tạo, sửa, xóa endpoint; cấu hình lịch kiểm tra; xem dashboard; quản lý rule cảnh báo.
- `Người xem`: xem trạng thái hệ thống và lịch sử kiểm tra.

### 3.2. Các thực thể nghiệp vụ chính

- `MonitoredEndpoint`: đại diện cho một API hoặc dịch vụ cần theo dõi.
- `CheckPolicy`: cấu hình cách thức kiểm tra, chu kỳ, timeout, retry, ngưỡng cảnh báo.
- `HealthCheckResult`: kết quả của một lần kiểm tra.
- `Incident`: sự cố được ghi nhận khi endpoint gặp lỗi liên tục hoặc downtime.
- `AlertRule`: quy tắc xác định khi nào phát cảnh báo.
- `ContactGroup`: nhóm nhận thông báo cảnh báo (email, webhook, users).
- `Notification`: thông tin cảnh báo đã gửi.
- `User`: người dùng hệ thống.

## 4. Yêu cầu chức năng

### 4.1. Quản lý endpoint

- Thêm mới endpoint cần theo dõi.
- Cập nhật thông tin endpoint.
- Xóa hoặc vô hiệu hóa endpoint.
- Xem danh sách endpoint.
- Tìm kiếm endpoint theo tên, URL, môi trường, trạng thái.

### 4.2. Cấu hình health check

- Chọn loại kiểm tra.
- Cấu hình chu kỳ kiểm tra.
- Cấu hình timeout.
- Cấu hình số lần retry.
- Cấu hình ngưỡng cảnh báo.

### 4.3. Thực hiện health check

- Hệ thống tự động thực hiện kiểm tra theo lịch.
- Đo thời gian phản hồi.
- Ghi nhận mã HTTP hoặc lỗi kết nối.
- Xác định trạng thái `UP`, `DOWN`, `DEGRADED`.

### 4.4. Quản lý kết quả và sự cố

- Lưu lịch sử tất cả lần kiểm tra.
- Xác định khi endpoint bắt đầu lỗi.
- Xác định khi endpoint phục hồi.
- Ghi nhận khoảng thời gian downtime.

### 4.5. Dashboard

- Hiển thị tổng số endpoint.
- Hiển thị số endpoint đang `UP`, `DOWN`, `DEGRADED`.
- Hiển thị danh sách endpoint theo mức độ ưu tiên.
- Hiển thị biểu đồ response time.
- Hiển thị lịch sử kiểm tra gần nhất.
- Hiển thị danh sách incident đang mở và đã đóng.

### 4.6. Cảnh báo

- Cảnh báo khi endpoint lỗi liên tiếp vượt ngưỡng.
- Cảnh báo khi response time vượt ngưỡng.
- Ghi nhận lịch sử cảnh báo.
- Cho phép mô phỏng gửi cảnh báo qua email hoặc kênh nội bộ.

## 5. Yêu cầu phi chức năng

- Hệ thống có cấu trúc rõ ràng, dễ mở rộng.
- Tách biệt domain với framework.
- Dễ kiểm thử đơn vị và kiểm thử tích hợp.
- Có logging cho các luồng quan trọng.
- Có xử lý lỗi thống nhất.
- Có thể triển khai bằng Docker.
- Frontend có dashboard trực quan, dễ theo dõi.
- Đảm bảo khả năng xử lý nhiều endpoint theo lịch mà không làm block toàn hệ thống.

## 6. Kiến trúc đề xuất

### 6.1. Kiến trúc tổng thể
Đề xuất áp dụng `Clean Architecture` kết hợp tư tưởng `Hexagonal Architecture`.

Hệ thống chia thành các lớp chính:

- `Domain`: chứa entity, value object, domain service, business rule.
- `Application`: chứa use case, DTO, port interface, orchestration.
- `Infrastructure`: chứa triển khai database, scheduler, HTTP client, notification, security.
- `Delivery`: cung cấp REST API cho frontend.
- `Frontend Dashboard`: giao diện quản trị và giám sát.

### 6.2. Lý do chọn kiến trúc này

- Phù hợp với yêu cầu môn học vì thể hiện rõ phụ thuộc hướng vào trong.
- Dễ giải thích trong báo cáo và sơ đồ kiến trúc.
- Cho phép thay thế hạ tầng mà ít ảnh hưởng đến nghiệp vụ.
- Hỗ trợ kiểm thử use case độc lập với framework.
- Tạo nền tốt để áp dụng design pattern có mục đích.

### 6.3. Luồng phụ thuộc

- `Delivery` phụ thuộc vào `Application`.
- `Infrastructure` triển khai các cổng do `Application` hoặc `Domain` định nghĩa.
- `Domain` không phụ thuộc vào Spring hay database.
- `Frontend` gọi `REST API` từ backend.

## 7. Design pattern dự kiến áp dụng

### 7.1. Repository Pattern
Áp dụng cho tầng truy xuất dữ liệu:

- `EndpointRepository`
- `HealthCheckResultRepository`
- `IncidentRepository`
- `AlertRuleRepository`

Mục đích:

- Tách logic nghiệp vụ khỏi logic lưu trữ.
- Hỗ trợ thay đổi công nghệ database dễ hơn.

### 7.2. Strategy Pattern
Áp dụng cho nhiều kiểu health check:

- `HttpHealthCheckStrategy`
- `TcpHealthCheckStrategy`
- `DatabaseHealthCheckStrategy`

Mục đích:

- Mỗi loại check có cách xử lý riêng.
- Dễ mở rộng thêm loại check mới mà không sửa logic cũ nhiều.

### 7.3. Factory Pattern
Áp dụng để chọn strategy phù hợp với từng loại endpoint hoặc loại check.

Ví dụ:

- `HealthCheckStrategyFactory`

Mục đích:

- Tách logic khởi tạo strategy ra khỏi use case.

### 7.4. Observer hoặc Publisher-Subscriber
Áp dụng cho cơ chế phát cảnh báo khi có sự cố hoặc khi endpoint phục hồi.

Ví dụ:

- Khi phát hiện incident, hệ thống phát sự kiện.
- Các thành phần notification sẽ đăng ký lắng nghe.

Mục đích:

- Giảm coupling giữa phần kiểm tra và phần cảnh báo.

### 7.5. Adapter Pattern
Áp dụng tại tầng infrastructure:

- Adapter gọi HTTP.
- Adapter gửi email hoặc mock notification.
- Adapter kết nối cơ sở dữ liệu.

Mục đích:

- Che giấu chi tiết phụ thuộc vào thư viện hoặc hạ tầng.

### 7.6. Template Method hoặc Policy Object
Có thể áp dụng cho quy trình kiểm tra chuẩn:

1. Chuẩn bị request
2. Thực hiện check
3. Đo thời gian
4. Phân tích kết quả
5. Tạo `HealthCheckResult`

Mục đích:

- Chuẩn hóa flow của các loại check khác nhau.

### 7.7. Specification Pattern
Có thể dùng để lọc endpoint hoặc incident theo điều kiện tìm kiếm phức tạp.

Mục đích:

- Làm phần truy vấn rõ ràng, dễ mở rộng.

## 8. Công nghệ đề xuất

### 8.1. Backend

- `Java 17`
- `Spring Boot`
- `Spring Web`
- `Spring Data JPA`
- `Spring Validation`
- `Spring Scheduler`
- `PostgreSQL`
- `Flyway`
- `Lombok` nếu cần
- `MapStruct` nếu muốn mapping rõ ràng
- `Resilience4j` nếu muốn nâng chất lượng kỹ thuật

### 8.2. Frontend

- `React`
- `Vite`
- `TypeScript`
- `Tailwind CSS` hoặc `MUI`
- `Recharts` hoặc `Chart.js` để vẽ biểu đồ
- `Axios` hoặc `fetch`

### 8.3. DevOps và hỗ trợ

- `Docker`
- `Docker Compose`
- `JUnit 5`
- `Mockito`
- `Testcontainers` nếu muốn làm mạnh phần integration test
- `OpenAPI / Swagger`

## 9. Cấu trúc module đề xuất

### 9.1. Cấu trúc backend

```text
backend/
  src/main/java/com/example/apihealthchecksystem/
    domain/
      model/
      valueobject/
      service/
      event/
      exception/
    application/
      port/in/
      port/out/
      usecase/
      dto/
      mapper/
    infrastructure/
      persistence/
      scheduler/
      http/
      notification/
      config/
      security/
      event/
    delivery/
      rest/
      advice/
    ApiHealthCheckSystemApplication.java
```

### 9.2. Cấu trúc frontend

```text
frontend/
  src/
    app/
    pages/
    components/
    features/
    services/
    hooks/
    types/
    utils/
```

## 10. Mô hình dữ liệu sơ bộ

### 10.1. Bảng `monitored_endpoints`

- `id`
- `name`
- `url`
- `method`
- `environment`
- `check_type`
- `is_active`
- `created_at`
- `updated_at`
- `policy_id`

### 10.2. Bảng `check_policies`

- `id`
- `interval_seconds`
- `timeout_millis`
- `retry_count`
- `failure_threshold`
- `latency_threshold_millis`
- `expected_status_code`
- `expected_response_body`
- `response_regex`
- `created_by`

### 10.3. Bảng `health_check_results`

- `id`
- `endpoint_id`
- `checked_at`
- `status`
- `http_status_code`
- `response_time_millis`
- `error_message`
- `response_payload`
- `node_id`
- `success`

### 10.4. Bảng `incidents`

- `id`
- `endpoint_id`
- `started_at`
- `resolved_at`
- `status`
- `reason`

### 10.5. Bảng `alert_rules`

- `id`
- `name`
- `rule_type`
- `operator`
- `threshold_value`
- `is_active`
- `override_default_contacts`
- `created_by`

### 10.6. Bảng `contact_groups`

- `id`
- `name`
- `description`
- `is_active`
- `created_by`

### 10.7. Bảng `notifications`

- `id`
- `incident_id`
- `channel`
- `recipient`
- `message`
- `sent_at`
- `status`

### 10.8. Bảng `users`

- `id`
- `username`
- `email`
- `phone_number`
- `password_hash`
- `role`
- `is_active`

## 11. Use case quan trọng

### 11.1. Quản trị viên thêm endpoint

1. Quản trị viên nhập thông tin endpoint.
2. Hệ thống kiểm tra dữ liệu hợp lệ.
3. Hệ thống lưu endpoint và policy mặc định.
4. Endpoint xuất hiện trên dashboard.

### 11.2. Scheduler thực hiện health check

1. Scheduler lấy danh sách endpoint đang hoạt động.
2. Chọn strategy phù hợp với loại check.
3. Thực hiện kiểm tra.
4. Lưu kết quả.
5. Phân tích xem có phát sinh incident hoặc alert không.
6. Cập nhật dashboard.

### 11.3. Hệ thống phát hiện incident

1. Một endpoint thất bại liên tục vượt ngưỡng.
2. Hệ thống tạo incident mới.
3. Phát sự kiện incident created.
4. Notification service nhận sự kiện và gửi cảnh báo.

### 11.4. Hệ thống phát hiện phục hồi

1. Endpoint đang trong trạng thái lỗi được kiểm tra lại thành công.
2. Hệ thống đóng incident.
3. Ghi nhận thời gian phục hồi.
4. Có thể gửi thông báo recovery.

## 12. Định hướng giao diện dashboard

### 12.1. Các màn hình chính

- Trang tổng quan dashboard
- Trang danh sách endpoint
- Trang chi tiết endpoint
- Trang danh sách incident
- Trang cấu hình rule cảnh báo
- Trang đăng nhập

### 12.2. Nội dung dashboard tổng quan

- Thẻ thống kê tổng số endpoint
- Thẻ thống kê trạng thái `UP`, `DOWN`, `DEGRADED`
- Biểu đồ response time theo thời gian
- Danh sách endpoint lỗi gần đây
- Danh sách incident đang mở
- Bảng lịch sử health check gần nhất

### 12.3. Định hướng UX/UI

- Giao diện hiện đại, chuyên nghiệp, dễ demo.
- Màu sắc phân biệt rõ trạng thái sức khỏe hệ thống.
- Có bộ lọc theo môi trường, trạng thái, thời gian.
- Ưu tiên trực quan hóa dữ liệu bằng biểu đồ và badge trạng thái.

## 13. Kế hoạch triển khai theo giai đoạn

### Giai đoạn 1. Phân tích và đặc tả

- Hoàn thiện tài liệu yêu cầu.
- Xác định actor, use case, business rule.
- Chốt kiến trúc và công nghệ.
- Vẽ sơ đồ use case và sơ đồ thành phần.

### Giai đoạn 2. Dựng khung dự án

- Khởi tạo backend Spring Boot.
- Khởi tạo frontend React.
- Tạo cấu trúc thư mục theo kiến trúc đã chọn.
- Cấu hình database, migration, Docker Compose.

### Giai đoạn 3. Xây dựng domain và use case cốt lõi

- Thiết kế entity và repository interface.
- Xây dựng use case quản lý endpoint.
- Xây dựng use case health check.
- Xây dựng use case incident và alert.

### Giai đoạn 4. Tích hợp hạ tầng

- Triển khai repository JPA.
- Tích hợp scheduler.
- Tích hợp HTTP client.
- Tích hợp notification adapter.

### Giai đoạn 5. Xây dựng frontend dashboard

- Dựng layout dashboard.
- Tích hợp API backend.
- Hiển thị biểu đồ, trạng thái, lịch sử.
- Tạo form CRUD endpoint.

### Giai đoạn 6. Hoàn thiện và kiểm thử

- Viết unit test và integration test.
- Tối ưu luồng xử lý lỗi.
- Hoàn thiện logging và tài liệu API.
- Chạy demo toàn hệ thống.

### Giai đoạn 7. Chuẩn bị báo cáo

- Viết phần mô tả bài toán.
- Viết phần phân tích kiến trúc.
- Trình bày design pattern đã dùng.
- Đính kèm UML, ERD, sequence diagram.
- Chuẩn bị kịch bản thuyết trình và demo.

## 14. Phân chia ưu tiên tính năng

### 14.1. Bắt buộc phải có

- CRUD endpoint
- Scheduler health check
- Lưu lịch sử kiểm tra
- Dashboard tổng quan
- Incident cơ bản
- Alert cơ bản

### 14.2. Nên có để tăng điểm

- Nhiều loại check strategy
- Retry và timeout policy
- Role-based access
- Biểu đồ trực quan
- OpenAPI tài liệu hóa API
- Test coverage tốt

### 14.3. Có thể thêm nếu còn thời gian

- WebSocket cập nhật realtime
- Gửi email thật
- Xuất PDF hoặc Excel báo cáo
- Theo dõi SLA

## 15. Rủi ro và hướng kiểm soát

### 15.1. Rủi ro quá tải phạm vi
Nếu làm quá nhiều tính năng từ đầu, dự án dễ bị dang dở.

Hướng xử lý:

- Chốt một phiên bản lõi trước.
- Triển khai theo thứ tự ưu tiên.

### 15.2. Rủi ro kiến trúc quá phức tạp
Nếu lạm dụng pattern, hệ thống có thể bị nặng và khó code.

Hướng xử lý:

- Chỉ dùng pattern khi có vấn đề cụ thể cần giải quyết.
- Giữ domain rõ ràng trước, rồi mới thêm mở rộng.

### 15.3. Rủi ro frontend chiếm quá nhiều thời gian

Hướng xử lý:

- Dùng dashboard tập trung vào dữ liệu.
- Thiết kế UI vừa đủ đẹp, không sa đà hiệu ứng.

## 16. Kết luận định hướng

Đây là một đề tài rất phù hợp để thể hiện năng lực về kiến trúc và thiết kế phần mềm. Nếu triển khai đúng hướng, đồ án có thể nổi bật ở các điểm sau:

- Có bài toán thực tế và dễ demo.
- Có kiến trúc rõ ràng, dễ giải thích.
- Có nhiều design pattern áp dụng hợp lý.
- Có backend lẫn frontend.
- Có khả năng mở rộng và thể hiện tư duy kỹ thuật tốt.

## 17. Đề xuất bước tiếp theo

Ngay sau tài liệu này, nên tiếp tục theo thứ tự:

1. Chốt danh sách chức năng của phiên bản 1.
2. Vẽ sơ đồ kiến trúc tổng thể và sơ đồ use case.
3. Chốt cấu trúc thư mục backend và frontend.
4. Khởi tạo project.
5. Triển khai module `endpoint management`.
6. Triển khai module `health check scheduler`.
7. Triển khai dashboard.

