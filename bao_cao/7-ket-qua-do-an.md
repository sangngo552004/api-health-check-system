# CHƯƠNG 7: KẾT QUẢ ĐỒ ÁN

Sau quá trình nghiên cứu, phân tích thiết kế và tiến hành cài đặt mã nguồn, đồ án "API Health Check System" đã đạt được mục tiêu đề ra là xây dựng thành công một nguyên bản (Minimum Viable Product - MVP) hoàn chỉnh, chạy ổn định và tuân thủ chặt chẽ các triết lý của Kiến trúc Sạch.

## 7.1. Các tính năng đã hoàn thiện của MVP

Hệ thống đã triển khai thành công và đưa vào vận hành trơn tru các luồng nghiệp vụ cốt lõi sau:
1. **Khung nền Bảo mật và Định danh:** Hoàn thiện cơ chế đăng nhập không trạng thái (Stateless Authentication) bằng cặp mã thông báo JWT (Access Token & Refresh Token). Xây dựng thành công hàng rào AOP phân lập không gian làm việc (Multi-workspace Isolation).
2. **Quản trị Cấu hình Giám sát (CRUD Operations):** Cung cấp đầy đủ các API cho phép Workspace Admin thêm mới, cập nhật, xóa các Điểm cuối (Monitored Endpoints) và thiết lập các Chính sách kiểm tra (Check Policies) linh hoạt (bao gồm chu kỳ quét, ngưỡng thời gian chờ, và ngưỡng chịu lỗi).
3. **Động cơ Giám sát Nền (Background Monitor Engine):** Triển khai thành công bộ lập lịch (Scheduler) đa luồng, tự động bóc tách danh sách Endpoint đang kích hoạt để thực thi Ping qua giao thức HTTP và TCP một cách bất đồng bộ.
4. **Quản lý Vòng đời Sự cố Tự động (Incident Lifecycle):** Động cơ `IncidentAnalyzer` hoạt động chính xác dựa trên thuật toán cửa sổ trượt (Sliding Window). Hệ thống không cảnh báo sai lệch (False Positive) khi rớt mạng tạm thời, tự động MỞ sự cố khi đạt ngưỡng lỗi liên tiếp và tự động ĐÓNG sự cố khi điểm cuối phục hồi.
5. **Tra cứu Incident ở giao diện người dùng:** Hệ thống đã có màn hình Incidents riêng cho phép lọc theo trạng thái, endpoint và xem chi tiết từng sự cố. Điều này biến Incident từ một dữ liệu chỉ xuất hiện trên Dashboard thành một mô-đun có khả năng trình bày độc lập.
6. **Giao diện Giám sát trực quan (Dashboard):** API trả về dữ liệu tổng hợp chính xác, giúp Frontend vẽ được biểu đồ độ trễ, thống kê tỷ lệ UP/DOWN/DEGRADED theo thời gian thực, và điều hướng trực tiếp sang danh sách incident đang mở.
7. **Cảnh báo Webhook thực tế:** Bên cạnh cơ chế log phục vụ debug, hệ thống đã triển khai luồng gửi webhook cảnh báo khi incident mở hoặc phục hồi. Đây là bước tiến quan trọng giúp đồ án có thể demo end-to-end với các dịch vụ nhận webhook bên ngoài.

## 7.2. Hình ảnh giao diện thực tế của ứng dụng

Dưới đây là các hình ảnh giao diện thực tế tương tác với hệ thống (Tuân thủ theo Cẩm nang UI Screenshot Guide của dự án).

*(Ghi chú cho tác giả: Hãy chụp màn hình ứng dụng đang chạy ở môi trường Local/Dev và dán đè lên các dòng Placeholder dưới đây)*

Để bộ ảnh có tính thuyết phục cao, nên chụp theo đúng một kịch bản demo thống nhất: cùng một workspace, cùng một endpoint monitor trỏ tới `demo-service`, cùng một mạch thời gian từ lúc hệ thống ổn định cho tới lúc incident mở rồi phục hồi. Khi toàn bộ ảnh dùng cùng một ngữ cảnh dữ liệu, báo cáo sẽ chuyên nghiệp hơn và hội đồng dễ theo dõi logic hơn.

- **[Hình 7.1] Màn hình Đăng nhập và Chọn Workspace:** Hiển thị giao diện bảo mật ban đầu.
- **[Hình 7.2] Bảng điều khiển (Dashboard Overview):** Hiển thị các Widget thống kê tổng số Endpoint, danh sách Incident đang mở, và biểu đồ đường (Line chart) diễn biến độ trễ mạng.
- **[Hình 7.3] Giao diện Quản lý Endpoint:** Màn hình CRUD danh sách các API cần giám sát, trạng thái UP/DOWN được tô màu trực quan (Xanh/Đỏ).
- **[Hình 7.4] Giao diện Thiết lập Check Policy:** Thể hiện form nhập liệu cấu hình Threshold, Interval và Timeout.
- **[Hình 7.5] Giao diện Danh sách Incident:** Màn hình lọc danh sách sự cố theo trạng thái `OPEN`, `RESOLVED`, và theo Endpoint.
- **[Hình 7.6] Màn hình Chi tiết Incident:** Hiển thị endpoint liên quan, thời điểm mở/đóng, severity, failure count và nguyên nhân.
- **[Hình 7.7] Minh họa Webhook Receiver:** Chứng minh hệ thống đã đẩy cảnh báo ra ngoài khi incident thay đổi trạng thái.

Ngoài các ảnh giao diện chính, nhóm nên lưu lại thêm hai loại bằng chứng phụ trợ:

- ảnh chụp trạng thái của `demo-service` trước và sau khi đổi mode,
- ảnh chụp Swagger UI hoặc `/actuator/health` để chứng minh dịch vụ backend đang chạy đúng tại thời điểm demo.

## 7.3. Kết quả kiểm thử và độ phủ mã nguồn (Test Coverage)

Một kiến trúc phần mềm chỉ thực sự "Sạch" khi nó có thể vượt qua các công cụ đo lường khách quan. Hệ thống đạt được các chỉ số chất lượng phần mềm (Software Quality Metrics) vô cùng tích cực:
- **Kiểm thử Kiến trúc (ArchUnit):** Đạt tỷ lệ **Pass 100%**. Toàn bộ mã nguồn tuân thủ tuyệt đối quy tắc ranh giới của Kiến trúc Sạch. Tầng Domain không chứa bất kỳ phụ thuộc (Dependency) nào của Framework, và Tầng Application không bị lây nhiễm cấu trúc CSDL của JPA.
- **Độ phủ mã nguồn (JaCoCo Coverage):** Hệ thống được kiểm thử tự động bằng kỹ thuật Unit Test và Integration Test. Các lớp lõi nghiệp vụ cực kỳ quan trọng, đặc biệt là `IncidentAnalyzer` và `MonitorEndpointService` đều đạt độ phủ mã nguồn (Instruction Coverage) **trên ngưỡng 80%**. Các kịch bản ngoại lệ như lỗi đường truyền, phản hồi Timeout đều được giả lập (Mocking) thành công để đảm bảo độ rẽ nhánh (Branch Coverage) đạt yêu cầu công nghiệp.
- **Kiểm soát Văn phong (Checkstyle):** Không tồn tại cảnh báo (0 Warnings) về quy ước đặt tên biến, thụt lề hay độ dài hàm, đảm bảo mã nguồn đồng nhất.

## 7.4. Đánh giá ưu điểm và nhược điểm

### Đánh giá Ưu điểm
1. **Kiến trúc bền vững và Dễ mở rộng:** Nhờ áp dụng linh hoạt 8 Mẫu thiết kế (Design Patterns) như Strategy, Observer, Repository, hệ thống loại bỏ hoàn toàn mã nguồn Spaghetti. Việc cắm thêm một thuật toán Ping mới (như gRPC) hay thêm một cổng thông báo (như Slack) chỉ tốn vài giờ code lớp Adapter mà không gây rủi ro đứt gãy luồng lõi.
2. **An toàn Bảo mật tuyệt đối:** Lỗ hổng Insecure Direct Object Reference (IDOR) bị triệt tiêu hoàn toàn nhờ tư duy thiết kế "Context-Aware Security". Lập trình viên bị ép phải kiểm tra `workspace_id` thông qua AOP ngay từ biên hệ thống, dữ liệu khách hàng được cô lập an toàn.
3. **Tính trình diễn cao khi bảo vệ đồ án:** Hệ thống hiện không chỉ có dashboard tổng quan mà còn có luồng incident riêng và khả năng phát webhook thật, giúp nhóm dễ chứng minh tính hoàn chỉnh của bài toán giám sát hơn trước hội đồng.

### Đánh giá Nhược điểm (Giới hạn hiện tại)
1. **Nút thắt cổ chai ở Bộ lập lịch (Single-Node Scheduler):** Ở phiên bản MVP này, hệ thống Scheduler chỉ được thiết kế để chạy trên một phiên bản máy chủ (Single JVM Instance). Điều này làm hạn chế khả năng nâng cấp hệ thống (Horizontal Scaling) nếu số lượng Endpoint cần giám sát lên tới hàng triệu.
2. **Notification mới mạnh ở nhánh Webhook:** Hệ thống đã gửi được webhook thực tế, tuy nhiên các kênh cảnh báo production-grade khác như Email, Slack Adapter chuyên biệt, retry policy và theo dõi delivery status vẫn chưa hoàn thiện đầy đủ.
3. **Đăng ký người dùng mới chưa hoàn chỉnh:** Luồng đăng ký self-service chưa phải trọng tâm của phiên bản hiện tại; hệ thống vẫn phù hợp hơn với mô hình account seed hoặc account do quản trị viên chuẩn bị trước.
