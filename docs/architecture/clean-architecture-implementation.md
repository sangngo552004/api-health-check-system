# Clean Architecture Implementation Guide

Tài liệu này đi rất sâu vào câu hỏi quan trọng nhất của dự án: `Clean Architecture` trong repo này đang được áp dụng cụ thể như thế nào ở mức code, mức package, mức transaction, mức mapping và mức business rule.

Đây là tài liệu nên dùng khi:

- bạn muốn giải thích cho đồng đội cách codebase được tổ chức
- bạn muốn viết phần “cách áp dụng kiến trúc” trong báo cáo
- bạn muốn review xem team có đang giữ đúng boundary kiến trúc hay không

## 1. Triết lý áp dụng trong dự án này

Dự án không áp dụng `Clean Architecture` như một khẩu hiệu. Nó đang cố gắng biến kiến trúc thành các quy tắc triển khai thực tế:

- business rule nằm gần `domain` và `application`, không nằm trong controller
- use case phụ thuộc `port`, không phụ thuộc JPA hay HTTP client cụ thể
- transaction không gắn bừa vào từng nơi, mà được áp ở biên của `application.usecase`
- mapping được tách theo đúng boundary
- Spring, database, scheduler, security được đẩy ra `infrastructure`

Nói cách khác, kiến trúc ở đây không chỉ là “chia folder”, mà là “chia trách nhiệm và chia hướng phụ thuộc”.

## 2. Các tầng trong dự án này thực sự làm gì

## 2.1. Domain

`domain` là nơi đặt `nghiệp vụ cốt lõi` và mô hình nghiệp vụ thuần.

Nó gồm:

- entity như `MonitoredEndpoint`, `CheckPolicy`, `Incident`, `Workspace`
- value object như `CheckType`, `EndpointStatus`, `UserRole`
- domain event như `IncidentOpenedEvent`
- domain service như `IncidentAnalyzer`

### Domain được phép làm gì

- giữ trạng thái nghiệp vụ
- biểu diễn rule nội tại của object
- thực hiện logic thuần không phụ thuộc framework
- định nghĩa ngôn ngữ nghiệp vụ của hệ thống

### Domain không nên làm gì

- gọi repository trực tiếp
- biết về HTTP request/response
- biết về JPA entity
- biết về annotation của Spring trừ khi thật sự bắt buộc
- điều phối transaction

### Ví dụ trong repo

`MonitoredEndpoint` có các hành vi:

- `initializeForCreation(...)`
- `applyUpdates(...)`
- `markChecked(...)`

Đây là business behavior gắn với bản thân endpoint. Nó không nên bị đẩy vào controller.

`CheckPolicy` có các hàm:

- `effectiveFailureThreshold()`
- `effectiveTimeoutMillis()`
- `effectiveExpectedStatusCode()`
- `hasLatencyThreshold()`

Đây là rule nội tại của policy, rất phù hợp để ở domain.

`IncidentAnalyzer` là ví dụ đẹp cho `domain service`: nó phân tích kết quả kiểm tra để quyết định mở hay đóng incident mà không cần biết database, controller hay framework.

## 2.2. Application

`application` là nơi đặt `use case orchestration`.

Tầng này trả lời câu hỏi:

- hệ thống cần làm gì để hoàn thành một nghiệp vụ
- cần gọi những dependency nào
- thứ tự xử lý ra sao
- khi nào ném lỗi nghiệp vụ

### Application được phép làm gì

- nhận input ở dạng command/DTO hoặc tham số use case
- gọi repository qua port
- gọi domain behavior
- kết hợp nhiều dependency để hoàn thành một use case
- kiểm tra rule liên quan tới ngữ cảnh thực thi
- trả DTO phù hợp cho tầng delivery

### Application không nên làm gì

- phụ thuộc trực tiếp vào JPA repository
- thao tác trực tiếp với entity persistence
- nhúng logic hạ tầng như HTTP client implementation chi tiết
- trộn business logic vào annotation framework quá nhiều

### Ví dụ trong repo

`ManageEndpointService` là ví dụ điển hình.

Nó:

- lấy policy qua `CheckPolicyRepository`
- kiểm tra policy có cùng workspace hay không
- map command sang domain object
- gọi hành vi domain để khởi tạo hoặc cập nhật
- lưu bằng `EndpointRepository`
- map domain sang response DTO

Điểm quan trọng là:

- nó không biết JPA
- nó không biết table
- nó không biết JSON response shape của persistence
- nó điều phối use case, không đóng vai trò domain object

## 2.3. Infrastructure

`infrastructure` là nơi triển khai các chi tiết kỹ thuật cụ thể.

Nó gồm:

- JPA repository
- persistence adapter
- mapper domain <-> JPA entity
- security JWT
- scheduler
- executor HTTP/TCP
- event listener
- transaction config
- bean wiring

### Infrastructure được phép làm gì

- phụ thuộc Spring
- phụ thuộc JPA
- phụ thuộc database
- phụ thuộc protocol/network library
- triển khai port của application

### Infrastructure không nên làm gì

- tự quyết định nghiệp vụ lõi thay cho domain
- biến adapter thành nơi gom business rule nặng

## 2.4. Delivery

`delivery` là lớp giao tiếp với bên ngoài.

Trong repo này, nó chủ yếu là REST controller và exception handling.

### Delivery được phép làm gì

- nhận request
- validate
- lấy context như path variable, header, authenticated user
- gọi input port
- trả response

### Delivery không nên làm gì

- nhúng rule monitor, incident, policy vào controller
- truy cập thẳng database
- giữ transaction logic riêng

## 3. Phân biệt nghiệp vụ của Domain và nghiệp vụ của Use Case

Đây là phần rất quan trọng vì nhiều nhóm hay trộn hai thứ này.

## 3.1. Nghiệp vụ của Domain là gì

Đó là logic gắn với bản chất của thực thể hoặc khái niệm nghiệp vụ.

Ví dụ:

- endpoint được map từ `CheckStatus` sang `EndpointStatus` như thế nào
- policy mặc định timeout/failure threshold là bao nhiêu
- khi nào một chuỗi health check được coi là đủ điều kiện mở incident
- severity incident nên là `INFO`, `WARNING`, `CRITICAL`

Những rule này đúng ngay cả khi:

- gọi từ REST API
- gọi từ CLI
- gọi từ scheduler
- gọi từ test

Nó không phụ thuộc vào ai gọi nó.

## 3.2. Nghiệp vụ của Use Case là gì

Đó là logic điều phối để hoàn thành một mục tiêu nghiệp vụ trong một bối cảnh cụ thể.

Ví dụ trong `ManageEndpointService.createEndpoint(...)`:

- tải policy theo `policyId`
- kiểm tra policy thuộc đúng workspace
- map command thành domain object
- khởi tạo thông tin tạo mới
- lưu endpoint
- trả DTO

Đây không phải “bản chất nội tại” của endpoint, mà là “cách hệ thống thực hiện thao tác tạo endpoint”.

## 3.3. Cách nhớ nhanh

- `Domain`: object này là gì, nó hành xử ra sao theo bản chất nghiệp vụ
- `Use case`: để hoàn thành yêu cầu này, hệ thống phải phối hợp những bước nào

## 4. Transaction đang được áp dụng như thế nào

Đây là một điểm rất đáng giá của dự án và bạn hoàn toàn nên nhấn mạnh trong báo cáo.

## 4.1. Dự án này không rải `@Transactional` bừa trong use case

Thay vào đó, transaction được cấu hình tập trung trong:

- `infrastructure.config.UseCaseTransactionConfig`

Tại đây, AOP được cấu hình để áp transaction cho:

- mọi method public trong `com.example.apihealthchecksystem.application.usecase..*`

Điều này có nghĩa là:

- transaction boundary nằm ở `application use case layer`
- use case không phải tự lo annotation transaction từng method
- kiến trúc thể hiện rõ “application là nơi điều phối unit of work”

## 4.2. Quy tắc read-only và write transaction

Config hiện tại map theo tên method:

- `get*`, `find*`, `list*`, `count*` -> read-only transaction
- mọi method còn lại -> write transaction

Đây là một cách làm rất hay để minh họa rằng:

- transaction là concern của application boundary
- policy transaction có thể được áp theo convention
- không cần để từng use case class mang quá nhiều chi tiết kỹ thuật

## 4.3. Vì sao không đặt transaction ở controller

Nếu transaction nằm ở controller:

- delivery layer sẽ nắm concern không thuộc về nó
- khó tái sử dụng use case ngoài HTTP
- boundary nghiệp vụ bị lệch ra ngoài

## 4.4. Vì sao không đặt transaction ở domain

Domain phải thuần và không nên phụ thuộc vào cách persistence hay framework hoạt động.

Nếu domain biết transaction:

- domain sẽ bị kéo gần infrastructure
- khó test độc lập
- sai tinh thần Clean Architecture

## 4.5. Vì sao không để repository adapter tự quyết transaction cho cả nghiệp vụ

Repository có thể tham gia transaction do application mở, nhưng repository không nên tự đại diện cho toàn bộ unit of work nghiệp vụ.

Ví dụ:

- tạo endpoint có thể cần kiểm tra policy, validate workspace, rồi mới save
- monitor endpoint có thể cần save result, update endpoint, save incident, publish event

Đó là orchestration của use case, không phải của repository đơn lẻ.

## 5. Mapper trong dự án này được tách như thế nào

Đây cũng là điểm rất đáng nói vì nhiều codebase thường trộn hết mapper vào một chỗ.

## 5.1. Mapper ở application

Ví dụ:

- `application.mapper.EndpointDtoMapper`

Vai trò:

- map từ request command sang domain
- map từ domain sang response DTO

Nó phục vụ boundary giữa:

- use case
- API contract

Tức là nó là mapper cho `application-facing representation`.

## 5.2. Mapper ở infrastructure

Ví dụ:

- `infrastructure.persistence.mapper.EndpointMapper`

Vai trò:

- map giữa `MonitoredEndpointJpaEntity` và `MonitoredEndpoint`

Nó phục vụ boundary giữa:

- domain
- persistence model

Tức là nó là mapper cho `persistence-facing representation`.

## 5.3. Vì sao phải tách hai loại mapper này

Vì chúng giải quyết hai loại chuyển đổi hoàn toàn khác nhau:

- DTO mapper: phục vụ giao tiếp use case/API
- persistence mapper: phục vụ giao tiếp domain/database

Nếu gộp lại:

- boundary bị mờ
- mapper dễ phình to
- domain vô tình bị kéo gần JPA entity hoặc REST DTO

## 5.4. Nguyên tắc thực hành

- DTO không được thay domain model
- JPA entity không được tràn vào application
- domain model không bị trả thẳng ra ngoài nếu API cần contract ổn định hơn

## 6. Bean wiring và dependency injection phục vụ kiến trúc thế nào

`UseCaseConfig` trong `infrastructure.config` là nơi tạo các use case bean.

Điều này cho thấy:

- application classes không cần tự gắn `@Service` để tồn tại
- infrastructure chịu trách nhiệm “cắm dây” các implementation cụ thể
- use case có thể được khởi tạo từ các dependency abstraction

Đây là điểm rất đẹp để giải thích trong báo cáo:

- application định nghĩa cần gì
- infrastructure quyết định cấp cho nó implementation nào

## 7. Những quy tắc phụ thuộc đang được bảo vệ bằng test

Repo có `CleanArchitectureTest` dùng ArchUnit.

Nó kiểm tra layer dependency theo hướng:

- `Delivery` không bị layer khác truy cập ngược
- `Application` chỉ được truy cập bởi `Delivery` và `Infrastructure`
- `Domain` chỉ được truy cập bởi `Application` và `Infrastructure`
- `Infrastructure` không bị layer khác truy cập ngược như một API public

Điểm quan trọng ở đây không chỉ là “có test”, mà là:

- kiến trúc được biến thành quy tắc kiểm chứng được
- team không chỉ nói bằng miệng là dùng Clean Architecture

## 8. Use case nào đang thể hiện rõ kiến trúc nhất

## 8.1. ManageEndpointService

Điểm mạnh:

- orchestration rõ
- validate workspace access ở tầng application
- domain object tự xử lý trạng thái của nó
- repository là port
- DTO mapper tách riêng

## 8.2. MonitorEndpointService

Đây là use case đẹp nhất để trình bày kiến trúc vì nó có đủ:

- chọn strategy executor
- gọi repository port
- cập nhật domain
- lưu persistence
- phát event
- gọi domain service `IncidentAnalyzer`

Nó cho thấy rất rõ:

- use case không chỉ CRUD
- use case là nơi điều phối nhiều concern nghiệp vụ

## 9. Quy ước nên giữ cho team trong tương lai

## 9.1. Ở domain

Nên:

- giữ logic thuần
- thêm hành vi nội tại của entity tại đây
- đặt value object và domain service ở đây

Không nên:

- inject Spring bean
- gọi repository
- dùng JPA entity làm domain model

## 9.2. Ở application

Nên:

- orchestration use case
- gọi port
- kiểm tra business rule theo ngữ cảnh use case
- trả DTO

Không nên:

- dùng `JpaRepository` trực tiếp
- viết query SQL/JPA ở đây
- trộn logic HTTP vào đây

## 9.3. Ở infrastructure

Nên:

- triển khai repository/executor/notification/security
- giữ mapper persistence tại đây
- cấu hình transaction, bean wiring, framework integration tại đây

Không nên:

- cướp business rule từ domain/application

## 9.4. Ở delivery

Nên:

- mỏng
- rõ contract
- chuyển request vào use case

Không nên:

- xử lý monitor logic
- điều phối repository trực tiếp

## 10. Những điểm chưa hoàn hảo nhưng đáng phân tích

Đây là phần rất tốt cho báo cáo vì nó thể hiện tư duy phản biện, không thần thánh hóa hệ thống.

### 10.1. Infrastructure vẫn có quyền chạm vào domain

Điều này là chấp nhận được trong kiến trúc kiểu adapter, nhưng phải cẩn thận để infrastructure chỉ “chuyển đổi và triển khai”, không “chiếm quyền quyết định nghiệp vụ”.

### 10.2. Một số business rule vẫn nằm ở application thay vì domain

Ví dụ:

- `validateWorkspaceAccess(...)` hiện nằm trong use case

Điều này không sai, vì đây là rule gắn với ngữ cảnh thao tác và authorization hơn là bản chất nội tại của entity. Nhưng đây cũng là điểm đáng thảo luận trong team để giữ consistency.

### 10.3. ArchUnit rule mới đang kiểm layer tổng quát

Đây là nền tốt, nhưng tương lai có thể bổ sung:

- cấm application phụ thuộc Spring Data
- cấm domain phụ thuộc package delivery
- cấm use case annotation framework không mong muốn

## 11. Bộ docs nào còn cần thiết để hỗ trợ phần này

Ngoài file này, các tài liệu rất nên có và hiện dự án đã có hoặc nên tiếp tục mở rộng:

- `architecture.md`: bức tranh tổng quan
- `architecture-and-patterns.md`: giải thích pattern
- `sequence-flows.md`: luồng nghiệp vụ
- `business-requirements.md`: góc nhìn BA
- `glossary.md`: thống nhất ngôn ngữ

Nếu muốn đi xa hơn nữa cho báo cáo, rất nên bổ sung thêm:

- tài liệu `decision-records` cho các quyết định kiến trúc lớn
- sơ đồ Mermaid cho layer dependency và monitoring flow
- guideline coding rule theo từng layer

## 12. Kết luận

Điểm hay nhất của dự án này thực sự nằm ở cách nó cố gắng áp `Clean Architecture` vào code chứ không chỉ vào slide:

- use case là application boundary
- transaction đặt ở biên use case qua config AOP
- mapper được tách theo boundary
- domain giữ rule thuần
- infrastructure giữ công nghệ cụ thể
- architecture được kiểm bằng test

Nếu bạn muốn nhấn mạnh một “linh hồn” của dự án trong báo cáo, thì đây chính là phần nên được đầu tư nhất.
