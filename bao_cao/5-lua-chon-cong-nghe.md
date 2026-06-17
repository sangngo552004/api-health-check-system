# CHƯƠNG 5: LỰA CHỌN CÔNG NGHỆ

Sự thành bại của một dự án giám sát tải nặng (Heavy-load Monitoring) phụ thuộc rất lớn vào nền tảng công nghệ cơ sở. Tập hợp công nghệ (Tech Stack) của dự án được lựa chọn dựa trên sự khắt khe của môi trường Enterprise: Đề cao tính định kiểu tĩnh (Statically Typed), sự ổn định và khả năng bảo trì trong dài hạn.

## 5.1. Công nghệ phía Backend

- **Java 17 LTS:** Việc lựa chọn Java 17 không đơn thuần vì thói quen, mà là do phiên bản này mang lại cấu trúc dữ liệu kiểu `Record`, giúp định nghĩa các Data Transfer Object (DTO) một cách bất biến (Immutable) và tinh gọn, loại bỏ hoàn toàn mã boilerplate của Getter/Setter. Ngoài ra, khả năng quản lý luồng (Multithreading) xuất sắc của Máy ảo Java (JVM) là bệ phóng lý tưởng để xây dựng Động cơ Lập lịch (Scheduler) quét hàng nghìn endpoint đồng thời.
- **Spring Boot 3.5:** Là bộ khung (Framework) số một trong giới Enterprise Backend. Nó mang lại khả năng đảo ngược điều khiển (Inversion of Control - IoC) và tiêm phụ thuộc (Dependency Injection), giúp hiện thực hóa Kiến trúc Cổng và Bộ chuyển đổi (Ports and Adapters). Đồng thời, Spring AOP là cốt lõi để triển khai phân quyền Multi-workspace và quản lý Giao dịch CSDL (Transaction Boundary) tập trung.
- **PostgreSQL:** Khác với các hệ CSDL NoSQL, dữ liệu giám sát yêu cầu tính toàn vẹn rất cao giữa Không gian làm việc, Chính sách và Điểm cuối. PostgreSQL cung cấp độ tin cậy tuyệt đối với tính tuân thủ ACID (Atomicity, Consistency, Isolation, Durability), khả năng tạo các chỉ mục phức tạp (B-Tree Indexing) giúp truy vấn hàng chục triệu bản ghi lịch sử `health_check_results` mà không làm nghẽn hệ thống.
- **Flyway:** Trong môi trường làm việc nhóm, cấu trúc CSDL liên tục thay đổi. Flyway đóng vai trò là "Git dành cho Database", thực thi mã hóa các bản di cư (Schema Migrations) theo thứ tự thời gian. Điều này giúp loại trừ hoàn toàn việc đồng bộ database thủ công và là yếu tố sống còn cho các luồng Tích hợp Liên tục (CI/CD).

## 5.2. Công nghệ phía Frontend

- **React & TypeScript:** React cung cấp khả năng đóng gói giao diện thành các Component độc lập, tái sử dụng cao. Khi kết hợp với TypeScript, tính Định kiểu Tĩnh của ngôn ngữ này giúp triệt tiêu tới 90% các lỗi thời gian chạy (Runtime Errors) liên quan đến việc truyền nhầm kiểu dữ liệu, bảo đảm tính khế ước API (API Contract) chặt chẽ với Backend.
- **Vite:** Thay thế hoàn toàn Webpack. Cấu trúc Server phát triển cục bộ dựa trên cơ chế Native ES Modules của Vite giúp Frontend tải nóng mã nguồn (Hot Module Replacement) ngay lập tức, tiết kiệm đáng kể thời gian phát triển và tăng trải nghiệm Developer Experience (DX).
- **Zustand:** Việc quản lý trạng thái toàn cục (Global State) như Thông tin Đăng nhập, Workspace hiện hành là vô cùng quan trọng. Thay vì sử dụng Redux với lượng mã boilerplate rườm rà, Zustand mang lại cơ chế Store nhẹ nhàng, phân phối trạng thái một cách tinh giản mà vẫn đảm bảo tính một nguồn chân lý (Single Source of Truth).
- **Recharts:** Nhu cầu vẽ biểu đồ độ trễ (Latency Graph) theo dòng thời gian yêu cầu một thư viện hiệu năng cao. Recharts, được xây dựng hoàn toàn bằng các phần tử SVG nguyên bản của React, giải quyết triệt để nhu cầu thị giác hóa dữ liệu (Data Visualization) này mà không gây giật lag trình duyệt.

## 5.3. Vai trò tối cao của Công cụ kiểm soát chất lượng mã nguồn

Viết ra mã nguồn chạy được là dễ, nhưng giữ cho khối mã đó không bị mục nát (Code Rot) theo thời gian lại là thử thách lớn nhất. Dự án thiết lập ranh giới phòng thủ nghiêm ngặt bằng bộ 3 công cụ kiểm soát chất lượng (Quality Assurance):

1. **ArchUnit (Kiểm soát Kiến trúc Sạch):** 
   - Lý do khoa học: Dù thiết kế kiến trúc lý thuyết có hoàn hảo đến đâu, lập trình viên vẫn có thể do lười biếng mà chèn một câu lệnh truy vấn JPA vào thẳng tầng Domain. 
   - ArchUnit biến cấu trúc hệ thống thành các bài Unit Test tự động. Nó "ép buộc" mã nguồn phải tuân thủ nghiêm ngặt quy tắc phụ thuộc: Tầng Domain cấm tuyệt đối import bất cứ thư viện nào của Spring Data hay Spring Web; Tầng Application không được phép trực tiếp gọi các tầng Adapter. Nếu quy tắc này bị vi phạm, quá trình Build dự án sẽ lập tức thất bại.
2. **JaCoCo (Kiểm soát Độ bao phủ Test):** 
   - Lý do khoa học: Dự án không thể khẳng định Lõi phân tích sự cố (`IncidentAnalyzer`) hoạt động đúng nếu không có số liệu chứng minh. 
   - JaCoCo đo lường và bắt buộc mã nguồn lõi phải đạt được **ngưỡng bao phủ lệnh tối thiểu 80% (Code Coverage)**. Điều này ép các kỹ sư phần mềm phải viết Unit Test cho toàn bộ các luồng if-else, đảm bảo các kịch bản ngoại lệ (như mất mạng, server timeout) đều đã được mô phỏng và vượt qua kiểm thử.
3. **Checkstyle (Đồng nhất Văn phong):** 
   - Công cụ này đóng vai trò như một người gác cổng định dạng mã nguồn (Code Formatter Validator). Nó tuân thủ các quy tắc chuẩn của ngành (Java Convention), đảm bảo rằng dù dự án có 10 kỹ sư tham gia lập trình, toàn bộ mã nguồn vẫn trông nhất quán như do một người duy nhất viết ra, giảm thiểu tối đa chi phí bảo trì (Maintenance Cost) trong dài hạn.
