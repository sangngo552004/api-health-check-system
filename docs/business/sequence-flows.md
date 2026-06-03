# Sequence Flows

Tài liệu này mô tả các luồng chính của hệ thống theo cách gần với BA/System Analyst để có thể dùng làm nền cho sequence diagram, activity diagram hoặc thuyết trình.

## Cách dùng tài liệu này

Mỗi flow bên dưới đều có:

- mục tiêu nghiệp vụ
- actor tham gia
- điều kiện đầu vào
- luồng chính
- ngoại lệ hoặc biến thể
- trạng thái hiện tại trong code

Bạn có thể chuyển trực tiếp các bước này thành sơ đồ sequence nếu cần.

## Flow 1. Đăng nhập và khởi tạo phiên làm việc

### Mục tiêu

Cho phép người dùng xác thực và vào được workspace phù hợp.

### Actor

- User
- Frontend
- Auth API
- Workspace API

### Điều kiện đầu vào

- người dùng có tài khoản hợp lệ trong hệ thống

### Luồng chính

1. Người dùng mở màn hình đăng nhập.
2. Người dùng nhập username và password.
3. Frontend gửi `POST /api/v1/auth/login`.
4. Backend xác thực thông tin đăng nhập.
5. Backend trả về `accessToken`, `refreshToken`, `tokenType`, `expiresIn`.
6. Frontend giữ `accessToken` trong memory.
7. Frontend lưu `refreshToken` trong `localStorage`.
8. Frontend giải mã JWT để lấy thông tin user cơ bản.
9. Sau khi có user, frontend gọi `GET /api/v1/workspaces/my`.
10. Backend trả danh sách workspace mà user thuộc về.
11. Frontend chọn workspace hiện hành:
12. Nếu local storage đã có `workspace_id` hợp lệ thì dùng lại.
13. Nếu chưa có thì chọn workspace đầu tiên.
14. Frontend lưu `workspace_id` để dùng cho các request tiếp theo.
15. Người dùng vào màn hình chính của hệ thống.

### Ngoại lệ

- Nếu login fail, backend trả lỗi xác thực.
- Nếu refresh token hết hạn khi app khởi động, frontend xóa session cục bộ.
- Nếu user chưa thuộc workspace nào, frontend không có active workspace để hiển thị dữ liệu monitor.

### Ghi chú kiến trúc

- access token được giữ in-memory để giảm rủi ro XSS hơn so với lưu local storage trực tiếp
- `X-Workspace-Id` trở thành ngữ cảnh bắt buộc cho phần lớn API nghiệp vụ

### Trạng thái hiện tại

- `implemented` cho login và refresh
- `partial` cho onboarding đầy đủ của user mới

## Flow 2. Tạo endpoint cần giám sát

### Mục tiêu

Cho phép Workspace Admin khai báo một endpoint để hệ thống giám sát tự động.

### Actor

- Workspace Admin
- Frontend
- Endpoint API
- Check Policy data

### Điều kiện đầu vào

- user đã đăng nhập
- user thuộc workspace
- user có quyền admin trong workspace
- đã có hoặc sẽ chọn một `CheckPolicy`

### Luồng chính

1. Workspace Admin mở màn hình `Endpoints`.
2. Frontend tải danh sách endpoint hiện có của workspace.
3. Người dùng chọn tạo endpoint mới.
4. Người dùng nhập các thông tin:
5. tên endpoint
6. URL
7. method
8. environment
9. check type
10. tags
11. trạng thái active/inactive
12. policy áp dụng
13. các dữ liệu bổ sung như header hoặc request body nếu cần
14. Frontend gửi `POST /api/v1/endpoints` kèm `X-Workspace-Id`.
15. Backend kiểm tra quyền admin của user trong workspace.
16. Backend tạo domain object `MonitoredEndpoint`.
17. Backend gắn `workspaceId`, `createdBy`, trạng thái ban đầu và timestamp.
18. Backend lưu endpoint vào database.
19. Backend trả endpoint vừa tạo.
20. Frontend tải lại danh sách endpoint và hiển thị kết quả.

### Ngoại lệ

- Nếu user không có quyền admin trong workspace, request bị từ chối.
- Nếu policy tham chiếu không hợp lệ, backend có thể trả lỗi nghiệp vụ.
- Nếu endpoint được tạo nhưng chưa có policy hợp lệ, scheduler sau đó sẽ bỏ qua endpoint đó.

### Trạng thái hiện tại

- `implemented`

## Flow 3. Scheduler chạy health check định kỳ

### Mục tiêu

Tự động quét các endpoint active để thu thập trạng thái hoạt động.

### Actor

- Scheduler
- MonitorEndpointService
- EndpointRepository
- CheckPolicyRepository
- HealthCheckExecutor
- HealthCheckResultRepository

### Điều kiện đầu vào

- backend đang chạy
- scheduler đang bật
- hệ thống có endpoint active

### Luồng chính

1. `HealthCheckScheduler` được kích hoạt theo `app.scheduler.interval-ms`.
2. Scheduler gọi `MonitorEndpointService.runHealthCheckForAll()`.
3. Service truy vấn toàn bộ endpoint active.
4. Hệ thống lặp qua từng endpoint.
5. Với mỗi endpoint, service kiểm tra endpoint có `policyId` hay không.
6. Nếu không có policy, endpoint bị bỏ qua và ghi log cảnh báo.
7. Nếu có policy, service tải `CheckPolicy`.
8. Service chọn executor phù hợp theo `CheckType`.
9. Executor thực hiện check thực tế.
10. Executor trả về `HealthCheckResult`.
11. Service lưu kết quả vào database.
12. Service cập nhật trạng thái và `lastCheckedAt` cho endpoint.
13. Service phát `EndpointCheckedEvent`.
14. Service tiếp tục sang bước phân tích incident.

### Biến thể theo loại check

#### HTTP check

- tạo HTTP request
- áp dụng timeout
- gửi request
- kiểm tra status code
- kiểm tra expected body nếu có
- kiểm tra regex nếu có
- kiểm tra latency threshold nếu có

#### TCP check

- phân tích host và port từ URL hoặc chuỗi đầu vào
- mở socket tới đích
- đánh giá thành công hoặc timeout/failure
- so sánh latency với threshold nếu có

### Ngoại lệ

- Endpoint inactive sẽ không nằm trong danh sách scan.
- Nếu không có executor hỗ trợ `CheckType`, hệ thống log lỗi và bỏ qua.
- Nếu có exception khi check một endpoint, hệ thống log lỗi và tiếp tục endpoint khác.

### Ghi chú BA/Kiến trúc

- Đây là luồng “trái tim” của hệ thống vì mọi dashboard và incident đều phụ thuộc vào nó.
- Hiện scheduler chạy chu kỳ global, chưa lập lịch riêng theo từng policy.

### Trạng thái hiện tại

- `implemented`

## Flow 4. Mở incident khi endpoint lỗi liên tiếp

### Mục tiêu

Tự động biến chuỗi thất bại kỹ thuật thành một sự cố nghiệp vụ có thể theo dõi được.

### Actor

- MonitorEndpointService
- HealthCheckResultRepository
- IncidentRepository
- IncidentAnalyzer
- ApplicationEventPublisher

### Điều kiện đầu vào

- endpoint vừa có kết quả check mới
- policy có `failureThreshold`

### Luồng chính

1. Sau khi lưu xong `HealthCheckResult`, service lấy 10 kết quả gần nhất của endpoint.
2. Service truy vấn xem endpoint hiện có incident mở hay chưa.
3. Service gọi `IncidentAnalyzer.analyze(...)`.
4. `IncidentAnalyzer` đánh giá:
5. số lần fail liên tiếp
6. trạng thái kết quả mới nhất
7. policy threshold
8. incident mở hiện có
9. Nếu chưa có incident mở và số fail liên tiếp đạt ngưỡng:
10. hệ thống quyết định `OPEN_INCIDENT`
11. service tạo incident mới
12. service lưu incident
13. service phát `IncidentOpenedEvent`

### Ngoại lệ

- Nếu kết quả chưa đủ để đạt threshold thì không mở incident.
- Nếu đã có incident mở thì không mở incident chồng mới cho cùng endpoint trong cùng đợt lỗi.

### Ghi chú nghiệp vụ

- Đây là điểm giúp hệ thống tránh việc mỗi lần check fail đều bị coi là một incident riêng.
- Incident thể hiện “sự cố có ý nghĩa vận hành”, không phải chỉ là một lỗi kỹ thuật đơn lẻ.

### Trạng thái hiện tại

- `implemented`

## Flow 5. Đóng incident khi endpoint phục hồi

### Mục tiêu

Tự động kết thúc sự cố khi endpoint đã hoạt động trở lại.

### Actor

- MonitorEndpointService
- IncidentAnalyzer
- IncidentRepository
- ApplicationEventPublisher

### Điều kiện đầu vào

- endpoint đang có incident mở
- kết quả check mới cho thấy endpoint đã phục hồi

### Luồng chính

1. Service lấy các kết quả gần nhất và incident đang mở.
2. `IncidentAnalyzer` đánh giá trạng thái mới nhất.
3. Nếu quyết định là `CLOSE_INCIDENT`:
4. service gọi `resolve()` trên incident
5. service lưu incident đã cập nhật
6. service phát `IncidentResolvedEvent`

### Ghi chú nghiệp vụ

- Điều này giúp dashboard và lịch sử sự cố phản ánh vòng đời sự cố rõ ràng.
- Khi nhìn từ BA, đây là bước chuyển từ “sự cố đang diễn ra” sang “sự cố đã khắc phục”.

### Trạng thái hiện tại

- `implemented`

## Flow 6. Phản ứng sau incident bằng event listener

### Mục tiêu

Tách phần phát hiện sự cố ra khỏi phần phản ứng cảnh báo.

### Actor

- ApplicationEventPublisher
- IncidentEventListener
- IncidentRepository
- EndpointRepository
- ContactGroupRepository
- NotificationPort

### Luồng mở incident

1. `IncidentOpenedEvent` được phát.
2. `IncidentEventListener` nhận sự kiện.
3. Listener tải lại incident theo `incidentId`.
4. Listener tải endpoint theo `endpointId`.
5. Listener lấy contact group của workspace.
6. Listener gọi `NotificationPort.sendIncidentAlert(...)`.

### Luồng đóng incident

1. `IncidentResolvedEvent` được phát.
2. `IncidentEventListener` nhận sự kiện.
3. Listener tải incident và endpoint.
4. Listener lấy contact group của workspace.
5. Listener gọi `NotificationPort.sendRecoveryAlert(...)`.

### Ghi chú kiến trúc

- Đây là minh họa rất tốt cho `domain event` và `observer pattern`.
- Nó giúp notification không làm phình use case monitor chính.

### Giới hạn hiện tại

- contact group hiện được lấy đơn giản theo workspace
- chưa có routing tinh vi theo endpoint override hay alert rule
- adapter hiện mới log ra console thay vì gửi notification thật

### Trạng thái hiện tại

- `implemented` ở mức nền event-driven
- `partial` ở mức delivery production-grade

## Flow 7. Xem dashboard workspace

### Mục tiêu

Cho phép thành viên workspace nhìn nhanh tình hình vận hành hiện tại.

### Actor

- Workspace Member
- Frontend Dashboard
- Dashboard API
- EndpointRepository
- IncidentRepository
- HealthCheckResultRepository

### Điều kiện đầu vào

- user đã đăng nhập
- user đã chọn workspace hợp lệ

### Luồng chính

1. Người dùng mở dashboard.
2. Frontend gọi `GET /api/v1/dashboard/stats`.
3. Backend kiểm tra user có phải member của workspace không.
4. Backend tổng hợp:
5. tổng số endpoint
6. số endpoint UP
7. số endpoint DOWN
8. số endpoint DEGRADED
9. số incident đang mở
10. danh sách incident active
11. Frontend hiển thị các stat card.
12. Frontend tiếp tục gọi `GET /api/v1/endpoints?page=0&size=10`.
13. Frontend lấy một số endpoint đầu tiên để dựng biểu đồ.
14. Với từng endpoint được chọn, frontend gọi `GET /api/v1/dashboard/endpoints/{endpointId}/latency`.
15. Frontend merge dữ liệu và vẽ biểu đồ độ trễ.
16. Dashboard tự refresh định kỳ theo timer ở frontend.

### Ngoại lệ

- Nếu chưa có workspace active, dashboard hiển thị thông báo yêu cầu chọn workspace.
- Nếu API lỗi, frontend log lỗi và không render đủ dữ liệu.

### Trạng thái hiện tại

- `implemented`

## Flow 8. Quản lý thành viên workspace

### Mục tiêu

Cho phép workspace admin duy trì quyền truy cập của team.

### Actor

- Workspace Admin
- Frontend Members screen
- Workspace API

### Luồng thêm thành viên

1. Admin mở màn hình members.
2. Frontend gọi API lấy danh sách member hiện tại.
3. Admin nhập `userId` và chọn role.
4. Frontend gọi `POST /api/v1/workspaces/{id}/members`.
5. Backend kiểm tra quyền admin trong workspace.
6. Backend thêm member vào workspace.
7. Frontend tải lại danh sách member.

### Luồng xóa thành viên

1. Admin chọn xóa một member.
2. Frontend gọi `DELETE /api/v1/workspaces/{id}/members/{userId}`.
3. Backend kiểm tra quyền.
4. Backend xóa quan hệ membership.
5. Frontend tải lại danh sách.

### Trạng thái hiện tại

- `implemented`

## Gợi ý chuyển sang sơ đồ sequence trong báo cáo

Nếu bạn cần vẽ sequence diagram, nên ưu tiên 4 luồng sau vì chúng vừa có giá trị nghiệp vụ, vừa thể hiện tốt kiến trúc:

1. login và chọn workspace
2. tạo endpoint
3. scheduler health check -> lưu result -> mở/đóng incident
4. incident event -> notification

Đây là 4 flow đủ mạnh để nói cả về:

- actor
- nghiệp vụ
- kiến trúc
- design pattern
- trạng thái hiện tại của hệ thống
