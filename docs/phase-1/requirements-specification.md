# Đặc tả yêu cầu giai đoạn 1

## 1. Mục tiêu

Giai đoạn 1 tập trung vào việc chốt bài toán, phạm vi, tác nhân, use case, business rule và định hướng kiến trúc để làm nền cho các bước triển khai sau này.

## 2. Phạm vi phiên bản đầu tiên

### Trong phạm vi

- Quản lý endpoint cần giám sát.
- Cấu hình policy kiểm tra gồm chu kỳ, timeout, retry, ngưỡng lỗi.
- Tự động chạy health check theo lịch.
- Lưu lịch sử kết quả kiểm tra.
- Hiển thị dashboard tổng quan.
- Ghi nhận incident khi endpoint lỗi liên tục.
- Gửi cảnh báo mô phỏng qua một kênh notification cơ bản.

### Ngoài phạm vi giai đoạn đầu

- Theo dõi nhiều tenant.
- Notification thật qua email, Slack, Telegram.
- Cơ chế phân quyền phức tạp.
- Real-time streaming dashboard.
- Health check nâng cao cho nhiều protocol ngoài HTTP.

## 3. Actor

### Quản trị viên

- Tạo, sửa, vô hiệu hóa endpoint.
- Cấu hình policy health check.
- Xem dashboard, lịch sử, incident.
- Quản lý rule cảnh báo.

### Người xem

- Xem dashboard tổng quan.
- Xem trạng thái endpoint và lịch sử kiểm tra.
- Xem danh sách incident.

### Scheduler hệ thống

- Tự động lấy danh sách endpoint đang hoạt động.
- Kích hoạt use case health check theo chu kỳ.
- Gửi sự kiện cho luồng incident và notification.

## 4. Use case chính

### UC-01. Tạo endpoint giám sát

- Actor: `Quản trị viên`
- Mô tả: nhập thông tin endpoint và policy mặc định.
- Kết quả: endpoint được lưu và sẵn sàng cho scheduler xử lý.

### UC-02. Cập nhật endpoint

- Actor: `Quản trị viên`
- Mô tả: chỉnh sửa URL, môi trường, expected status code hoặc trạng thái kích hoạt.
- Kết quả: thông tin mới được áp dụng cho các lần check tiếp theo.

### UC-03. Xem danh sách endpoint

- Actor: `Quản trị viên`, `Người xem`
- Mô tả: xem danh sách endpoint có lọc theo tên, môi trường, trạng thái.
- Kết quả: người dùng nắm được tình trạng hệ thống hiện tại.

### UC-04. Chạy health check theo lịch

- Actor: `Scheduler hệ thống`
- Mô tả: scheduler lấy endpoint đang hoạt động, chọn strategy phù hợp và thực hiện kiểm tra.
- Kết quả: tạo ra `HealthCheckResult`.

### UC-05. Ghi nhận incident

- Actor: `Scheduler hệ thống`
- Mô tả: khi số lần thất bại liên tiếp vượt ngưỡng, hệ thống tạo incident mới.
- Kết quả: incident được mở và phát sự kiện cảnh báo.

### UC-06. Đóng incident khi phục hồi

- Actor: `Scheduler hệ thống`
- Mô tả: endpoint đang lỗi được kiểm tra lại thành công.
- Kết quả: incident đang mở được đóng và ghi nhận thời điểm phục hồi.

### UC-07. Xem dashboard

- Actor: `Quản trị viên`, `Người xem`
- Mô tả: xem tổng quan số endpoint theo trạng thái, incident đang mở, lịch sử gần nhất.
- Kết quả: có góc nhìn trực quan để đánh giá hệ thống.

### UC-08. Gửi cảnh báo

- Actor: `Scheduler hệ thống`
- Mô tả: sau khi mở incident hoặc vượt ngưỡng latency, hệ thống gửi notification.
- Kết quả: lịch sử gửi cảnh báo được lưu lại.

## 5. Business rule

- `BR-01`: Mỗi endpoint phải có `name`, `url`, `method`, `environment`, `checkType`.
- `BR-02`: URL phải hợp lệ và dùng giao thức phù hợp với loại check.
- `BR-03`: Chỉ endpoint `isActive = true` mới được scheduler xử lý.
- `BR-04`: Mỗi endpoint phải có đúng một policy đang hiệu lực tại một thời điểm.
- `BR-05`: Một lần health check phải ghi nhận được thời gian kiểm tra và trạng thái kết quả, kể cả khi lỗi kết nối.
- `BR-06`: Incident chỉ được mở khi số lần thất bại liên tiếp đạt hoặc vượt `failureThreshold`.
- `BR-07`: Tại một thời điểm chỉ có tối đa một incident đang mở cho mỗi endpoint.
- `BR-08`: Incident chỉ được đóng khi endpoint đã kiểm tra thành công trở lại.
- `BR-09`: Trạng thái `DEGRADED` được dùng khi endpoint vẫn phản hồi đúng nhưng độ trễ vượt `latencyThresholdMillis`.
- `BR-10`: Notification chỉ được phát khi rule cảnh báo đang hoạt động.

## 6. Yêu cầu chức năng

### Quản lý endpoint

- Tạo mới endpoint.
- Cập nhật endpoint.
- Vô hiệu hóa endpoint thay vì xóa cứng ở phiên bản đầu.
- Tìm kiếm và lọc endpoint.

### Cấu hình policy

- Cấu hình `intervalSeconds`.
- Cấu hình `timeoutMillis`.
- Cấu hình `retryCount`.
- Cấu hình `failureThreshold`.
- Cấu hình `latencyThresholdMillis`.

### Health check

- Hỗ trợ `HTTP health check` cho phiên bản đầu.
- Đo response time.
- So khớp mã phản hồi mong đợi.
- Phân loại trạng thái `UP`, `DOWN`, `DEGRADED`.

### Incident và notification

- Tạo incident khi lỗi liên tiếp vượt ngưỡng.
- Đóng incident khi phục hồi.
- Gửi cảnh báo mô phỏng.
- Lưu lịch sử notification.

### Dashboard

- Thống kê tổng số endpoint.
- Hiển thị số endpoint `UP`, `DOWN`, `DEGRADED`.
- Hiển thị incident đang mở.
- Hiển thị danh sách kết quả kiểm tra gần nhất.

## 7. Yêu cầu phi chức năng

- Kiến trúc tách biệt domain với framework.
- Code dễ kiểm thử đơn vị.
- Dễ thay thế hạ tầng lưu trữ và notification.
- Có logging cho scheduler, health check, incident.
- Xử lý lỗi thống nhất ở tầng delivery.
- Có thể chạy cục bộ qua Docker Compose.

## 8. Chốt công nghệ giai đoạn 1

### Backend

- `Java 17`
- `Spring Boot`
- `Spring Web`
- `Spring Data JPA`
- `Spring Scheduler`
- `PostgreSQL`
- `Flyway`

### Frontend

- `React`
- `Vite`
- `TypeScript`

### Hỗ trợ kỹ thuật

- `Docker Compose`
- `OpenAPI`
- `JUnit 5`
- `Mockito`

## 9. Kiến trúc được chốt

- Chọn `Clean Architecture` làm trục chính.
- Dùng `Repository Pattern` cho persistence.
- Dùng `Strategy + Factory` cho các loại health check.
- Dùng `Publisher-Subscriber` cho incident và notification event.
- Giới hạn phiên bản đầu ở `HTTP health check` nhưng thiết kế sẵn điểm mở rộng cho `TCP` và `Database`.

## 10. Deliverable của giai đoạn 1

- Tài liệu tổng quan dự án trong `README.md`.
- Tài liệu đặc tả yêu cầu hiện tại.
- Sơ đồ use case bằng Mermaid.
- Sơ đồ thành phần bằng Mermaid.
- Tài liệu thiết kế nền `thiet-ke-do-an-api-health-check-system.md`.
