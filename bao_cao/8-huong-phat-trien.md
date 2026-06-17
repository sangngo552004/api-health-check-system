# CHƯƠNG 8: HƯỚNG PHÁT TRIỂN

Từ nền móng kiến trúc vững chắc đã được thiết lập ở phiên bản MVP, để chuyển đổi hệ thống từ cấp độ đồ án lên một nền tảng Giám sát Doanh nghiệp (Enterprise-Grade Monitoring Platform) thực thụ, phục vụ hàng triệu truy vấn mỗi ngày, lộ trình phát triển trong tương lai sẽ tập trung giải quyết các bài toán về Hệ thống Phân tán (Distributed Systems) và Tối ưu hóa Dữ liệu lớn (Big Data Optimization).

## 8.1. Áp dụng Transactional Outbox Pattern cho Hệ thống Cảnh báo

**Vấn đề hiện tại:** Kiến trúc Hướng sự kiện (Event-Driven) sử dụng Domain Event (`IncidentOpenedEvent`) hiện tại chạy trực tiếp trên bộ nhớ RAM. Nếu sau khi lưu sự kiện Mở sự cố vào CSDL (PostgreSQL) thành công, nhưng máy chủ gửi Email (SendGrid) hoặc máy chủ Chat (Slack) bị từ chối dịch vụ (Time-out), toàn bộ thông báo sẽ bị mất vĩnh viễn (Data Loss). Tình huống "Ghi CSDL thành công nhưng Gửi tin thất bại" là bài toán **Dual-Write Problem** kinh điển trong hệ thống phân tán.

**Giải pháp kỹ thuật:** Nâng cấp kiến trúc lên mô hình **Transactional Outbox Pattern**.
- Khi `MonitorEndpointService` quyết định mở sự cố, thay vì bắn Event trực tiếp ra API bên ngoài, hệ thống sẽ chèn một bản ghi sự kiện vào bảng trung gian có tên `outbox_events` nằm trong CÙNG MỘT GIAO DỊCH (Database Transaction) với việc chèn bảng `incidents`. Điều này đảm bảo tính Nguyên tử (Atomicity): Có sự cố là chắc chắn có Event trong CSDL.
- Tiếp theo, một tiến trình chạy ngầm độc lập (Message Relay Worker) sẽ liên tục đọc bảng `outbox_events` và đẩy thông điệp sang một Message Broker (như Apache Kafka hoặc RabbitMQ) hoặc gọi thẳng API của Slack/Email. Cơ chế này đảm bảo thông điệp sẽ được gửi đi theo nguyên tắc "Ít nhất một lần" (At-least-once Delivery).

## 8.2. Kỹ thuật Phân mảnh Bảng (Table Partitioning) & Chính sách Lưu trữ Dữ liệu

**Vấn đề hiện tại:** Nếu hệ thống có 10,000 Endpoint, mỗi Endpoint quét 1 lần/phút, bảng `health_check_results` sẽ phình to thêm khoảng **14.4 triệu bản ghi MỖI NGÀY**. Chỉ sau vài tháng, kích thước bảng sẽ vượt quá sức chịu đựng của RAM, việc tạo Index hay truy vấn vẽ biểu đồ Dashboard sẽ bị trì trệ nghiêm trọng (Performance Degradation).

**Giải pháp kỹ thuật:** 
- Áp dụng kỹ thuật **Range Partitioning** của hệ quản trị PostgreSQL. Bảng `health_check_results` sẽ không còn là một bảng đơn lẻ, mà được chia cắt tự động thành các "Bảng con" (Partitions) theo từng tháng (Ví dụ: `results_2026_05`, `results_2026_06`). Khi Dashboard truy vấn biểu đồ của tháng hiện tại, bộ máy truy vấn (Query Optimizer) chỉ cần quét một phân vùng cực nhỏ, giữ hiệu năng luôn ở mức O(1) về mặt quản lý phân vùng.
- Thiếp lập **Data Retention Policy (Chính sách giữ lại dữ liệu)**: Dữ liệu ping mạng thường chỉ có giá trị phân tích trong khoảng 3 đến 6 tháng. Thay vì chạy lệnh `DELETE` hàng triệu bản ghi cũ (gây thảm họa khóa bảng - Table Lock và phình dung lượng do Dead Tuples), quản trị viên chỉ cần chạy câu lệnh `DROP TABLE results_2025_12`. Hệ điều hành sẽ thu hồi lại dung lượng đĩa cứng ngay lập tức trong tíc tắc.

## 8.3. Triển khai Khóa Phân tán (Distributed Lock) cho Scheduler Đa máy chủ

**Vấn đề hiện tại:** Để chịu tải 100,000 Endpoint, ta buộc phải nhân bản (Scale out) máy chủ Backend từ 1 Instance lên 3 hoặc 5 Instances chạy song song (Load Balancing). Tuy nhiên, vì bộ quét Scheduler được kích hoạt theo bộ đếm thời gian (Cron), cả 3 máy chủ sẽ thức dậy cùng một lúc và "tranh nhau" lấy danh sách 100,000 Endpoint để ping. Kết quả: Một Endpoint sẽ bị hệ thống ping 3 lần cùng một giây, gây tấn công từ chối dịch vụ (DDoS) ngược lại chính hệ thống của khách hàng. Đây là thảm họa **Race Condition**.

**Giải pháp kỹ thuật:**
- Áp dụng cơ chế **Khóa Phân tán (Distributed Lock)** bằng việc sử dụng thư viện **ShedLock** kết hợp với Redis, hoặc sử dụng bảng Lock trong PostgreSQL.
- Trước khi một Thread tại Máy chủ A chuẩn bị ping URL của khách hàng X, nó phải xin phép Redis cấp một cái "Khóa" mang tên `lock_ping_endpoint_X`. Nếu Redis đồng ý, Máy chủ A thực thi ping. Trong lúc đó, Máy chủ B cũng định ping URL X, nhưng khi xin Khóa, Redis từ chối vì Máy chủ A đang giữ. Máy chủ B lập tức bỏ qua và đi ping URL Y.
- Cải tiến này biến động cơ Scheduler đơn lẻ thành một **Cụm Cluster Giám sát Phân tán**, nơi các Node tự động chia tải công việc với nhau một cách thông minh, đưa kiến trúc dự án tiệm cận với độ hoàn hảo của các tập đoàn công nghệ lớn.
