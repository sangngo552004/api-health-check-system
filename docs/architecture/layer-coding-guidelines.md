# Layer Coding Guidelines

Tài liệu này là “quy ước viết code theo tầng” dành cho cả nhóm. Mục tiêu là giúp mọi người thêm tính năng mới mà không vô tình làm mờ `Clean Architecture`.

## 1. Khi thêm code mới, hãy tự hỏi trước

1. Logic này là bản chất nghiệp vụ hay chỉ là orchestration?
2. Logic này có phụ thuộc framework không?
3. Logic này có phải chi tiết persistence hay protocol không?
4. Đầu ra của logic này là domain model, DTO hay JPA entity?

Chỉ cần trả lời 4 câu trên, bạn thường sẽ biết code nên đặt ở tầng nào.

## 2. Quy tắc cho `domain`

### Đặt ở đây khi

- logic là bản chất của entity/value object
- rule đúng bất kể ai gọi hệ thống
- không cần biết database, HTTP hay Spring

### Ví dụ phù hợp

- tính trạng thái endpoint từ kết quả check
- default threshold của policy
- phân tích số lần fail liên tiếp
- xác định severity của incident

### Không đặt ở đây

- repository call
- query database
- đọc header request
- access control dựa trên authenticated principal

## 3. Quy tắc cho `application`

### Đặt ở đây khi

- bạn đang thực hiện một use case hoàn chỉnh
- cần phối hợp nhiều port/dependency
- cần validate ngữ cảnh thao tác
- cần trả DTO cho delivery

### Ví dụ phù hợp

- tạo endpoint
- update policy
- lấy dashboard stats
- chạy health check cho tất cả endpoint

### Không đặt ở đây

- `JpaRepository`
- entity persistence
- `HttpServletRequest`
- logic render response JSON

## 4. Quy tắc cho `infrastructure`

### Đặt ở đây khi

- bạn đang triển khai port
- bạn đang tích hợp framework, network, DB, scheduler, security
- bạn đang map domain với entity persistence
- bạn đang cấu hình transaction, bean wiring, adapter

### Ví dụ phù hợp

- `EndpointRepositoryAdapter`
- `HttpHealthCheckExecutor`
- `TcpHealthCheckExecutor`
- `UseCaseTransactionConfig`
- `SecurityConfig`

### Không đặt ở đây

- quyết định nghiệp vụ lõi thay cho domain
- orchestrate toàn bộ use case chỉ vì tiện dùng framework

## 5. Quy tắc cho `delivery`

### Đặt ở đây khi

- bạn cần định nghĩa endpoint API
- nhận input HTTP
- validate request
- gọi input port
- translate exception sang response

### Không đặt ở đây

- query trực tiếp database
- nhét logic monitor
- tính toán incident threshold

## 6. Quy tắc cho mapper

### Application mapper

Dùng khi:

- map request command -> domain
- map domain -> response DTO

### Infrastructure mapper

Dùng khi:

- map domain <-> JPA entity

### Không nên

- cho DTO mapper biết JPA entity
- cho persistence mapper biết HTTP request/response DTO

## 7. Quy tắc cho transaction

- transaction boundary mặc định ở `application.usecase`
- không thêm `@Transactional` bừa bãi ở controller
- không kéo transaction concern vào domain
- nếu cần ngoại lệ đặc biệt, phải ghi rõ lý do trong code review hoặc decision doc

## 8. Quy tắc cho exception

- lỗi nghiệp vụ và lỗi use case nên đi qua application exception phù hợp
- controller không nên tự dựng business rule error nếu use case có thể làm việc đó
- domain không nên biết HTTP status code

## 9. Quy tắc cho test

### Domain test

- tập trung vào business rule thuần

### Use case test

- mock port
- test orchestration và error path

### Integration test

- kiểm tra adapter persistence/executor/config hoạt động đúng với framework thật

### Architecture test

- bảo vệ dependency direction giữa các layer

## 10. Dấu hiệu bạn đang đặt code sai tầng

- use case import `JpaRepository`
- controller thao tác quá nhiều bước nghiệp vụ
- domain import class của Spring Security hoặc JPA
- mapper vừa biết DTO vừa biết JPA entity vừa biết domain
- infrastructure tự quyết định rule nghiệp vụ đáng lẽ thuộc domain

## 11. Checklist trước khi merge tính năng mới

- logic nghiệp vụ đã đặt đúng tầng chưa
- transaction boundary có còn ở use case không
- mapper có đang đúng boundary không
- use case có đang phụ thuộc abstraction thay vì implementation không
- tài liệu kiến trúc có cần cập nhật không
