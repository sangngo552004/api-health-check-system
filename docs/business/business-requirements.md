# Business Requirements

Tài liệu này được viết theo góc nhìn BA để giải thích bài toán, actor, nghiệp vụ cốt lõi, quy tắc và phạm vi triển khai hiện tại của dự án.

## 1. Bối cảnh bài toán

Trong môi trường phát triển phần mềm, đội ngũ kỹ thuật thường phải theo dõi nhiều endpoint hoặc service:

- API public
- API nội bộ
- authentication service
- payment service
- database gateway
- webhook receiver

Nếu không có công cụ theo dõi tập trung, team thường gặp các vấn đề sau:

- chỉ phát hiện lỗi khi người dùng báo
- khó biết endpoint nào đang chậm, đang lỗi hay đã phục hồi
- thiếu lịch sử để phân tích xu hướng
- mỗi team theo dõi bằng cách riêng, thiếu chuẩn chung
- khó phân tách dữ liệu giữa các team hoặc workspace

`API Health Check System` được xây dựng để giải quyết nhóm vấn đề đó bằng một nền tảng giám sát tập trung.

## 2. Mục tiêu nghiệp vụ

Hệ thống cần cho phép team:

- đăng nhập vào hệ thống
- làm việc trong workspace của mình
- khai báo endpoint cần giám sát
- gán policy kiểm tra phù hợp
- xem tình trạng endpoint hiện tại
- xem incident đang mở
- nhóm người nhận cảnh báo theo contact group
- quản lý alert rule để định hướng cơ chế cảnh báo

## 3. Phạm vi nghiệp vụ hiện tại

### Trong phạm vi hiện tại

- quản lý workspace và thành viên
- quản lý endpoint
- quản lý check policy
- quản lý alert rule
- quản lý contact group
- health check định kỳ
- mở và đóng incident cơ bản
- dashboard tổng quan theo workspace

### Ngoài phạm vi hiện tại

- user self-registration hoàn chỉnh
- quy trình phê duyệt thành viên
- notification production-grade
- workflow xử lý incident nhiều bước
- maintenance window
- SLA reporting đầy đủ

## 4. Actor

### 4.1. System Admin

Vai trò quản trị cấp hệ thống.

Nhu cầu:

- tạo workspace mới
- xóa workspace
- quản trị tổng thể người dùng và không gian làm việc

### 4.2. Workspace Admin

Vai trò quản trị trong phạm vi một workspace.

Nhu cầu:

- tạo và cập nhật endpoint
- tạo policy, alert rule, contact group
- thêm và xóa thành viên trong workspace
- cấu hình dữ liệu giám sát của team

### 4.3. Workspace Member

Vai trò người dùng đọc và theo dõi.

Nhu cầu:

- xem dashboard
- xem endpoint
- xem policy, alert, contact group
- theo dõi tình trạng service trong workspace của mình

### 4.4. Scheduler / Monitoring Engine

Đây là actor hệ thống nội bộ, không phải người dùng.

Nhiệm vụ:

- tự động quét các endpoint active
- thực thi health check
- lưu lịch sử
- phân tích incident
- phát sự kiện cho cảnh báo

## 5. Danh sách thực thể nghiệp vụ

### 5.1. Workspace

Là đơn vị phân vùng dữ liệu chính của hệ thống.

Ý nghĩa nghiệp vụ:

- mỗi team hoặc mỗi nhóm dự án có thể có một workspace riêng
- dữ liệu monitor không bị trộn lẫn giữa các team
- phân quyền truy cập dựa trên workspace

### 5.2. Workspace Member

Biểu diễn quan hệ giữa người dùng và workspace.

Ý nghĩa nghiệp vụ:

- một user có thể thuộc nhiều workspace
- mỗi workspace member có role riêng trong workspace đó

### 5.3. Monitored Endpoint

Là đối tượng giám sát chính.

Thông tin nghiệp vụ điển hình:

- tên endpoint
- URL hoặc địa chỉ kết nối
- phương thức HTTP
- loại check
- môi trường
- policy áp dụng
- tags
- trạng thái hiện tại

### 5.4. Check Policy

Định nghĩa cách một endpoint được kiểm tra.

Ví dụ:

- timeout bao nhiêu
- kỳ vọng status code nào
- số lần lỗi liên tiếp bao nhiêu thì coi là sự cố
- response có cần chứa chuỗi cụ thể không
- response có cần match regex không
- độ trễ bao nhiêu thì coi là degraded

### 5.5. Alert Rule

Biểu diễn logic cảnh báo ở mức nghiệp vụ.

Hiện tại dự án đã có CRUD cho alert rule, nhưng luồng áp dụng cảnh báo nâng cao vẫn đang được đơn giản hóa trong xử lý runtime.

### 5.6. Contact Group

Nhóm đối tượng nhận cảnh báo.

Có thể bao gồm:

- user nội bộ
- email nhận cảnh báo
- webhook URL

### 5.7. Health Check Result

Là bản ghi lịch sử của một lần kiểm tra.

Ý nghĩa:

- giúp quan sát trạng thái hiện tại
- giúp phân tích xu hướng
- là đầu vào cho incident detection

### 5.8. Incident

Là sự cố nghiệp vụ được mở khi một endpoint có dấu hiệu lỗi theo policy.

Incident thường mang thông tin:

- endpoint nào gặp sự cố
- thời điểm bắt đầu
- lý do
- mức độ nghiêm trọng
- trạng thái mở hay đã khắc phục

## 6. Functional requirements

### FR-01. Đăng nhập hệ thống

Người dùng phải có thể đăng nhập để lấy access token và refresh token.

Kỳ vọng:

- hệ thống xác thực username/password
- trả access token để gọi API
- trả refresh token để làm mới phiên

### FR-02. Quản lý workspace

System Admin phải có thể tạo và xóa workspace.

Workspace Admin hoặc member hợp lệ phải có thể xem thông tin workspace mà họ thuộc về.

### FR-03. Quản lý thành viên workspace

Workspace Admin phải có thể:

- thêm user vào workspace
- gán role trong workspace
- xóa user khỏi workspace
- xem danh sách thành viên

### FR-04. Quản lý endpoint giám sát

Workspace Admin phải có thể:

- tạo endpoint mới
- cập nhật endpoint
- bật hoặc tắt endpoint
- xóa endpoint

Workspace Member phải có thể xem endpoint trong workspace.

### FR-05. Quản lý check policy

Workspace Admin phải có thể:

- tạo policy
- cập nhật policy
- xóa policy

Workspace Member phải có thể xem policy.

### FR-06. Quản lý alert rule

Workspace Admin phải có thể CRUD alert rule.

Workspace Member phải có thể xem alert rule.

### FR-07. Quản lý contact group

Workspace Admin phải có thể CRUD contact group.

Workspace Member phải có thể xem contact group.

### FR-08. Tự động kiểm tra endpoint

Hệ thống phải định kỳ kiểm tra toàn bộ endpoint active.

Kết quả cần lưu lại:

- thời điểm check
- success hay fail
- status code nếu là HTTP
- response time
- payload hoặc error message phù hợp

### FR-09. Xác định trạng thái endpoint

Sau mỗi lần check, hệ thống phải cập nhật trạng thái gần nhất của endpoint:

- `UP`
- `DEGRADED`
- `DOWN`

### FR-10. Mở incident khi lỗi liên tiếp

Nếu endpoint có chuỗi kết quả thất bại theo ngưỡng `failureThreshold`, hệ thống phải mở incident nếu hiện chưa có incident mở.

### FR-11. Đóng incident khi phục hồi

Nếu endpoint phục hồi sau khi đang có incident mở, hệ thống phải đóng incident đó.

### FR-12. Hiển thị dashboard workspace

Workspace Member phải có thể xem dashboard với:

- tổng số endpoint
- số endpoint UP
- số endpoint DEGRADED
- số endpoint DOWN
- số incident đang mở
- danh sách incident active
- lịch sử độ trễ của một số endpoint

## 7. Business rules

### BR-01

Mọi tài nguyên monitor chính phải thuộc một workspace.

### BR-02

Endpoint chỉ được scheduler chạy khi `isActive = true`.

### BR-03

Endpoint muốn được monitor đúng phải có `policyId`.

### BR-04

HTTP check có thể fail không chỉ vì status code mà còn vì response body hoặc regex không khớp.

### BR-05

Một endpoint có thể ở trạng thái `DEGRADED` nếu phản hồi thành công nhưng quá chậm theo ngưỡng policy.

### BR-06

Một workspace member không được thao tác dữ liệu của workspace khác.

### BR-07

Workspace Admin có quyền ghi trên tài nguyên của workspace mình; member chỉ có quyền xem.

### BR-08

Tại một thời điểm, một endpoint không nên sinh nhiều incident mở chồng nhau cho cùng một đợt lỗi.

## 8. Non-functional expectations

Mặc dù dự án hiện đang ở mức học thuật và MVP, hệ thống vẫn đang hướng tới một số tiêu chí phi chức năng:

- cấu trúc code rõ ràng để dễ phân tích kiến trúc
- có phân tách trách nhiệm giữa domain, application, infrastructure
- có test cho các use case quan trọng
- có OpenAPI để mô tả contract API
- có CI để bảo vệ formatting, linting, build và spec consistency

## 9. Pain points và khoảng trống hiện tại

Đây là phần quan trọng cho báo cáo vì nó cho thấy nhóm hiểu rất rõ mình đã làm đến đâu.

### 9.1. Khoảng trống nghiệp vụ

- chưa có quy trình đăng ký user hoàn chỉnh
- chưa có mô-đun incidents riêng đủ mạnh cho tra cứu và xử lý
- chưa có maintenance window
- chưa có quy trình escalation

### 9.2. Khoảng trống kỹ thuật ảnh hưởng nghiệp vụ

- notification chưa gửi thật nên giá trị cảnh báo ngoài runtime demo còn hạn chế
- scheduler chưa chạy theo interval riêng của từng policy
- chưa có chiến lược scale đa instance cho scheduler

## 10. Câu chuyện sử dụng điển hình

### User story 1

Là một `Workspace Admin`, tôi muốn tạo endpoint và gắn policy để hệ thống tự theo dõi service của team tôi.

### User story 2

Là một `Workspace Member`, tôi muốn mở dashboard và thấy ngay endpoint nào đang down để phản ứng nhanh hơn.

### User story 3

Là `hệ thống giám sát`, tôi muốn mở incident khi endpoint lỗi liên tục để team không bỏ sót sự cố thật.

### User story 4

Là một `Workspace Admin`, tôi muốn nhóm contact theo team để sau này có thể định tuyến cảnh báo đúng người nhận.
