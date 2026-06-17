# CHƯƠNG 1: MÔ TẢ ĐỀ BÀI

## 1.1. Đặt vấn đề và lý do chọn đề tài

Trong kỷ nguyên chuyển đổi số hiện nay, kiến trúc phần mềm tại các doanh nghiệp (Enterprise) đã dịch chuyển mạnh mẽ từ mô hình nguyên khối (Monolithic) sang mô hình vi dịch vụ (Microservices). Sự phân rã này mang lại tính linh hoạt cao, song cũng kéo theo một thách thức to lớn: hệ thống giờ đây phụ thuộc vào hàng trăm, thậm chí hàng ngàn giao tiếp qua mạng lưới (Network Communication) thông qua các Application Programming Interface (API). Trong một môi trường phân tán như vậy, rủi ro đứt gãy dịch vụ luôn thường trực. Một dịch vụ nhỏ phản hồi chậm (High Latency) hoặc ngừng hoạt động (Downtime) có thể tạo ra hiệu ứng dây chuyền (Cascading Failure), làm sụp đổ toàn bộ quy trình nghiệp vụ lõi, gây tổn thất nghiêm trọng về mặt tài chính cũng như uy tín của doanh nghiệp.

Việc giám sát trạng thái hoạt động của các dịch vụ này bằng phương pháp thủ công hoặc thông qua phản hồi từ khách hàng (Customer Reports) là hoàn toàn bị động và không thể chấp nhận được trong các hệ thống yêu cầu tính sẵn sàng cao (High Availability). Sự cố cần phải được phát hiện ở mức độ mili-giây, ngay khi hệ thống có dấu hiệu suy thoái, trước cả khi người dùng cuối cảm nhận được. Điều này đặt ra yêu cầu cấp thiết về một hệ thống "Health Check" tự động, có khả năng liên tục ping và phân tích phản hồi từ các điểm cuối (Endpoints) 24/7 mà không phụ thuộc vào sự can thiệp của con người.

Tuy nhiên, khi áp dụng các công cụ giám sát mã nguồn mở hoặc thương mại vào thực tiễn doanh nghiệp quy mô lớn, một vấn đề nan giải lập tức nảy sinh: Bài toán đa không gian làm việc (Multi-tenancy / Multi-workspace). Trong một tập đoàn với nhiều trung tâm phát triển (Development Centers) và nhiều dự án khác nhau, nếu sử dụng chung một không gian giám sát phẳng (Flat Monitoring Space), các nhóm sẽ phải đối mặt với tình trạng "nhiễu loạn thông tin". Một kỹ sư của dự án A có thể liên tục nhận được cảnh báo lỗi từ dự án B; cấu hình (Check Policy) của nhóm này có thể vô tình ghi đè lên cấu hình của nhóm khác. Điều này không chỉ vi phạm nguyên tắc đặc quyền tối thiểu (Principle of Least Privilege) mà còn gây rò rỉ dữ liệu hạ tầng nhạy cảm giữa các bộ phận.

Xuất phát từ những trăn trở về mặt kiến trúc và thực tiễn vận hành trên, đề tài **"API Health Check System"** được lựa chọn để nghiên cứu và phát triển. Dự án không đơn thuần là một công cụ ping API, mà là một nền tảng giám sát tập trung, được thiết kế với tư duy cô lập dữ liệu nhiều lớp (Workspace Isolation), quản lý vòng đời sự cố thông minh, và tuân thủ chặt chẽ các nguyên lý của Kiến trúc Sạch (Clean Architecture) nhằm đảm bảo khả năng bảo trì và mở rộng trong tương lai.

## 1.2. Mục tiêu và phạm vi của dự án

### 1.2.1. Các tính năng cốt lõi (In-scope)

Để đảm bảo dự án giải quyết trọn vẹn bài toán đã đặt ra đồng thời duy trì tính khả thi của một Minimum Viable Product (MVP), phạm vi hệ thống được khoanh vùng ở 6 trụ cột nghiệp vụ sau:

- **Quản lý đa không gian (Multi-workspace):** Đây là hạt nhân bảo mật của hệ thống. Dữ liệu được phân vùng logic theo mô hình Shared Database - Shared Schema. Mỗi Workspace đóng vai trò như một hòn đảo biệt lập, sở hữu tập hợp thành viên, endpoint, cấu hình và sự cố riêng. Điều này đảm bảo tính độc lập tuyệt đối giữa các đội nhóm, giải quyết triệt để bài toán nhiễu loạn thông tin trong môi trường Enterprise.
- **Quản lý Endpoint & Policy (Endpoint & Policy Management):** Cho phép người dùng linh hoạt định nghĩa các mục tiêu giám sát (Monitored Endpoints) thông qua nhiều giao thức (HTTP/HTTPS, TCP). Đi kèm với đó là các Chính sách kiểm tra (Check Policies) cung cấp khả năng tinh chỉnh chuyên sâu: từ chu kỳ quét (interval), ngưỡng thời gian chờ (timeout), cho đến quy định về ngưỡng chịu lỗi (failure threshold). Tính năng này giúp hệ thống thích ứng với các mức Cam kết chất lượng dịch vụ (Service Level Objectives - SLO) đa dạng của từng API.
- **Động cơ giám sát tự động (Automated Monitoring Engine):** Xây dựng một bộ lập lịch (Scheduler) chạy ngầm không gián đoạn ở backend. Động cơ này tự động quét qua các endpoint đang ở trạng thái kích hoạt, thực thi các thuật toán kiểm tra và ghi nhận chính xác trạng thái phản hồi cùng độ trễ. Thiết kế này tách biệt hoàn toàn tải trọng của việc giám sát khỏi luồng phục vụ API tương tác với người dùng (User API threads).
- **Quản lý Vòng đời sự cố (Incident Lifecycle Management):** Hệ thống không cảnh báo mù quáng ngay lần lỗi đầu tiên. Thay vào đó, một bộ phân tích sự cố (Incident Analyzer) sẽ đánh giá chuỗi lịch sử phản hồi để phân biệt giữa hiện tượng chập chờn mạng tạm thời (Network Glitch) và một sự cố sụp đổ hệ thống thực sự. Sự cố (Incident) chỉ được MỞ khi chuỗi lỗi chạm ngưỡng cấu hình, và tự động ĐÓNG khi dịch vụ phục hồi, tạo ra một bức tranh vận hành chính xác và giảm thiểu mệt mỏi cảnh báo (Alert Fatigue).
- **Cảnh báo dựa trên sự kiện (Event-driven Alerting):** Áp dụng mẫu thiết kế Domain Event để phân tách hoàn toàn quy trình xử lý lõi (Ping & Phân tích) khỏi quy trình thông báo. Khi một sự cố thay đổi trạng thái, hệ thống chỉ phát ra một sự kiện vào bộ nhớ. Các tiến trình quan sát (Observers) sẽ bắt lấy sự kiện này và điều phối luồng cảnh báo tới các nhóm liên lạc, đảm bảo hiệu năng của động cơ giám sát không bị ảnh hưởng bởi độ trễ của các dịch vụ gửi tin nhắn ngoại vi.
- **Bảng điều khiển tương tác (Interactive Dashboard):** Cung cấp một giao diện trực quan tại Frontend để tổng hợp tình trạng sức khỏe tổng thể. Hệ thống hiển thị số liệu thống kê thời gian thực, danh sách các sự cố đang diễn ra và biểu đồ độ trễ, cung cấp cho các kỹ sư bức tranh toàn cảnh về độ ổn định của hệ thống ngay khi họ đăng nhập.

### 1.2.2. Các phần nằm ngoài phạm vi (Out-of-scope)

Trong khuôn khổ của một đồ án tập trung vào thiết kế kiến trúc phần mềm, một số vấn đề mang tính chất vận hành hạ tầng (Infrastructure Operations) được chủ động loại bỏ khỏi phiên bản MVP. Dưới đây là giải trình khoa học cho các quyết định này:

- **Bộ lập lịch phân tán (Distributed Lock / Multi-node Scheduler):** Ở phiên bản hiện tại, động cơ quét chạy trên một Single JVM Instance. Việc triển khai phân tán đòi hỏi phải giải quyết bài toán đồng thuận (Consensus) bằng cơ chế khóa phân tán (như Redis Redisson hoặc ShedLock) để tránh hiện tượng Race Condition (nhiều node cùng ping một endpoint ở cùng một thời điểm). Tính năng này làm phức tạp hóa tầng Infrastructure mà không mang lại thêm giá trị chứng minh cho triết lý Clean Architecture, do đó được xếp vào lộ trình phát triển tương lai.
- **Tích hợp cổng thông báo thực tế (Real Notification Adapters):** Hệ thống hiện tại sử dụng `LogNotificationAdapter` để giả lập quá trình gửi cảnh báo ra console. Việc kết nối trực tiếp với API của SendGrid (Email), Slack hay Telegram bị loại bỏ. Lý do là Kiến trúc Hexagonal (Ports & Adapters) chỉ yêu cầu chứng minh được tính mở của biên hệ thống thông qua các Interface. Khi Port đã được định nghĩa chuẩn xác, việc implement thêm các Adapter gọi API bên ngoài chỉ là công việc lập trình kỹ thuật (Coding Task) đơn giản, không mang tính thách thức về mặt kiến trúc phần mềm.
- **Hệ thống báo cáo SLA phức tạp (Complex SLA Reporting):** Việc tính toán độ sẵn sàng (ví dụ: 99.99% Uptime) theo tháng/năm đòi hỏi các kỹ thuật xử lý dữ liệu lớn (Batch Processing) hoặc Data Warehousing để tổng hợp từ hàng chục triệu bản ghi lịch sử. Do giới hạn về mặt thời gian và trọng tâm của đồ án nằm ở hệ thống xử lý giao dịch trực tuyến (OLTP), tính năng phân tích (OLAP) này tạm thời chưa được phát triển.

## 1.3. Các đối tượng sử dụng hệ thống (Actors)

Hệ thống thiết lập các ranh giới an ninh (Security Boundaries) nghiêm ngặt, phân chia quyền hạn rõ ràng cho 3 nhóm đối tượng sử dụng:

1. **System Administrator (Quản trị viên Hệ thống):**
   - **Đặc tả vai trò:** Là nhân sự thuộc cấp độ hạ tầng tập đoàn, nắm giữ quyền hạn cao nhất của phần mềm.
   - **Không gian thao tác:** Thao tác trên toàn bộ hệ thống (Global Scope).
   - **Ranh giới an ninh:** Đây là đối tượng duy nhất có quyền thiết lập (Provisioning), đình chỉ (Suspend) hoặc xóa bỏ các Workspaces. Tuy nhiên, System Admin không can thiệp vào các hoạt động giám sát chi tiết bên trong mỗi Workspace trừ khi được phân công thêm quyền hạn cụ thể.
2. **Workspace Admin (Quản trị viên Không gian làm việc):**
   - **Đặc tả vai trò:** Thường là Tech Lead hoặc Engineering Manager của một dự án/đội ngũ cụ thể.
   - **Không gian thao tác:** Hoàn toàn bị giới hạn trong phạm vi của một (hoặc nhiều) Workspace mà họ được phân quyền.
   - **Ranh giới an ninh:** Nắm toàn quyền sinh sát (CRUD) đối với các tài nguyên thuộc Workspace của mình: Quản lý danh sách Monitored Endpoints, thiết lập Check Policies, thêm/bớt Workspace Members, và định nghĩa các luật cảnh báo (Alert Rules). Không có quyền xem hay can thiệp vào bất kỳ dữ liệu nào thuộc Workspace của các nhóm khác.
3. **Workspace Member (Thành viên Không gian làm việc):**
   - **Đặc tả vai trò:** Các kỹ sư phần mềm, Tester hoặc chuyên viên vận hành thuộc đội dự án.
   - **Không gian thao tác:** Giới hạn trong Workspace mà họ tham gia.
   - **Ranh giới an ninh:** Quyền truy cập chủ yếu là Chỉ đọc (Read-only). Đối tượng này tiêu thụ các dữ liệu quan sát (Observability Consumer), có thể theo dõi Dashboard, phân tích biểu đồ độ trễ và kiểm tra chi tiết các sự cố, nhưng không được quyền thay đổi cấu hình hệ thống hay thao tác với các endpoint để tránh rủi ro do thao tác sai lệch.
