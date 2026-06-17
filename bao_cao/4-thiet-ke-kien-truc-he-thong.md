# CHƯƠNG 4: THIẾT KẾ KIẾN TRÚC HỆ THỐNG

## 4.1. Mô hình kiến trúc tổng thể (Client - Server)

Hệ thống "API Health Check" được thiết kế dựa trên mô hình kiến trúc phân tán **Client - Server** thuần túy, định hình rõ ràng hai thái cực: Phía phục vụ (Backend) và Phía tiêu thụ (Frontend). 

Sự phân tách này mang lại giá trị cốt lõi là sự lỏng lẻo trong kết nối (Decoupling). 
- **Phía Backend** đóng vai trò là một máy chủ cung cấp dịch vụ RESTful API hoàn toàn Phi trạng thái (Stateless). Nó tập trung toàn bộ tài nguyên CPU và RAM để giải quyết các bài toán nghiệp vụ lõi: Lập lịch quét, thực thi thuật toán kiểm tra mạng, phân tích sự cố và bảo vệ an toàn dữ liệu. Việc không dính líu đến quá trình Render HTML (như các mô hình MVC truyền thống) giúp Backend dễ dàng mở rộng theo chiều ngang (Horizontal Scaling) khi số lượng endpoint tăng vọt.
- **Phía Frontend** là một ứng dụng Single Page Application (SPA) hoạt động trên trình duyệt của người dùng. Nó tiêu thụ các API từ Backend, chịu trách nhiệm quản lý luồng điều hướng, quản lý trạng thái giao diện và biểu diễn dữ liệu trực quan bằng biểu đồ. 

## 4.2. Cơ chế Xác thực và Phân quyền

Trong kiến trúc Enterprise, Bảo mật (Security) không thể phó mặc cho những đoạn code kiểm tra `if-else` rời rạc, mà phải được thiết kế thành một hàng rào vững chắc nằm ở vòng ngoài cùng của hệ thống.

### 4.2.1. Xác thực bằng JWT (Access Token & Refresh Token)
Để đảm bảo bản chất Phi trạng thái (Stateless) của kiến trúc REST, hệ thống loại bỏ hoàn toàn cơ chế Session lưu trữ trên bộ nhớ máy chủ (Server-side Session). Thay vào đó, nền tảng ứng dụng Tiêu chuẩn chữ ký kỹ thuật số **JSON Web Token (JWT)** với chiến lược "Cặp bài trùng" mã thông báo:
- **Access Token (Mã thông báo Truy cập):** Đây là chiếc chìa khóa ngắn hạn (thời gian sống chỉ khoảng 15-30 phút). Để đối phó với lỗ hổng chèn mã độc (Cross-Site Scripting - XSS) khét tiếng, Frontend hoàn toàn không lưu Access Token xuống `localStorage` hay `sessionStorage`. Thay vào đó, nó được giữ trong **In-memory State** (biến RAM của trình duyệt). Nếu kẻ tấn công chèn được mã JS độc hại, chúng cũng không thể đánh cắp token này khi người dùng tải lại trang.
- **Refresh Token (Mã thông báo Cấp mới):** Đây là chiếc chìa khóa dài hạn (sống vài tuần). Nó được lưu trữ cố định ở `localStorage` (hoặc lý tưởng hơn là HttpOnly Cookie). Khi Access Token in-memory hết hạn, Frontend sẽ sử dụng Refresh Token để âm thầm gọi một API chạy ngầm, xin máy chủ cấp phát một Access Token mới. 

Thiết kế này giải quyết được một bài toán bảo mật kinh điển: Vừa vô hiệu hóa rủi ro XSS nhắm vào Access Token, vừa mang lại Trải nghiệm Người dùng (UX) liền mạch, không bắt buộc người dùng phải đăng nhập lại liên tục mỗi khi token ngắn hạn hết phép.

### 4.2.2. Phân quyền bóc tách qua Header X-Workspace-Id + Spring Security AOP
Xác thực danh tính (Authentication) bằng JWT mới chỉ trả lời câu hỏi *"Anh là ai?"*, hệ thống cần giải quyết bài toán Cấp phép (Authorization) để trả lời câu hỏi *"Anh được quyền làm gì ở không gian này?"*.

Với đặc thù của hệ thống Multi-tenant, dữ liệu bị chia cắt theo từng Workspace. Việc kiểm tra quyền hạn không thể được hard-code lặp đi lặp lại bên trong từng thân hàm (Use Case). Dự án đã ứng dụng kỹ thuật **Lập trình Hướng Khía Cạnh (AOP - Aspect-Oriented Programming)** thông qua Spring Security.

1. **Giao tiếp Context:** Mọi thao tác nghiệp vụ từ Frontend gửi lên (tạo Endpoint, xóa Policy) đều bị ép buộc phải đính kèm một Custom Header có tên `X-Workspace-Id`. Header này đại diện cho Ngữ cảnh Không gian (Workspace Context) mà người dùng đang muốn thao tác.
2. **Hàng rào AOP Evaluator:** Trước khi HTTP Request kịp chạm tới lớp Controller, các Annotation bảo mật (như `@PreAuthorize`) sẽ kích hoạt lớp `WorkspaceSecurityEvaluator`. 
3. **Phân tích đối chiếu:** Evaluator sẽ trích xuất User ID từ JWT, trích xuất Workspace ID từ Header, và thực hiện truy vấn xuống cơ sở dữ liệu để kiểm chứng: *"User này có thực sự thuộc Workspace này không? Và Role của họ (Admin hay Member) có thỏa mãn điều kiện thực thi API này không?"*.

Nếu Evaluator trả về `false`, hệ thống lập tức ném ra ngoại lệ `403 Forbidden`, cắt đứt luồng thực thi. Kỹ thuật này giúp tách bạch hoàn toàn Logic Bảo mật khỏi Logic Nghiệp vụ, giữ cho mã nguồn các lớp Use Case vô cùng gọn gàng và tuân thủ chặt chẽ Nguyên lý Đơn trách nhiệm (Single Responsibility Principle).
