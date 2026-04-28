# Giai đoạn 2 - Quyết định kỹ thuật

## 1. Nguyên tắc chọn công nghệ

Công nghệ ở phase 2 không được chọn theo tiêu chí “quen tay” hoặc “phổ biến là đủ”, mà phải hỗ trợ trực tiếp cho bài toán của đề tài:

- Hệ thống có nhiều thành phần phải tách trách nhiệm rõ.
- Có lịch chạy định kỳ, lưu lịch sử dài hạn và truy vấn tổng hợp.
- Cần dễ demo, dễ mở rộng và dễ giải thích trong báo cáo đồ án.
- Team có nhiều thành viên nên cần stack ít ma sát khi onboarding.

## 2. Backend: Java 17 + Spring Boot + Maven

### Vì sao chọn Java 17

- `Java 17` là bản LTS, ổn định cho đồ án kéo dài nhiều tuần.
- Phù hợp với phần lớn tài liệu, thư viện và ví dụ về `Spring Boot`.
- Hỗ trợ record, sealed type, switch expression và nhiều cải tiến giúp viết domain rõ hơn.
- Chốt target ở `Java 17` giúp tránh phụ thuộc vào tính năng quá mới của JDK, giảm rủi ro lệch máy giữa các thành viên.

### Vì sao chọn Spring Boot

- Bài toán cần nhiều tích hợp hạ tầng như REST API, validation, scheduling, persistence, actuator.
- `Spring Boot` cung cấp auto-configuration giúp dựng nền nhanh nhưng vẫn cho phép team kiểm soát thiết kế lớp nghiệp vụ.
- Hệ sinh thái trưởng thành, dễ tìm tài liệu và dễ giải thích trong báo cáo.
- Khi cần mở rộng sang scheduler, OpenAPI, security hoặc metrics, `Spring Boot` có sẵn đường nâng cấp tự nhiên.

### Vì sao chọn Maven

- `Maven` có lifecycle chuẩn và khá nhất quán giữa các máy.
- Phù hợp cho team đang cần quy trình build/test dễ hiểu.
- Dễ tích hợp với lệnh kiểm tra local trong Git hook như `mvn test`.
- Phù hợp với cách tổ chức đồ án mà mỗi thành viên không nhất thiết phải quá rành build script tùy biến.

## 3. Persistence: PostgreSQL + Flyway

### Vì sao chọn PostgreSQL

- Dữ liệu của đề tài có quan hệ khá rõ: endpoint, policy, result, incident, notification.
- `PostgreSQL` mạnh ở tính toàn vẹn dữ liệu, transaction và khả năng query tổng hợp theo thời gian.
- Phù hợp với dashboard cần thống kê lịch sử, latency, incident đang mở.
- Dễ chạy cục bộ bằng Docker và dễ chuyển sang môi trường demo.

### Vì sao chọn Flyway

- Đề tài có nhiều bảng và sẽ thay đổi dần theo từng giai đoạn.
- `Flyway` giúp quản lý phiên bản schema rõ ràng, tránh tình trạng mỗi máy một cấu trúc DB.
- Phù hợp với team vì lịch sử migration chính là lịch sử tiến hóa dữ liệu của dự án.

## 4. Frontend: React + Vite + TypeScript

### Vì sao chọn React

- Dashboard là dạng giao diện có nhiều khu vực độc lập như widget, bảng, biểu đồ, filter, form.
- `React` phù hợp để chia UI thành component nhỏ và tái sử dụng.
- Khi dự án lớn hơn, nhóm `features` và `components` vẫn giữ được cấu trúc rõ ràng.
- Dễ tuyển tài liệu và dễ onboarding cho thành viên mới.

### Vì sao chọn Vite

- Phase 2 cần tốc độ khởi động nhanh để team vào code sớm.
- `Vite` cho vòng lặp dev rất nhanh, phù hợp khi giao diện cần chỉnh nhiều.
- Cấu hình ban đầu gọn hơn so với nhiều giải pháp nặng hơn.

### Vì sao chọn TypeScript

- Frontend sẽ phải làm việc với nhiều model như endpoint, health result, incident, dashboard summary.
- `TypeScript` giúp giảm lỗi do sai shape dữ liệu khi nối với backend.
- Rất phù hợp cho team nhiều người vì type đóng vai trò như tài liệu sống.

## 5. Docker Compose cho môi trường local

- Team cần một cách chạy DB đồng nhất.
- `Docker Compose` giảm lệ thuộc vào cài đặt tay trên từng máy.
- Dễ reset dữ liệu khi demo hoặc kiểm thử cục bộ.
- Đủ nhẹ cho phase 2 mà chưa cần kéo thêm hạ tầng phức tạp.

## 6. Cấu trúc backend được chọn vì sao hợp lý

Chia `domain`, `application`, `infrastructure`, `delivery` không phải để “đẹp thư mục”, mà để ép ranh giới phụ thuộc.

- `domain` tập trung vào quy tắc nghiệp vụ, không biết Spring hay database là gì.
- `application` mô tả hệ thống phải làm gì thông qua use case và port.
- `infrastructure` là nơi xử lý chuyện kỹ thuật cụ thể như JPA, HTTP client, scheduler.
- `delivery` chịu trách nhiệm nói chuyện với thế giới bên ngoài qua HTTP.

Lợi ích trực tiếp:

- Dễ kiểm thử use case mà không cần chạy toàn bộ framework.
- Dễ thay adapter nhưng ít đụng vào nghiệp vụ.
- Dễ review vì biết mỗi loại logic phải nằm ở đâu.

## 7. Cấu trúc frontend được chọn vì sao hợp lý

Frontend được chia theo hai trục:

- trục application-level: `app`, `pages`
- trục reusable/business-level: `components`, `features`, `services`, `hooks`, `types`, `utils`

Lợi ích:

- Tránh để mọi thứ dồn vào `components`.
- Tách phần dùng lại được với phần gắn chặt vào một nghiệp vụ.
- Dễ scale từ một dashboard đơn giản sang nhiều màn hình hơn.

## 8. Kiểm tra chất lượng code local

### Backend

- Dùng `JUnit` để kiểm tra ứng dụng khởi động được.
- Dùng `ArchUnit` để kiểm tra code có vi phạm ranh giới `Clean Architecture` hay không.
- Chạy qua `mvn test`.

### Frontend

- Dùng `TypeScript` strict mode để bắt lỗi kiểu dữ liệu.
- Dùng `ESLint` để kiểm tra convention và code smell phổ biến.
- Chạy qua `npm run lint` và `npm run build`.

### Tích hợp vào Git hook

- `pre-commit`: chạy lint frontend và test backend.
- `pre-push`: chạy build frontend và test backend.

Mục tiêu là chặn lỗi sớm ngay trên máy local, trước khi code được đẩy lên remote.
