# Project Progress Checklist

Tài liệu này được viết để giải quyết đúng vấn đề rất thường gặp khi quay lại dự án sau một thời gian: `mình đã làm tới đâu rồi`, `cái gì xong thật`, `cái gì đang dở`, `nên code tiếp cái gì trước`.

Khác với [project-status.md](project-status.md), file này mang tính `checklist thực chiến` hơn:

- có thể tick khi hoàn thành
- có thể dùng làm daily/weekly progress tracker
- có thể làm backlog cá nhân hoặc backlog nhóm
- có thể dùng như “resume point” mỗi lần quay lại code

## 1. Cách dùng tài liệu này

### Khi quay lại code sau một thời gian

1. Đọc nhanh `Current Resume Point`
2. Xem `In Progress / Partial`
3. Chọn một mục trong `Next Priority`
4. Sau khi code xong, cập nhật lại checklist

### Khi làm việc theo sprint cá nhân

- chuyển các mục `Next Priority` thành task
- ghi ngày bắt đầu, ngày hoàn thành nếu cần
- ưu tiên hoàn thành từ trên xuống dưới

### Trạng thái dùng trong checklist này

- `[x]` hoàn thành đủ để dùng thực tế trong repo hiện tại
- `[-]` đã có một phần, nhưng chưa hoàn chỉnh
- `[ ]` chưa làm
- `[~]` có thể cải tiến nếu còn thời gian

## 2. Current Resume Point

Nếu hôm nay phải quay lại code từ con số 0 về ngữ cảnh, hãy nhớ nhanh các điểm này:

- backend đã có `auth`, `workspace`, `endpoint`, `policy`, `alert`, `contact`, `dashboard`
- frontend đã có dashboard và phần lớn CRUD chính
- scheduler health check nền đã chạy được
- incident open/resolve cơ bản đã hoạt động
- `TCP health check` đã xuất hiện trong code và có test
- điểm còn dở nhất là `incident module hoàn chỉnh`, `notification delivery thật`, `register flow`, `policy interval runtime`

## 3. Checklist theo khối chức năng

## 3.1. Kiến trúc và nền tảng

- [x] Tách tầng `domain`, `application`, `infrastructure`, `delivery`
- [x] Dùng `port` và `adapter`
- [x] Có `ArchUnit` test cho layer dependency
- [x] Có transaction boundary ở `application.usecase`
- [x] Có mapper tách riêng cho `application` và `infrastructure`
- [~] Bổ sung thêm nhiều rule ArchUnit hơn để cấm import sai tầng chi tiết hơn
- [~] Viết thêm Mermaid diagram cho dependency direction

## 3.2. Authentication và security

- [x] Login API
- [x] Refresh token API
- [x] JWT filter và security config
- [x] Role global `ADMIN`
- [x] Kiểm tra quyền theo workspace qua `@PreAuthorize`
- [-] Frontend register screen có UI
- [ ] Backend register API
- [~] Hoàn thiện session hardening và refresh strategy chủ động hơn ở frontend

## 3.3. Workspace và membership

- [x] Tạo workspace
- [x] Xóa workspace
- [x] Lấy workspace theo id
- [x] Lấy danh sách workspace của user
- [x] Thêm thành viên vào workspace
- [x] Xóa thành viên khỏi workspace
- [x] Xem danh sách thành viên
- [~] Cải thiện onboarding thành viên theo email hoặc username thay vì nhập `userId` thô

## 3.4. Endpoint management

- [x] Tạo endpoint
- [x] Cập nhật endpoint
- [x] Xóa endpoint
- [x] Xem chi tiết endpoint
- [x] Xem danh sách endpoint theo workspace
- [x] Frontend CRUD endpoint đã nối API
- [~] Bổ sung filtering/search/pagination thực sự tối ưu hơn thay vì tải nhiều item cho UI

## 3.5. Check policy management

- [x] CRUD policy backend
- [x] CRUD policy frontend
- [x] Domain rule cho timeout / threshold default
- [-] `intervalSeconds` có trong model
- [ ] `intervalSeconds` được dùng đúng nghĩa để lập lịch riêng theo policy
- [~] Bổ sung validate business rule chặt hơn cho giá trị policy

## 3.6. Alert rule management

- [x] CRUD alert rule backend
- [x] CRUD alert rule frontend
- [-] Alert rule tồn tại ở mức data model và UI
- [ ] Alert rule tham gia sâu hơn vào runtime notification decision
- [~] Thêm tài liệu rule matrix cho từng loại alert

## 3.7. Contact group management

- [x] CRUD contact group backend
- [x] CRUD contact group frontend
- [-] Contact group đang được lấy theo workspace trong incident event flow
- [ ] Routing contact group tinh vi theo endpoint hoặc alert rule override
- [~] Thêm validate format mạnh hơn cho email/webhook

## 3.8. Monitoring engine

- [x] Scheduler health check chạy định kỳ
- [x] Quét endpoint active
- [x] HTTP health check executor
- [x] TCP health check executor
- [x] Lưu `HealthCheckResult`
- [x] Cập nhật trạng thái endpoint sau mỗi lần check
- [-] Scheduler đang chạy theo interval global
- [ ] Scheduler tôn trọng `CheckPolicy.intervalSeconds`
- [~] Retry/backoff/circuit breaker cho check execution

## 3.9. Incident management

- [x] Mở incident khi fail liên tiếp tới ngưỡng
- [x] Đóng incident khi phục hồi
- [x] Có event `IncidentOpenedEvent` và `IncidentResolvedEvent`
- [x] Dashboard trả incident summary
- [ ] API riêng cho incident list/detail/filter
- [ ] Frontend incidents screen thật
- [~] Thêm incident timeline hoặc audit trail

## 3.10. Notification

- [x] Có `NotificationPort`
- [x] Có `LogNotificationAdapter`
- [x] Có `IncidentEventListener`
- [ ] Email notification adapter thật
- [ ] Webhook notification adapter thật
- [ ] Retry / delivery status / failure handling
- [~] Outbox pattern cho notification nếu còn tiến độ

## 3.11. Dashboard và reporting

- [x] Dashboard stats API
- [x] Dashboard latency history API
- [x] Frontend dashboard overview
- [x] Stat cards
- [x] Latency chart
- [x] Active incidents board
- [ ] Incident reporting đầy đủ
- [ ] SLA / uptime reporting
- [~] Dashboard caching khi dữ liệu tăng

## 3.12. Testing và quality

- [x] Context load test
- [x] Swagger/OpenAPI sync test
- [x] Clean architecture test
- [x] Use case tests cho nhiều module chính
- [x] Persistence adapter integration tests
- [x] TCP executor tests
- [x] CI backend/frontend
- [ ] Frontend automated tests
- [~] Test cho `MonitorEndpointService` ở mức sâu hơn nếu chưa đủ

## 4. Next Priority

Nếu mục tiêu là hoàn thiện MVP hợp lý nhất, nên đi theo thứ tự:

1. [ ] Hoàn thiện `incident module` ở backend và frontend
2. [ ] Hoàn thiện `notification delivery` thật
3. [ ] Hoàn thiện `register/onboarding` hoặc quyết định bỏ route register khỏi UI
4. [ ] Làm cho `CheckPolicy.intervalSeconds` được áp dụng thực sự
5. [ ] Dọn các placeholder/text cũ trong frontend

## 5. Nice-to-have nếu còn thời gian

- [~] Maintenance window
- [~] Alert escalation
- [~] Circuit breaker / retry
- [~] Structured logging
- [~] Redis cache cho dashboard
- [~] Prometheus + Grafana hoàn chỉnh
- [~] Mermaid diagrams đầy đủ trong docs

## 6. Resume Checklist mỗi lần quay lại code

Mỗi lần quay lại dự án, chỉ cần check 8 dòng này:

- [ ] `git status` có những thay đổi gì
- [ ] `docs/project/project-status.md` và file này có còn đúng không
- [ ] feature mình đang làm nằm ở tầng nào
- [ ] API cần đổi không, nếu có thì có cần update OpenAPI không
- [ ] test hiện có đã bao phủ đường đi mới chưa
- [ ] frontend có màn hình tương ứng chưa
- [ ] còn placeholder nào đang đánh lừa người đọc không
- [ ] sau khi xong có cần cập nhật docs kiến trúc / nghiệp vụ không

## 7. Gợi ý cập nhật định kỳ

Bạn có thể dùng khung này mỗi cuối buổi code:

### Hôm nay đã xong

- ...

### Đang dở

- ...

### Buổi tới làm tiếp

- ...

### Nếu còn thời gian sẽ cải tiến

- ...
