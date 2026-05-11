# Phase 4: Thiết kế lại Database Workspace và Kế hoạch Sprint

Tài liệu này ghi nhận sự thay đổi về kiến trúc dữ liệu trong Giai đoạn 4 và định hướng các Sprint tiếp theo để hoàn thiện hệ thống theo chuẩn "Làm việc nhóm" (Team-based).

## 1. Mục tiêu của việc tái thiết kế Database

Khác với các ứng dụng SaaS truyền thống chia tách khách hàng, hệ thống API Health Check của chúng ta sử dụng khái niệm **Workspace** để giải quyết bài toán: **Làm sao để các team (nhóm dự án/nhóm microservice) quản lý độc lập các API của mình mà không bị nhiễu loạn thông tin?**

- **Phân lập tài nguyên (Resource Isolation):** Team A (VD: Payment Team) sẽ có "Payment Workspace", chỉ thấy và quản lý các API, Alert Rule, và Contact Group của riêng team mình. Không bị lẫn lộn hàng trăm API của các team khác.
- **Cảnh báo chính xác (Targeted Alerting):** Khi API Payment sập, hệ thống tự động tìm các Contact Group cấu hình trong Workspace Payment để gửi cảnh báo. Các team khác không bị nhận email/webhook rác.
- **Phân quyền linh hoạt:** Trong cùng một Workspace, Team Lead có thể là `ADMIN` (được thêm/xóa endpoint), còn các lập trình viên khác là `MEMBER`.

Sự thay đổi này đã được hiện thực hóa qua bản migration `V8__add_workspaces_and_membership.sql`, bổ sung bảng `workspaces`, `workspace_members` và gắn khóa ngoại `workspace_id` vào toàn bộ các thực thể cốt lõi (`endpoints`, `check_policies`, `alert_rules`, `contact_groups`, `health_check_results`, `incidents`).

## 2. Kế hoạch Sprint hoàn thiện Phase 4

Với thiết kế Database chuẩn xác này, chúng ta sẽ triển khai 4 Sprint tiếp theo để hoàn thiện Phase 4:

### Sprint 1: Nền tảng Team & Quản lý API theo nhóm
- Hoàn thiện REST API cho việc tạo/sửa/xóa Workspace và thêm thành viên (`WorkspaceMember`).
- Nâng cấp toàn bộ các API CRUD hiện tại (Endpoint, Policy, ContactGroup, AlertRule) để chúng luôn nhận và xác thực `workspace_id`.
- Triển khai cơ chế bảo mật (Authorization): Đảm bảo User chỉ truy xuất và thao tác được trên các dữ liệu thuộc về Workspace mà họ là thành viên.

### Sprint 2: Core Executor (Trái tim của hệ thống)
- Tích hợp **Spring Scheduler** và thư viện **HTTP/TCP Client**.
- Viết Background Job chạy định kỳ: Quét tất cả các `monitored_endpoints` đang *active* của *tất cả các team*.
- Gọi Health Check (VD: HTTP GET/POST), phân tích kết quả (StatusCode, Response Time) và lưu trữ vào `health_check_results` kèm theo `workspace_id` tương ứng.

### Sprint 3: Incident & Targeted Notification (Cảnh báo thông minh)
- Phát triển bộ phân tích lỗi (Analyzer): Đọc `HealthCheckResult`, nếu phát hiện endpoint liên tục thất bại vượt mức `failureThreshold` của `CheckPolicy`, hệ thống tự động mở một `Incident`.
- Xây dựng Notification Adapter (Email/Webhook).
- Định tuyến cảnh báo: Dựa vào `workspace_id` của Incident, hệ thống truy xuất các `ContactGroup` của team đó và gửi thông báo chính xác tới những người chịu trách nhiệm.

### Sprint 4: Báo cáo & Phân loại Dashboard
- Phát triển **Team Dashboard API**: Cung cấp số liệu thống kê (tổng số API UP/DOWN, biểu đồ phản hồi, các Incident đang mở) **chỉ giới hạn trong phạm vi Workspace** của User đang đăng nhập.
- Phát triển **Global Dashboard API** (Dành cho Super Admin/SRE): Cung cấp cái nhìn tổng quan về sức khỏe toàn bộ hệ thống Microservice của cả tổ chức (truy vấn bỏ qua `workspace_id`).
- Viết Unit Test và Integration Test cho các luồng xử lý chính.
