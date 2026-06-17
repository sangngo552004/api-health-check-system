# CHƯƠNG 6: KIẾN TRÚC PHẦN MỀM VÀ CÁC MẪU THIẾT KẾ

Thước đo giá trị của một kỹ sư phần mềm cấp cao không nằm ở việc họ có thể làm cho ứng dụng chạy được, mà nằm ở việc họ tổ chức mã nguồn như thế nào để ứng dụng có thể "sống sót" và phát triển qua nhiều năm bảo trì. Hệ thống API Health Check System không viết code theo lối mòn nguyên khối (Spaghetti code) mà được kiến trúc hóa nghiêm ngặt bằng **Kiến trúc Sạch (Clean Architecture)** kết hợp với hàng loạt các **Mẫu thiết kế (Design Patterns)** kinh điển.

## 6.1. Áp dụng Clean Architecture

Trái tim của Clean Architecture là nguyên tắc: **Quy tắc Phụ thuộc (The Dependency Rule)**. Mã nguồn chỉ được phép phụ thuộc từ ngoài vào trong. Lớp bên trong tuyệt đối không được biết gì về sự tồn tại của lớp bên ngoài. Dự án chia cắt ranh giới bảo mật thành 4 phân lớp rõ rệt:

1. **Lớp Lõi Nghiệp vụ (Domain Layer):** Đây là khu vực bất khả xâm phạm. Nó chứa các Thực thể (Entities) như `MonitoredEndpoint`, `Incident` và các Dịch vụ Nghiệp vụ (Domain Service) như `IncidentAnalyzer`. Đặc điểm tối thượng của lớp này là nó **hoàn toàn thuần Java (Pure Java)**. Bạn sẽ không tìm thấy bất kỳ dòng code nào import thư viện Spring, Hibernate hay Jackson tại đây. Nhờ vậy, nghiệp vụ lõi miễn nhiễm hoàn toàn với những đợt nâng cấp hoặc khai tử framework trong tương lai.
2. **Lớp Ứng dụng (Application / Use Case Layer):** Nhiệm vụ của lớp này là điều phối (Orchestration). Nó nhận yêu cầu từ người dùng, gọi cơ sở dữ liệu để lấy thực thể Domain, yêu cầu Domain xử lý nghiệp vụ, và lưu lại kết quả. Lớp này chứa các Cổng Giao Tiếp (Ports) như `EndpointRepository`, `HealthCheckExecutor`. Nó quy định "hệ thống cần gì" nhưng không quy định "công nghệ nào sẽ làm việc đó".
3. **Lớp Hạ Tầng (Infrastructure Layer):** Nằm ở vòng ngoài cùng, đây là nơi chứa các chi tiết kỹ thuật: Cơ sở dữ liệu (PostgreSQL/JPA), Hệ thống Lập lịch (Spring Scheduler), và Bảo mật (JWT). Nó cung cấp các Bộ chuyển đổi (Adapters) để thực thi các Ports do tầng Application định nghĩa.
4. **Lớp Giao Tiếp Biên (Delivery Layer):** Nơi tiếp nhận các tương tác HTTP/REST từ Client. Lớp này chỉ làm nhiệm vụ xác thực đầu vào (Validation) và chuyển hướng (Routing) dữ liệu xuống Use Case. Nó mỏng, gọn và không chứa bất kỳ logic kiểm tra mạng lưới nào.

Nếu nhìn ở góc độ học thuật, kiến trúc này mang lại hai lợi ích cực kỳ lớn. Thứ nhất là **khả năng bảo vệ nghiệp vụ lõi khỏi sự biến động của công nghệ**. Trong thực tế doanh nghiệp, framework, database, và cách thức triển khai có thể thay đổi sau 1-2 năm; nhưng bản chất nghiệp vụ "quét endpoint", "đánh giá incident", "cô lập dữ liệu theo workspace" gần như không thay đổi. Clean Architecture cho phép phần ổn định nhất của hệ thống nằm ở trung tâm, được bảo vệ tối đa. Thứ hai là **khả năng kiểm thử độc lập**. Bởi vì lõi nghiệp vụ không phụ thuộc Spring hay JPA, lập trình viên có thể dựng Unit Test rất nhanh cho `IncidentAnalyzer`, `ManageEndpointService`, `MonitorEndpointService` mà không cần khởi động cả ứng dụng.

### 6.1.1. Ánh xạ lớp kiến trúc vào mã nguồn dự án

Để chứng minh đây không chỉ là lý thuyết trên giấy, có thể ánh xạ trực tiếp các phân lớp Clean Architecture vào cấu trúc package thực tế của dự án:

- `domain`: chứa `MonitoredEndpoint`, `Incident`, `CheckPolicy`, `EndpointCheckedEvent`, `IncidentOpenedEvent`, `IncidentResolvedEvent`, `IncidentAnalyzer`.
- `application`: chứa `ManageEndpointService`, `MonitorEndpointService`, `GetDashboardStatsService`, các Input Port và Output Port, DTO, Mapper ở mức use case.
- `infrastructure`: chứa `EndpointRepositoryAdapter`, `IncidentRepositoryAdapter`, `JwtAuthenticationFilter`, `HealthCheckScheduler`, `HttpHealthCheckExecutor`, `TcpHealthCheckExecutor`, `WebhookNotificationAdapter`.
- `delivery`: chứa `AuthController`, `MonitoredEndpointController`, `DashboardController`, `IncidentController`, `GlobalExceptionHandler`.

Việc chia package như trên không đơn thuần để "đẹp mắt", mà để tạo ra hàng rào tâm lý và hàng rào kỹ thuật. Một lập trình viên khi đang làm trong package `domain` sẽ không có lý do chính đáng nào để import thẳng một `JpaRepository`; nếu làm điều đó, họ đã tự phá vỡ quy tắc kiến trúc và các bài ArchUnit test sẽ lập tức phát hiện sai phạm.

### 6.1.2. Quy tắc phụ thuộc và hướng đi của dữ liệu

Một điểm dễ gây nhầm lẫn cho người mới là: Hướng dữ liệu chạy trong lúc thực thi không trùng với hướng phụ thuộc của mã nguồn.

- **Hướng dữ liệu lúc chạy:** Request từ Frontend đi vào Controller, Controller gọi Use Case, Use Case gọi Port, Port được Adapter hiện thực hóa, Adapter truy cập DB hoặc mạng ngoài.
- **Hướng phụ thuộc mã nguồn:** Delivery phụ thuộc Application, Infrastructure phụ thuộc Application, Application phụ thuộc Domain, còn Domain không phụ thuộc ai cả.

Sự khác nhau này chính là bản chất của nguyên lý **Dependency Inversion**. Ở runtime, chương trình vẫn vận hành bình thường từ ngoài vào trong và từ trong ra ngoài. Nhưng ở compile time, phần lõi không bị phụ thuộc ngược vào hạ tầng. Nhờ đó, nếu một ngày hệ thống đổi từ PostgreSQL sang một công nghệ khác, hoặc đổi cơ chế gửi cảnh báo từ webhook sang message broker, tầng nghiệp vụ trung tâm vẫn giữ nguyên.

### 6.1.3. Vì sao dự án không chọn mô hình Controller - Service - Repository truyền thống

Kiến trúc 3 lớp truyền thống (`Controller -> Service -> Repository`) là cách tổ chức rất phổ biến, dễ bắt đầu, nhưng dễ gặp ba vấn đề khi hệ thống lớn dần:

1. **Service phình to thành God Object:** mọi thứ dồn vào một lớp Service duy nhất, từ validation, query database, gọi HTTP client, gửi cảnh báo, đến phân tích business rules.
2. **Entity bị rò rỉ xuyên tầng:** JPA Entity thường bị trả thẳng ra API hoặc được dùng trực tiếp ở UI contract, gây coupling rất mạnh.
3. **Khó thay đổi công nghệ rìa:** phần Service lõi bị trói chặt với framework persistence hoặc external SDK.

Đối với một đề tài mà trọng tâm là chứng minh tư duy kiến trúc, việc chọn Clean Architecture là hợp lý hơn nhiều. Nó giúp đồ án không chỉ "làm được" mà còn trả lời được câu hỏi của hội đồng: *Tại sao hệ thống này có thể bảo trì được lâu dài?*

**Nguyên lý Đảo ngược phụ thuộc (Dependency Inversion) thông qua Cổng - Bộ chuyển đổi (Ports and Adapters):**
Theo lẽ thường, Use Case sẽ phải gọi thẳng xuống CSDL (JPA Repository). Nhưng làm vậy, nghiệp vụ lõi sẽ bị trói chặt vào công nghệ SQL. Hệ thống giải quyết bằng cách: Tầng `Application` tự định nghĩa một Interface (Port) mang tên `EndpointRepository` và lập trình dựa trên Interface đó. Tầng `Infrastructure` bên ngoài sẽ viết một lớp `EndpointRepositoryAdapter` thực thi (implement) Interface đó. Spring Framework sẽ tự động "tiêm" (Inject) Adapter vào Port lúc ứng dụng khởi động. Kết quả: Chiều gọi hàm vẫn là từ Trên xuống Dưới, nhưng Chiều phụ thuộc mã nguồn đã bị đảo ngược từ Dưới lên Trên. 

### 6.1.4. Ranh giới giao dịch và ranh giới lỗi

Một hệ thống giám sát không chỉ cần xử lý đúng, mà còn cần thất bại đúng cách. Do đó, khi áp dụng Clean Architecture, dự án còn quan tâm tới hai ranh giới kỹ thuật:

- **Transaction Boundary:** Các Use Case như tạo endpoint, cập nhật policy, mở/đóng incident phải được đặt trong ranh giới giao dịch rõ ràng để tránh trạng thái dữ liệu nửa chừng.
- **Exception Boundary:** Ngoại lệ ở Domain, Application, Delivery phải được tách nghĩa. Domain chỉ ném ra lỗi nghiệp vụ thuần túy; Delivery mới là nơi chuyển hóa chúng thành mã HTTP như `400`, `403`, `404`.

Thiết kế này giúp hệ thống tránh được một sai lầm phổ biến: để chi tiết HTTP, JSON hay database transaction xâm nhập thẳng vào trung tâm nghiệp vụ.

## 6.2. Tư duy kiến trúc xuyên suốt hệ thống

Trước khi đi vào từng Design Pattern cụ thể, cần xác định rõ hệ thống này được dẫn dắt bởi ba tư duy thiết kế chủ đạo:

1. **Context-aware Security:** mọi hành vi nghiệp vụ đều phụ thuộc ngữ cảnh workspace, do đó xác thực và phân quyền không chỉ kiểm tra user là ai, mà còn phải kiểm tra user đang thao tác trong workspace nào.
2. **Automation-first:** hệ thống được xây quanh các luồng nền tự động như scheduler, incident analyzer, event listener; giao diện người dùng chỉ là lớp trình bày của dữ liệu vận hành.
3. **Decoupled Integration:** mọi tích hợp ra ngoài như webhook, email, database, TCP socket đều bị đẩy ra rìa bằng Port và Adapter để giữ cho lõi nghiệp vụ không bị ô nhiễm.

Ba tư duy này giải thích vì sao dự án lại chọn những mẫu thiết kế ở phần sau, thay vì dùng các cách viết "thẳng tay" nhanh hơn nhưng khó bảo trì.

## 6.2. Các mẫu thiết kế (Design Patterns) áp dụng

Mẫu thiết kế không phải là lý thuyết sáo rỗng. Trong dự án này, mỗi mẫu thiết kế được áp dụng như một liều thuốc đặc trị cho một vấn đề kiến trúc nhức nhối.

### 6.2.1. Repository Pattern (Mẫu Kho chứa)
- **Vấn đề thực tế:** Khi cần lấy danh sách Endpoint, nếu viết trực tiếp câu lệnh truy vấn SQL (`SELECT * FROM...`) hoặc dùng thẳng `JpaRepository` vào bên trong luồng quét mạng, mã nguồn sẽ trở thành một mớ hỗn độn. Hơn thế nữa, không thể viết Unit Test cho hàm quét mạng nếu máy chủ tắt Database.
- **Giải pháp:** Sử dụng Repository Pattern để tạo ra một ảo giác rằng: "Cơ sở dữ liệu chỉ là một List Collection nằm trong bộ nhớ RAM". Tầng nghiệp vụ chỉ cần gọi lệnh `save(endpoint)` hoặc `findAllActive()`, toàn bộ sự phức tạp của SQL, Connection Pool, hay Hibernate đều bị giấu kín phía sau.
- **Minh chứng mã nguồn:** Interface `EndpointRepository` (Tầng Application) và lớp triển khai `EndpointRepositoryAdapter` (Tầng Infrastructure).

**Giá trị thực tiễn:** Nhờ Repository Pattern, cùng một Use Case có thể làm việc với dữ liệu thật trong PostgreSQL hoặc dữ liệu giả lập trong Unit Test. Đây là lý do các bài test ở tầng Application viết nhanh và ít phụ thuộc hạ tầng hơn.

### 6.2.2. Strategy Pattern (Mẫu Chiến lược)
- **Vấn đề thực tế:** Hệ thống cần kiểm tra sức khỏe qua nhiều giao thức: HTTP (Check body, status code) và TCP (Check open socket). Nếu viết một hàm `ping()` chứa những khối lệnh `if(type == HTTP) ... else if (type == TCP) ...`, thì mỗi khi công ty yêu cầu hỗ trợ thêm giao thức PING ICMP hoặc gRPC, lập trình viên sẽ phải mổ xẻ mã nguồn cốt lõi để sửa. Điều này vi phạm nghiêm trọng Nguyên lý Đóng/Mở (Open/Closed Principle).
- **Giải pháp:** Đóng gói từng thuật toán kiểm tra vào một lớp riêng biệt (Strategy). Tất cả các lớp này đều tuân thủ chung một hợp đồng giao tiếp (Interface). Khi quét, hệ thống tự động duyệt qua danh sách chiến lược và hỏi: "Ai hỗ trợ giao thức này?". Chiến lược phù hợp sẽ tự đứng ra thực thi.
- **Minh chứng mã nguồn:** Interface `HealthCheckExecutor` chứa phương thức `supports(CheckType)`. Hai chiến lược cài đặt thực tế là `HttpHealthCheckExecutor` và `TcpHealthCheckExecutor`.

**Ý nghĩa mở rộng:** Strategy Pattern biến bài toán "thêm giao thức mới" từ một thay đổi mang tính phá hủy sang một thay đổi mang tính cộng thêm. Nếu sau này cần hỗ trợ ICMP, gRPC health check hay database connectivity check, nhóm chỉ cần cài thêm chiến lược mới thay vì chỉnh sửa lại Use Case lõi.

### 6.2.3. Domain Event Pattern (Mẫu Sự kiện Nghiệp vụ)
- **Vấn đề thực tế:** Khi phát hiện Endpoint bị sập, hệ thống phải làm 3 việc: (1) Lưu sự cố vào DB, (2) Viết Audit Log, (3) Gửi Email cảnh báo. Nếu nhét cả 3 việc này vào một khối lệnh tuần tự, thời gian phản hồi sẽ cực chậm. Nếu server Email bị lỗi, nó sẽ làm sập luôn tiến trình quét mạng hiện tại (Tight Coupling).
- **Giải pháp:** Áp dụng Kiến trúc Hướng sự kiện (Event-Driven). Sau khi lưu sự cố vào CSDL, Luồng công việc chính chỉ đơn giản là "hét lên" (Publish) một sự kiện vào bộ nhớ RAM: "Ê, có sự cố mới mở kìa!", rồi tiếp tục đi quét Endpoint khác. Tiến trình chính không cần quan tâm ai sẽ xử lý sự kiện đó.
- **Minh chứng mã nguồn:** Các lớp biểu diễn sự kiện `EndpointCheckedEvent`, `IncidentOpenedEvent` và `IncidentResolvedEvent`.

**Điểm đáng chú ý về học thuật:** Domain Event không chỉ được dùng để "cho hiện đại", mà giải quyết trực tiếp bài toán cắt coupling giữa quyết định nghiệp vụ và phản ứng hậu sự kiện. Trong đồ án này, việc phát hiện incident thuộc về miền nghiệp vụ; còn ghi log, gửi webhook hay mở rộng ra audit trail là những phản ứng có thể thay đổi theo thời gian. Event giúp hai mối quan tâm đó sống độc lập với nhau.

### 6.2.4. Observer Pattern (Mẫu Quan sát viên)
- **Vấn đề thực tế:** Trả lời cho câu hỏi: Ai là người lắng nghe tiếng "hét" của Domain Event ở trên để thực thi việc gửi thông báo mà không làm phiền tiến trình chính?
- **Giải pháp:** Thiết lập các "Quan sát viên" (Observers). Các đối tượng này đứng trong bóng tối, đăng ký lắng nghe một loại sự kiện cụ thể. Khi sự kiện `IncidentOpenedEvent` xuất hiện, Quan sát viên tự động thức dậy ở một tiểu trình (Thread) khác, thu thập danh sách email và âm thầm gửi đi.
- **Minh chứng mã nguồn:** Lớp `IncidentEventListener` sử dụng Annotation `@EventListener` của Spring để bắt sóng sự kiện và kích hoạt quy trình gửi cảnh báo.

**Phân biệt với Domain Event:** Domain Event là "thông điệp đã xảy ra". Observer là "cơ chế ai lắng nghe thông điệp đó". Hai mẫu thiết kế này bổ sung cho nhau nhưng không trùng nhau. Nêu rõ sự khác biệt này sẽ giúp phần bảo vệ đồ án vững hơn.

### 6.2.5. Adapter Pattern (Mẫu Bộ chuyển đổi)
- **Vấn đề thực tế:** Việc gửi cảnh báo phải tương thích với nhiều dịch vụ ngoại vi bên thứ ba (Third-party SDK) như Amazon SES, SendGrid, hay Slack. Mỗi SDK lại có một bộ hàm API khác nhau. Việc gọi thẳng SDK vào lõi hệ thống sẽ làm "ô nhiễm" mã nguồn.
- **Giải pháp:** Thiết kế các Bộ chuyển đổi (Adapter) bao bọc lấy các SDK ngoại vi. Các Adapter này biến đổi giao diện phức tạp của bên thứ ba thành một giao diện đơn giản và chuẩn hóa mà hệ thống mong muốn.
- **Minh chứng mã nguồn:** Interface `NotificationPort` định nghĩa hàm `sendIncidentAlert(...)`. Lớp `LogNotificationAdapter` đóng vai trò fallback, còn `WebhookNotificationAdapter` hiện thực hóa luồng gửi cảnh báo webhook thật mà không cần thay đổi Use Case lõi.

**Ý nghĩa mở rộng:** Adapter Pattern đặc biệt phù hợp với các hệ thống cần giao tiếp nhiều đối tác bên ngoài, vì bên ngoài luôn thay đổi giao diện. Hôm nay hệ thống gọi webhook đơn giản, ngày mai có thể phải ký HMAC, thêm retry policy hoặc chuyển sang Slack API riêng. Adapter là lớp hấp thụ toàn bộ sự biến động đó.

### 6.2.6. Lightweight Factory/Selection (Mẫu Xưởng chế tạo tinh gọn)
- **Vấn đề thực tế:** Với Strategy Pattern ở trên, làm sao để khởi tạo đúng Chiến lược tương ứng với CheckType của người dùng mà không cần viết một lớp Factory lồng ghép hàng loạt lệnh switch-case?
- **Giải pháp:** Tận dụng tối đa sức mạnh của Dependency Injection (DI) trong Spring Boot. Bằng cách khai báo một danh sách `List<HealthCheckExecutor>`, Spring sẽ tự động tìm tất cả các Bean kế thừa interface này và nhét vào danh sách. Hệ thống tạo ra một Factory tự động (Auto-discovery) vô cùng tinh gọn.
- **Minh chứng mã nguồn:** Bên trong lớp `MonitorEndpointService`, lập trình viên chỉ cần inject `List<HealthCheckExecutor> executors` và sử dụng Java Stream API để `filter(e -> e.supports(checkType)).findFirst()` lấy ra chiến lược chuẩn xác.

**Lợi ích:** Cách làm này giảm boilerplate nhưng vẫn giữ được tinh thần Factory. Quyết định lựa chọn implementation không bị hard-code vào Use Case, mà được ủy quyền cho cơ chế dependency injection của framework.

### 6.2.7. Facade-like Use Case Layer (Mẫu Mặt tiền)
- **Vấn đề thực tế:** Tiến trình giám sát mạng quá phức tạp. Nó phải gọi Database lấy danh sách, gọi Executor để ping, gọi Analyzer phân tích sự cố, và gọi EventPublisher để bắn sự kiện. Nếu Controller hoặc Scheduler phải gọi lần lượt từng dịch vụ này, mã nguồn của Client sẽ cực kỳ rối rắm.
- **Giải pháp:** Sử dụng các lớp Use Case Service làm "Mặt tiền" (Facade). Nó che giấu đi toàn bộ mức độ rối rắm của sự phối hợp bên dưới. Scheduler chỉ cần gọi đúng một hàm duy nhất, Facade sẽ lo liệu việc sai vặt (Orchestration) tất cả các thành phần còn lại.
- **Minh chứng mã nguồn:** Các lớp `ManageEndpointService`, `MonitorEndpointService`. Scheduler chỉ gọi hàm `runHealthCheckForAll()` một cách vô cùng thanh lịch.

**Điểm mạnh thiết kế:** Use Case Layer đóng vai trò mặt tiền không chỉ cho Controller mà còn cho cả Scheduler và Event Listener. Điều này làm cho các tác nhân bên ngoài luôn nói chuyện với hệ thống qua một số "cửa vào chuẩn hóa", tránh việc nhiều thành phần bên ngoài gọi chéo trực tiếp vào nhau.

### 6.2.8. DTO + Mapper Pattern (Mẫu Chuyển đổi Dữ liệu Xuyên ranh giới)
- **Vấn đề thực tế:** Khi REST API trả về dữ liệu cho Client, nếu ta mang nguyên đối tượng CSDL (JPA Entity) trả về, ta sẽ làm lộ cấu trúc bảng, lộ luôn những cột nhạy cảm như Mật khẩu hay Khóa ngoại. Ngoài ra, việc dùng chung một Model cho cả UI và DB sẽ dẫn đến thảm họa rạn nứt cấu trúc khi có thay đổi từ một phía.
- **Giải pháp:** Xây dựng các rào cản kiểm dịch dữ liệu bằng Data Transfer Object (DTO) và Mapper. Có 2 chiều bảo vệ: Dữ liệu từ API đi vào sẽ được `EndpointDtoMapper` chuyển thành Domain Model (Phục vụ lõi Use Case). Dữ liệu từ Domain khi muốn lưu xuống CSDL sẽ được `EndpointMapper` chuyển thành JPA Entity (Phục vụ Hibernate).
- **Minh chứng mã nguồn:** Sự hiện diện của các package `application.mapper` (phục vụ DTO) và `infrastructure.persistence.mapper` (phục vụ Database JPA Entity) tách biệt rành mạch, bảo vệ tuyệt đối bản thể gốc của Lõi nghiệp vụ.

**Ý nghĩa bảo mật và bảo trì:** DTO Pattern không chỉ giúp mã nguồn sạch, mà còn là lớp tường lửa dữ liệu. Nó ngăn việc vô tình lộ cột nội bộ, giảm nguy cơ phá vỡ API contract, và cho phép frontend/backend tiến hóa độc lập hơn.

## 6.3. Mối quan hệ giữa Design Patterns và SOLID

Giá trị của các Design Pattern không nên được nhìn rời rạc. Trên thực tế, chúng là công cụ để hiện thực hóa các nguyên lý SOLID:

- **Single Responsibility Principle:** `IncidentAnalyzer` chỉ phân tích sự cố; `WebhookNotificationAdapter` chỉ lo giao tiếp webhook; `IncidentController` chỉ nhận và trả HTTP.
- **Open/Closed Principle:** thêm `TcpHealthCheckExecutor`, `WebhookNotificationAdapter`, hoặc một notification adapter mới mà không cần sửa phần lõi nghiệp vụ.
- **Liskov Substitution Principle:** mọi `HealthCheckExecutor` phải có thể thay thế cho nhau qua interface chung.
- **Interface Segregation Principle:** Port như `NotificationPort`, `EndpointRepository`, `IncidentRepository` đều giữ interface nhỏ, đúng mục đích.
- **Dependency Inversion Principle:** Application phụ thuộc vào Port, không phụ thuộc trực tiếp vào class hạ tầng.

Việc liên hệ SOLID vào các lớp thực tế là điểm cộng rất lớn trong một báo cáo kỹ thuật, vì nó cho thấy đồ án không chỉ "dùng nhiều pattern" mà còn hiểu sâu mục đích của từng pattern trong thiết kế phần mềm.

## 6.4. Các quyết định kiến trúc nổi bật của đề tài

### 6.4.1. Tại sao Incident được xem là một thực thể nghiệp vụ riêng

Nếu coi incident chỉ là một cờ Boolean `isDown` gắn trên Endpoint, hệ thống sẽ rất đơn giản nhưng mất đi khả năng biểu diễn vòng đời sự cố. Dự án chọn mô hình Incident như một Entity riêng vì nó cần lưu:

- thời điểm bắt đầu,
- thời điểm phục hồi,
- nguyên nhân,
- mức độ nghiêm trọng,
- số lần lỗi tích lũy,
- danh sách kết quả kiểm tra liên quan.

Nhờ đó, hệ thống không chỉ trả lời được câu hỏi "endpoint hiện đang down hay không", mà còn trả lời được "đã có bao nhiêu đợt sự cố", "sự cố bắt đầu khi nào", "cảnh báo đã được kích hoạt ra sao". Đây là khác biệt quan trọng giữa giám sát mang tính tạm thời và giám sát mang tính quản trị vận hành.

### 6.4.2. Tại sao cảnh báo dùng Event-driven thay vì gọi trực tiếp

Nếu Use Case quét mạng gọi webhook trực tiếp, ba rủi ro sẽ xảy ra:

1. độ trễ của webhook làm chậm toàn bộ vòng quét,
2. lỗi webhook có thể làm hỏng luồng xử lý incident,
3. khó mở rộng thêm hành vi hậu sự kiện khác như audit log hay metrics.

Bằng kiến trúc event-driven, hệ thống chấp nhận thêm một lớp trung gian để đổi lấy sự tách bạch rõ ràng hơn. Đây là một đánh đổi rất hợp lý cho một phần mềm giám sát, nơi luồng phát hiện sự cố luôn phải được ưu tiên cao hơn luồng thông báo ra ngoài.

### 6.4.3. Tại sao phân quyền theo `X-Workspace-Id` là hợp lý

Trong một hệ thống nhiều workspace, chỉ kiểm tra JWT là chưa đủ. JWT chỉ cho biết user là ai, nhưng không cho biết user đang muốn hành động trong ngữ cảnh không gian nào. Việc bổ sung `X-Workspace-Id` giúp hệ thống biến quyền truy cập thành bài toán hai chiều:

- danh tính người dùng,
- ngữ cảnh tài nguyên.

Đây là một thiết kế rất phù hợp với các nền tảng quản trị nội bộ, vì cùng một user có thể giữ vai trò khác nhau ở những workspace khác nhau.

## 6.5. Kiểm soát kiến trúc bằng kiểm thử tự động

Một kiến trúc tốt sẽ không bền nếu chỉ được bảo vệ bằng lời hứa. Dự án sử dụng các lớp kiểm thử và công cụ chất lượng để biến kiến trúc thành các ràng buộc có thể thực thi được:

- **ArchUnit:** phát hiện import sai tầng, ví dụ Delivery phụ thuộc trực tiếp vào Infrastructure hoặc Domain phụ thuộc framework.
- **Unit Test cho Use Case:** xác minh orchestration ở tầng Application mà không cần khởi động cả hệ thống.
- **Integration Test cho Adapter:** chứng minh lớp Infrastructure thực sự nói chuyện đúng với JPA/DB.
- **Swagger/OpenAPI sync test:** đảm bảo contract API không bị lệch khỏi implementation.

Điểm hay của cách làm này là kiến trúc không còn là tài liệu "để đọc", mà trở thành thứ được kiểm tra mỗi lần build. Đây là một đặc điểm rất có giá trị trong môi trường phát triển nhóm.

## 6.6. Đánh giá tổng hợp về giá trị kiến trúc của dự án

Nhìn tổng thể, giá trị lớn nhất của kiến trúc dự án không nằm ở việc sử dụng thật nhiều thuật ngữ, mà ở chỗ các quyết định thiết kế đều phục vụ trực tiếp cho một vấn đề cụ thể:

- Clean Architecture để bảo vệ lõi nghiệp vụ.
- Ports & Adapters để chống phụ thuộc công nghệ.
- Strategy để mở rộng giao thức kiểm tra.
- Domain Event và Observer để tách incident khỏi notification.
- Repository để cô lập persistence.
- DTO/Mapper để bảo vệ ranh giới dữ liệu.

Sự kết hợp này giúp hệ thống đạt được bốn phẩm chất kiến trúc quan trọng: **dễ hiểu, dễ kiểm thử, dễ mở rộng, và khó mục nát theo thời gian**. Đó cũng chính là lý do chương này được xem là một trong hai chương trọng điểm của toàn bộ báo cáo, vì nó trả lời trực diện câu hỏi cốt lõi nhất của đồ án: *Tại sao hệ thống này được tổ chức như vậy, và tại sao cách tổ chức đó lại tốt?*
