# Architecture and Design Patterns

Tài liệu này được viết riêng để phục vụ mục tiêu học phần về `kiến trúc phần mềm` và `design pattern`. Nội dung cố gắng gắn chặt với code hiện tại thay vì nói lý thuyết chung chung.

## 1. Tại sao dự án này phù hợp để học kiến trúc

Dự án có đủ nhiều loại concern để minh họa kiến trúc một cách thực tế:

- business logic thuần
- orchestration use case
- persistence
- REST API
- background scheduler
- security
- event-driven communication
- frontend tiêu thụ API

Nó cũng không quá lớn tới mức mất kiểm soát, nên rất phù hợp để phân tích cách tách lớp và áp dụng pattern.

## 2. Kiến trúc tổng thể đang theo hướng gì

Backend đang theo hướng:

- `Clean Architecture`
- kết hợp tư duy `Hexagonal Architecture`
- thực thi bằng `ports and adapters`

Điều này thể hiện qua việc:

- `domain` không phụ thuộc Spring
- `application` định nghĩa use case và port
- `infrastructure` triển khai các port bằng công nghệ cụ thể
- `delivery` là lớp giao tiếp với bên ngoài

## 3. Tư duy thiết kế cốt lõi

### 3.1. Business logic ở trung tâm

Các rule như:

- xác định trạng thái endpoint
- khi nào mở incident
- khi nào đóng incident
- ngưỡng failure threshold

được đặt gần `domain` và `application`, không nhét trực tiếp vào controller hay repository.

### 3.2. Dependency inversion

Application không phụ thuộc trực tiếp vào:

- JPA
- HTTP client cụ thể
- notification adapter cụ thể

Thay vào đó, application phụ thuộc vào abstraction như:

- `EndpointRepository`
- `CheckPolicyRepository`
- `HealthCheckExecutor`
- `NotificationPort`

Đây là điểm rất đáng nhấn mạnh trong báo cáo vì nó chính là tinh thần `D` trong `SOLID`.

### 3.3. Boundary rõ giữa use case và framework

Controller chỉ làm nhiệm vụ:

- nhận request
- validate
- gọi use case
- trả response

Nó không tự mang business rule nặng.

## 4. Pattern đang hiện diện trong code

### 4.1. Repository Pattern

Biểu hiện:

- application định nghĩa các interface repository
- infrastructure có adapter triển khai dựa trên JPA

Ví dụ:

- `EndpointRepository`
- `IncidentRepository`
- `WorkspaceRepository`

Giá trị:

- tách business logic khỏi persistence
- test use case dễ hơn
- có thể thay đổi persistence strategy mà ít ảnh hưởng lớp trên

### 4.2. Strategy Pattern

Biểu hiện rõ nhất ở cơ chế health check executor.

Các class như:

- `HttpHealthCheckExecutor`
- `TcpHealthCheckExecutor`

đều implement cùng một abstraction là `HealthCheckExecutor`, và runtime chọn executor phù hợp bằng `supports(CheckType)`.

Ý nghĩa:

- thêm loại check mới như `PING`, `DNS`, `TLS` sẽ dễ hơn
- không cần `if-else` khổng lồ trong một class duy nhất
- phù hợp với nguyên tắc `Open/Closed`

### 4.3. Factory-like Selection

Hiện chưa có một `factory` tách riêng bằng tên class, nhưng `MonitorEndpointService` đang thực hiện hành vi chọn executor từ `List<HealthCheckExecutor>`.

Có thể gọi đây là:

- `strategy selection`
- hoặc một dạng `lightweight factory behavior`

Đây là điểm hay để phân tích trong báo cáo:

- pattern không nhất thiết luôn phải xuất hiện dưới đúng tên lớp truyền thống
- đôi khi cấu trúc Spring DI + list injection đã hiện thực hóa ý tưởng của pattern

### 4.4. Domain Event Pattern

Biểu hiện:

- `EndpointCheckedEvent`
- `IncidentOpenedEvent`
- `IncidentResolvedEvent`

Sau khi use case hoàn tất tác vụ chính, hệ thống phát event để các hành vi phụ có thể phản ứng.

Giá trị:

- giảm coupling trực tiếp giữa monitoring và notification
- giúp luồng xử lý mở rộng dễ hơn
- phù hợp cho future enhancement như audit log, websocket, metrics export

### 4.5. Observer Pattern

Trong ngữ cảnh Spring event, `@EventListener` đóng vai trò rất gần với `Observer`.

`IncidentEventListener` đang lắng nghe sự kiện và phản ứng bằng cách gửi notification.

Điểm đáng phân tích:

- event publisher không cần biết ai là consumer
- listener có thể thêm bớt mà không sửa use case gốc nhiều

### 4.6. Adapter Pattern

Hầu như toàn bộ `infrastructure` đều là adapter.

Ví dụ:

- JPA adapter chuyển từ port repository sang database implementation
- `AuthenticationAdapter` nối application với security/auth provider
- `LogNotificationAdapter` nối `NotificationPort` với cơ chế log cụ thể

Giá trị:

- giảm ràng buộc giữa core và framework
- minh họa rất rõ `Hexagonal Architecture`

### 4.7. Facade-like Use Case Layer

Các service như:

- `ManageEndpointService`
- `ManageWorkspaceService`
- `MonitorEndpointService`
- `GetDashboardStatsService`

đóng vai trò như điểm vào tập trung cho một nhóm hành vi ứng dụng.

Chúng không phải `Facade` theo textbook 100%, nhưng có tính chất:

- che giấu độ phức tạp của nhiều dependency phía sau
- cung cấp entry point rõ ràng cho controller

### 4.8. DTO + Mapper Pattern

Dự án dùng DTO tách khỏi domain model và mapper để chuyển đổi:

- domain -> response DTO
- request command -> domain intent

Điều này giúp:

- tránh lộ domain model thẳng ra API
- giảm phụ thuộc giữa API contract và core model
- kiểm soát dữ liệu trả về tốt hơn

## 5. SOLID thể hiện ở đâu

### S - Single Responsibility Principle

Ví dụ:

- controller lo HTTP contract
- use case lo orchestration
- executor lo check endpoint
- repository adapter lo persistence
- event listener lo reaction sau sự kiện

### O - Open/Closed Principle

Ví dụ:

- thêm `HealthCheckExecutor` mới mà ít cần sửa lớp cũ
- thêm event listener mới cho incident mà không cần đổi luồng monitor chính

### L - Liskov Substitution Principle

Các implementation của `HealthCheckExecutor` phải có thể thay thế nhau dưới cùng contract.

### I - Interface Segregation Principle

Application dùng các port nhỏ, có chủ đích, thay vì một interface quá lớn cho toàn bộ infrastructure.

### D - Dependency Inversion Principle

Đây là điểm thể hiện mạnh nhất:

- use case phụ thuộc abstraction
- adapter phụ thuộc framework
- framework được đẩy ra rìa kiến trúc

## 6. Kiến trúc này mang lại lợi ích gì cho môn học

### 6.1. Dễ chỉ ra boundary

Bạn có thể chỉ ra rất rõ:

- lớp nào là domain
- lớp nào là application
- lớp nào là infrastructure
- dependency nào được phép đi theo chiều nào

### 6.2. Dễ minh họa pattern bằng code thật

Thay vì chỉ nêu lý thuyết, bạn có thể trích được ví dụ cụ thể ngay trong repo:

- strategy ở health check executor
- observer/domain event ở incident flow
- repository ở persistence layer
- adapter ở infrastructure

### 6.3. Dễ nói về trade-off

Không phải pattern nào cũng được triển khai “chuẩn sách giáo khoa” tuyệt đối, nhưng đó lại là điều tốt cho báo cáo vì bạn có thể phân tích:

- tại sao chọn mức độ đơn giản hiện tại
- điểm nào đáng refactor tiếp nếu làm production

## 7. Điểm mạnh của thiết kế hiện tại

- ranh giới kiến trúc khá rõ
- business logic không bị dồn vào controller
- có khả năng mở rộng thêm loại check
- có event-driven hook cho cảnh báo
- có test kiến trúc bằng ArchUnit
- có OpenAPI sync test giúp contract minh bạch

## 8. Điểm còn yếu hoặc đáng cải thiện

### 8.1. Scheduler chưa phản ánh đầy đủ policy

Hiện scheduler chạy theo chu kỳ global, trong khi `CheckPolicy` có `intervalSeconds`.

Điều này cho thấy:

- domain đã nghĩ tới bài toán
- nhưng implementation runtime chưa tận dụng hết thiết kế

### 8.2. Alert rule chưa thực sự đi sâu vào runtime decision

CRUD alert rule đã có, nhưng cơ chế runtime cảnh báo vẫn còn giản lược.

Đây là ví dụ tốt về khoảng cách giữa:

- `data model completeness`
- và `behavioral completeness`

### 8.3. Notification adapter mới ở mức demo

`LogNotificationAdapter` giúp chứng minh boundary và event flow, nhưng chưa chứng minh delivery production-grade.

### 8.4. Incident module chưa có API riêng đầy đủ

Dashboard đã hiển thị incident summary, nhưng incident chưa phải một bounded feature hoàn chỉnh ở tầng delivery/frontend.

## 9. Nếu cần trình bày trong báo cáo

Bạn có thể dùng cấu trúc sau:

1. Bài toán cần giám sát endpoint theo workspace
2. Lý do chọn Clean Architecture
3. Cách tách domain, application, infrastructure, delivery
4. Design pattern đang dùng
5. Minh họa một luồng tiêu biểu như monitoring -> incident -> notification
6. Đánh giá ưu điểm và hạn chế
7. Hướng mở rộng cho production-grade system

## 10. Kết luận

Dự án này không chỉ là một CRUD app. Giá trị lớn của nó trong môn học nằm ở chỗ:

- có business flow nền chạy tự động
- có phân tầng kiến trúc khá rõ
- có nhiều pattern xuất hiện một cách tự nhiên trong code
- có thể phân tích cả phần đã hoàn thiện lẫn phần còn khoảng trống

Đó là chất liệu rất tốt để làm báo cáo kiến trúc và design pattern một cách thuyết phục.
