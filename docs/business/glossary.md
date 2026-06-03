# Glossary

Tài liệu này giúp thống nhất thuật ngữ để cả nhóm nói cùng một ngôn ngữ khi đọc code, viết báo cáo hoặc trình bày.

## Thuật ngữ nghiệp vụ

### Workspace

Không gian làm việc logic để tách dữ liệu và quyền truy cập giữa các team hoặc nhóm dự án.

### Workspace Member

Một người dùng thuộc về một workspace với một vai trò cụ thể trong workspace đó.

### Endpoint

Đối tượng cần được hệ thống giám sát. Có thể là HTTP endpoint hoặc đích TCP.

### Check Policy

Bộ quy tắc xác định cách health check được thực hiện và khi nào hệ thống coi kết quả là không ổn.

### Alert Rule

Quy tắc liên quan đến cảnh báo. Trong dự án hiện tại, đây là một phần cấu hình nghiệp vụ đã có CRUD nhưng runtime behavior vẫn đang được hoàn thiện thêm.

### Contact Group

Nhóm người nhận cảnh báo, có thể gồm user nội bộ, email hoặc webhook.

### Health Check Result

Bản ghi kết quả của một lần kiểm tra endpoint.

### Incident

Sự cố nghiệp vụ đại diện cho tình trạng lỗi có ý nghĩa vận hành, không chỉ là một lần check fail đơn lẻ.

### Dashboard

Màn hình tổng quan cho biết sức khỏe hiện tại của workspace.

## Thuật ngữ trạng thái

### UP

Endpoint hoạt động bình thường.

### DOWN

Endpoint không thể đáp ứng đúng điều kiện kiểm tra, ví dụ lỗi kết nối hoặc sai status code nghiêm trọng.

### DEGRADED

Endpoint vẫn phản hồi nhưng chất lượng chưa tốt, ví dụ phản hồi quá chậm theo ngưỡng policy.

### OPEN Incident

Incident đang còn hiệu lực, nghĩa là sự cố chưa được coi là phục hồi.

### RESOLVED Incident

Incident đã được đóng vì endpoint đã phục hồi.

## Thuật ngữ kiến trúc

### Clean Architecture

Cách tổ chức hệ thống sao cho business logic nằm ở trung tâm, còn framework và công nghệ cụ thể nằm ở vòng ngoài.

### Ports and Adapters

Tư duy thiết kế trong đó core system giao tiếp với bên ngoài qua các cổng trừu tượng và adapter cụ thể.

### Use Case

Một đơn vị hành vi ứng dụng, mô tả hệ thống làm gì để phục vụ một nhu cầu nghiệp vụ.

### Domain Event

Sự kiện biểu diễn một việc đã xảy ra trong domain, ví dụ incident được mở hoặc được đóng.

### Repository

Abstraction cho việc truy cập và lưu dữ liệu domain.

### Adapter

Lớp cầu nối giữa core application và công nghệ cụ thể như database, HTTP client, notification delivery.

### Strategy

Pattern cho phép thay nhiều thuật toán hoặc cách xử lý dưới cùng một interface. Trong dự án này nó xuất hiện ở `HealthCheckExecutor`.

## Thuật ngữ kỹ thuật

### Access Token

JWT ngắn hạn dùng để gọi API đã bảo vệ.

### Refresh Token

Token dài hạn hơn dùng để xin access token mới.

### `X-Workspace-Id`

Header dùng để xác định ngữ cảnh workspace cho request hiện tại.

### OpenAPI

Đặc tả API dạng machine-readable dùng để mô tả endpoint, request và response.

### Actuator

Module Spring Boot dùng để expose health, metrics và thông tin vận hành.
