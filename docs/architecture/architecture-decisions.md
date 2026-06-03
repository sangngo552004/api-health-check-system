# Architecture Decisions

Tài liệu này đóng vai trò như một `decision log` rút gọn cho các quyết định kiến trúc chính của dự án. Nó rất hữu ích khi:

- onboarding đồng đội
- viết báo cáo vì sao chọn hướng thiết kế hiện tại
- review xem một thay đổi mới có đi lệch kiến trúc hay không

## AD-01. Chọn Clean Architecture làm trục chính

### Quyết định

Backend được tổ chức theo `domain`, `application`, `infrastructure`, `delivery`.

### Lý do

- phù hợp mục tiêu môn học về kiến trúc
- giúp business logic không bị trộn với framework
- dễ chỉ ra design pattern và dependency direction
- dễ test use case độc lập hơn

### Hệ quả

- code cần nhiều lớp trung gian hơn so với một CRUD app đơn giản
- mapper, port, adapter sẽ nhiều hơn
- team cần kỷ luật để không phá boundary

## AD-02. Dùng Ports and Adapters thay vì để use case gọi thẳng JPA

### Quyết định

Application chỉ biết repository port, không biết `JpaRepository`.

### Lý do

- giảm coupling với persistence
- giúp use case dễ test
- hỗ trợ thay đổi implementation hạ tầng

### Hệ quả

- cần thêm adapter và mapper ở hạ tầng

## AD-03. Đặt transaction boundary ở `application.usecase`

### Quyết định

Transaction được áp bằng AOP config tại `UseCaseTransactionConfig`, thay vì rải `@Transactional` trực tiếp trong từng use case.

### Lý do

- application là nơi tự nhiên để định nghĩa unit of work nghiệp vụ
- giữ use case sạch hơn về annotation
- áp transaction theo convention tên method

### Hệ quả

- team phải giữ naming method nhất quán như `get*`, `find*`, `list*`
- cần hiểu rằng transaction là concern của application boundary, không phải domain

## AD-04. Tách mapper theo boundary

### Quyết định

- application mapper cho command/DTO <-> domain intent
- infrastructure mapper cho domain <-> JPA entity

### Lý do

- mỗi boundary có một loại representation khác nhau
- tránh trộn REST contract với persistence contract

### Hệ quả

- số lượng mapper tăng
- nhưng boundary rõ và dễ bảo trì hơn

## AD-05. Dùng domain event cho incident flow

### Quyết định

Khi incident được mở hoặc đóng, hệ thống phát event rồi để listener xử lý tiếp notification.

### Lý do

- giảm coupling giữa monitor flow và notification flow
- dễ mở rộng thêm audit log, websocket, metrics sau này

### Hệ quả

- cần cẩn thận về eventual consistency của hành vi sau event
- luồng debug sẽ trải qua nhiều thành phần hơn

## AD-06. Dùng strategy cho health check executor

### Quyết định

Mỗi loại check có implementation riêng của `HealthCheckExecutor`.

### Lý do

- tránh `if-else` khổng lồ
- hỗ trợ mở rộng `CheckType`
- bám tốt nguyên tắc open/closed

### Hệ quả

- cần cơ chế chọn executor đúng ở runtime

## AD-07. Chấp nhận một số phần `partial` để ưu tiên trục kiến trúc

### Quyết định

Một số module như notification thật, incidents screen, register flow hiện chưa hoàn chỉnh, nhưng bộ khung kiến trúc đã được dựng trước.

### Lý do

- mục tiêu học phần ưu tiên thể hiện chất lượng tổ chức hệ thống
- giúp tiếp tục mở rộng sau này mà không phải đập lại nền kiến trúc

### Hệ quả

- tài liệu phải nói rõ phần nào implemented, phần nào partial
- báo cáo nên trình bày trung thực về mức độ hoàn thiện
