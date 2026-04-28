# Hướng dẫn Clean Architecture cho team

## 1. Clean Architecture là gì

`Clean Architecture` là cách tổ chức code để phần nghiệp vụ cốt lõi của hệ thống không bị trói vào framework, database, HTTP hay UI.

Nói ngắn gọn:

- nghiệp vụ là phần bền vững nhất
- framework và hạ tầng là phần thay đổi nhiều hơn
- phụ thuộc phải hướng từ ngoài vào trong

## 2. Vì sao dự án này nên dùng Clean Architecture

Đề tài này có đủ yếu tố khiến kiến trúc sạch mang lại lợi ích thật:

- Có nhiều loại adapter: REST API, database, scheduler, HTTP client, notification.
- Bài toán sẽ mở rộng dần từ health check HTTP sang incident, alert, dashboard.
- Team nhiều người nên cần ranh giới để tránh code chồng chéo.
- Cần dễ giải thích trong báo cáo đồ án.

## 3. Bốn lớp chính trong dự án

### Domain

Chứa:

- entity
- value object
- domain service
- domain event
- business rule

Không được chứa:

- annotation Spring không cần thiết
- controller
- repository JPA
- gọi HTTP
- logic phụ thuộc database

### Application

Chứa:

- use case
- input/output port
- DTO trao đổi giữa delivery và application
- orchestration của luồng nghiệp vụ

Vai trò:

- điều phối hệ thống phải làm gì
- gọi domain để áp business rule
- gọi port out để làm việc với bên ngoài

### Infrastructure

Chứa:

- triển khai repository
- HTTP client adapter
- scheduler adapter
- notification adapter
- cấu hình framework

Vai trò:

- hiện thực các chi tiết kỹ thuật mà application yêu cầu

### Delivery

Chứa:

- REST controller
- request/response model
- exception handler

Vai trò:

- nhận request
- validate input ở mức API
- gọi use case
- trả response

## 4. Quy tắc phụ thuộc

Quy tắc quan trọng nhất:

- `delivery -> application`
- `infrastructure -> application` hoặc `infrastructure -> domain`
- `application -> domain`
- `domain` không phụ thuộc ngược ra ngoài

Những điều không được làm:

- `domain` import class từ `infrastructure`
- `application` gọi trực tiếp `JpaRepository`
- `delivery` chứa business rule
- `infrastructure` quyết định luồng use case thay cho `application`

## 5. Cách nghĩ khi viết một tính năng mới

Ví dụ tính năng “tạo endpoint giám sát”:

1. Xác định use case ở `application/port/in`
2. Tạo DTO cần thiết ở `application/dto`
3. Cài đặt use case ở `application/usecase`
4. Nếu cần lưu dữ liệu, định nghĩa port ở `application/port/out`
5. Triển khai adapter thật ở `infrastructure/persistence`
6. Tạo controller gọi use case ở `delivery/rest`

Đi từ trong ra ngoài sẽ giúp không bị framework dẫn dắt quá sớm.

## 6. Dấu hiệu code đang đi sai kiến trúc

- Entity domain bắt đầu có annotation JPA dày đặc.
- Controller tự viết logic xác định incident.
- Use case import `RestTemplate`, `WebClient`, `JpaRepository`.
- Adapter infrastructure chứa business rule kiểu “nếu lỗi 3 lần thì mở incident”.
- DTO API bị dùng trực tiếp như domain model.

Nếu thấy một trong các dấu hiệu này, nên dừng lại và tách lại trách nhiệm.

## 7. Convention thực tế cho backend

### Khi nào đặt code vào domain

- Khi logic đó phản ánh quy tắc nghiệp vụ.
- Khi logic đó cần đúng dù chạy qua REST, scheduler hay test đơn vị.

### Khi nào đặt code vào application

- Khi logic đó điều phối nhiều bước.
- Khi logic đó gọi nhiều port hoặc nhiều entity để hoàn thành use case.

### Khi nào đặt code vào infrastructure

- Khi code phụ thuộc công nghệ cụ thể.
- Khi code cần biết Spring, JPA, HTTP library, email provider, cron.

### Khi nào đặt code vào delivery

- Khi code liên quan tới request, response, HTTP status, validation đầu vào API.

## 8. Convention thực tế cho frontend

Frontend không áp `Clean Architecture` cứng như backend, nhưng vẫn nên giữ tinh thần tách trách nhiệm:

- `pages` không ôm hết logic gọi API và render phức tạp.
- `services` chịu trách nhiệm nói chuyện với backend.
- `features` gom logic theo nghiệp vụ màn hình.
- `components` chỉ nên là UI dùng lại được.
- `utils` là hàm thuần, không ôm side effect.

## 9. Cách review code theo tinh thần Clean Architecture

Khi review một PR, nên hỏi:

- Business rule có đang nằm trong đúng lớp không?
- Lớp trong có bị import lớp ngoài không?
- Tên use case có nói rõ ý đồ nghiệp vụ không?
- Adapter có chỉ làm nhiệm vụ kỹ thuật hay đang lẫn nghiệp vụ?
- DTO và domain model có bị trộn vai trò không?

## 10. Cơ chế kiểm tra hiện tại trong repo

Repo đã có kiểm tra tự động local cho backend bằng `ArchUnit`.

Mục đích:

- phát hiện sớm việc vi phạm ranh giới package
- nhắc team giữ đúng cấu trúc ngay từ đầu

Tuy `ArchUnit` không thay thế review kiến trúc, nó là lớp bảo vệ đầu tiên rất hữu ích.
