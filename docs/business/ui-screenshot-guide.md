# UI Screenshot Guide

Tài liệu này được tạo để bạn dễ chuẩn bị ảnh giao diện cho:

- báo cáo đồ án
- slide thuyết trình
- tài liệu demo
- README hoặc portfolio

Mục tiêu của file này là:

- liệt kê đúng các màn hình nên chụp
- chỉ ra những trạng thái UI nào đáng chụp
- ghi ngắn gọn ý nghĩa của từng ảnh
- chừa sẵn chỗ để bạn tự dán ảnh sau

Bạn không cần chụp tất cả nếu thời gian gấp, nhưng nếu muốn một bộ ảnh đẹp và đầy đủ cho báo cáo thì nên đi gần hết danh sách này.

## Cách dùng tài liệu này

Với mỗi mục bên dưới, bạn có thể:

1. mở đúng màn hình
2. chụp ảnh
3. dán ảnh vào phần `Screenshot`
4. nếu muốn, thêm chú thích ngắn ở phần `Notes`

Bạn có thể thay dòng:

```md
![TODO](../assets/ui/ten-anh.png)
```

bằng ảnh thật sau này.

## Quy ước đặt tên ảnh gợi ý

Để dễ quản lý, bạn có thể tạo thư mục ảnh riêng, ví dụ:

- `docs/assets/ui/`

Và đặt tên theo kiểu:

- `login-page.png`
- `dashboard-overview.png`
- `endpoint-list.png`
- `endpoint-create-form.png`
- `policy-list.png`

## Bộ ảnh ưu tiên cao nhất

Nếu chỉ có thời gian chụp một số ảnh quan trọng nhất, nên ưu tiên:

1. login
2. dashboard overview
3. endpoints list
4. endpoint create/edit form
5. policies list + form
6. alerts list + form
7. contacts list + form
8. members list + add member form

## 1. Authentication Screens

## 1.1. Login Page

### Mục đích

Cho thấy người dùng đăng nhập vào hệ thống trước khi truy cập dashboard.

### Nên chụp gì

- toàn bộ màn hình login
- ô username/password
- nút đăng nhập
- visual style tổng thể

### Gợi ý caption

“Giao diện đăng nhập của hệ thống giám sát API Health Check System.”

### Screenshot

![TODO](../assets/ui/login-page.png)

### Notes

- ...

## 1.2. Register Page

### Mục đích

Cho thấy hệ thống đã có UI cho đăng ký/onboarding, dù backend flow hiện chưa hoàn thiện đầy đủ.

### Nên chụp gì

- form đăng ký
- các field chính như username, email, phone, password

### Gợi ý caption

“Giao diện đăng ký tài khoản ở mức frontend của hệ thống.”

### Screenshot

![TODO](../assets/ui/register-page.png)

### Notes

- Có thể ghi rõ trong báo cáo rằng UI đã có nhưng backend register API còn là phần mở rộng tiếp theo.

## 2. Workspace Context

## 2.1. Workspace Selection / Active Workspace Context

### Mục đích

Cho thấy ứng dụng làm việc theo ngữ cảnh workspace.

### Nên chụp gì

- phần header hoặc vị trí hiển thị workspace hiện tại
- nếu có dropdown chọn workspace thì chụp luôn trạng thái đó

### Gợi ý caption

“Ngữ cảnh workspace hiện hành dùng để phân vùng dữ liệu và phân quyền truy cập.”

### Screenshot

![TODO](../assets/ui/workspace-context.png)

### Notes

- ...

## 3. Dashboard Screens

## 3.1. Dashboard Overview

### Mục đích

Đây là ảnh quan trọng nhất của toàn bộ frontend vì nó thể hiện mục tiêu chính của hệ thống.

### Nên chụp gì

- các stat card tổng quan
- tên workspace đang xem
- nút refresh
- layout tổng thể

### Gợi ý caption

“Dashboard tổng quan hiển thị nhanh tình trạng vận hành của các endpoint trong workspace.”

### Screenshot

![TODO](../assets/ui/dashboard-overview.png)

### Notes

- ...

## 3.2. Dashboard Latency Chart

### Mục đích

Cho thấy hệ thống không chỉ hiển thị trạng thái hiện tại mà còn theo dõi xu hướng độ trễ.

### Nên chụp gì

- biểu đồ latency
- legend của các endpoint nếu có

### Gợi ý caption

“Biểu đồ độ trễ của các endpoint tiêu biểu trong workspace.”

### Screenshot

![TODO](../assets/ui/dashboard-latency-chart.png)

### Notes

- ...

## 3.3. Active Incidents Board

### Mục đích

Cho thấy dashboard có khả năng hiển thị sự cố đang mở.

### Nên chụp gì

- danh sách incident đang active
- số lượng incident mở
- trạng thái severity nếu có

### Gợi ý caption

“Bảng tổng hợp các incident đang mở trong workspace.”

### Screenshot

![TODO](../assets/ui/dashboard-active-incidents.png)

### Notes

- Nếu chưa có data đẹp, có thể seed hoặc tạo incident trước khi chụp.

## 4. Endpoint Management Screens

## 4.1. Endpoint List

### Mục đích

Cho thấy khả năng quản lý danh sách endpoint được giám sát.

### Nên chụp gì

- bảng hoặc list endpoint
- trạng thái từng endpoint
- nút tạo/sửa/xóa nếu hiển thị
- search box nếu có

### Gợi ý caption

“Màn hình quản lý danh sách endpoint trong workspace.”

### Screenshot

![TODO](../assets/ui/endpoint-list.png)

### Notes

- ...

## 4.2. Endpoint Create Form

### Mục đích

Cho thấy người dùng có thể cấu hình endpoint mới để hệ thống monitor.

### Nên chụp gì

- field name
- URL
- method
- environment
- check type
- tags
- checkbox active

### Gợi ý caption

“Biểu mẫu tạo mới endpoint cần giám sát.”

### Screenshot

![TODO](../assets/ui/endpoint-create-form.png)

### Notes

- ...

## 4.3. Endpoint Edit Form

### Mục đích

Cho thấy endpoint có thể được cập nhật sau khi tạo.

### Nên chụp gì

- form ở trạng thái edit
- dữ liệu mẫu đã được đổ sẵn

### Gợi ý caption

“Biểu mẫu cập nhật cấu hình endpoint hiện có.”

### Screenshot

![TODO](../assets/ui/endpoint-edit-form.png)

### Notes

- ...

## 4.4. Endpoint Status Visual

### Mục đích

Cho thấy hệ thống có phân biệt trạng thái `UP`, `DEGRADED`, `DOWN`.

### Nên chụp gì

- vài endpoint với màu trạng thái khác nhau nếu có thể

### Gợi ý caption

“Màu sắc và trạng thái hiển thị trực quan cho endpoint.”

### Screenshot

![TODO](../assets/ui/endpoint-status-visual.png)

### Notes

- Nếu khó tạo đủ 3 trạng thái, chỉ cần chụp trạng thái có dữ liệu đẹp nhất.

## 5. Policy Management Screens

## 5.1. Policy List

### Mục đích

Cho thấy khả năng quản lý rule kiểm tra cho endpoint.

### Nên chụp gì

- danh sách policy
- các thông số nổi bật như timeout, threshold, interval

### Gợi ý caption

“Màn hình quản lý các check policy cho hệ thống monitor.”

### Screenshot

![TODO](../assets/ui/policy-list.png)

### Notes

- ...

## 5.2. Policy Create/Edit Form

### Mục đích

Cho thấy người dùng có thể định nghĩa rule kỹ thuật cho health check.

### Nên chụp gì

- interval
- timeout
- retry count
- failure threshold
- latency threshold
- expected status code
- response regex

### Gợi ý caption

“Biểu mẫu cấu hình check policy áp dụng cho endpoint.”

### Screenshot

![TODO](../assets/ui/policy-form.png)

### Notes

- Đây là ảnh rất tốt để giải thích nghiệp vụ của `CheckPolicy`.

## 6. Alert Rule Screens

## 6.1. Alert Rule List

### Mục đích

Cho thấy hệ thống có lớp cấu hình cảnh báo riêng.

### Nên chụp gì

- danh sách alert rule
- rule type
- operator
- threshold

### Gợi ý caption

“Màn hình quản lý các rule cảnh báo theo ngưỡng.”

### Screenshot

![TODO](../assets/ui/alert-rule-list.png)

### Notes

- ...

## 6.2. Alert Rule Form

### Mục đích

Cho thấy người dùng có thể cấu hình rule cảnh báo.

### Nên chụp gì

- name
- rule type
- operator
- threshold value
- contact group ids
- isActive

### Gợi ý caption

“Biểu mẫu tạo/cập nhật alert rule cho hệ thống.”

### Screenshot

![TODO](../assets/ui/alert-rule-form.png)

### Notes

- ...

## 7. Contact Group Screens

## 7.1. Contact Group List

### Mục đích

Cho thấy hệ thống có khả năng gom người nhận cảnh báo theo nhóm.

### Nên chụp gì

- danh sách contact group
- tên nhóm
- mô tả

### Gợi ý caption

“Màn hình quản lý contact group phục vụ cảnh báo.”

### Screenshot

![TODO](../assets/ui/contact-group-list.png)

### Notes

- ...

## 7.2. Contact Group Form

### Mục đích

Cho thấy cấu hình contact group có thể chứa user, email và webhook.

### Nên chụp gì

- name
- description
- userIds
- emailAddresses
- webhookUrls

### Gợi ý caption

“Biểu mẫu cấu hình nhóm người nhận cảnh báo.”

### Screenshot

![TODO](../assets/ui/contact-group-form.png)

### Notes

- ...

## 8. Member Management Screens

## 8.1. Members List

### Mục đích

Cho thấy hệ thống hỗ trợ quản lý thành viên trong workspace.

### Nên chụp gì

- danh sách member
- role của từng member
- nút thêm/xóa nếu hiển thị

### Gợi ý caption

“Màn hình quản lý thành viên và quyền hạn trong workspace.”

### Screenshot

![TODO](../assets/ui/members-list.png)

### Notes

- ...

## 8.2. Add Member Form

### Mục đích

Cho thấy workspace admin có thể thêm thành viên và gán role.

### Nên chụp gì

- userId input
- role selector

### Gợi ý caption

“Biểu mẫu thêm thành viên mới vào workspace.”

### Screenshot

![TODO](../assets/ui/add-member-form.png)

### Notes

- ...

## 9. Placeholder / Incomplete Screens

## 9.1. Incidents Placeholder Screen

### Mục đích

Dùng nếu bạn muốn trung thực trong báo cáo rằng module incident UI riêng vẫn đang là phần đang phát triển.

### Nên chụp gì

- màn hình placeholder incidents

### Gợi ý caption

“Màn hình incidents ở trạng thái placeholder, là phần dự kiến hoàn thiện tiếp theo.”

### Screenshot

![TODO](../assets/ui/incidents-placeholder.png)

### Notes

- Nếu không muốn nhấn mạnh phần chưa xong trong báo cáo chính, bạn có thể bỏ ảnh này.

## 10. UX / Interaction Cases nên cân nhắc chụp thêm

Các ảnh dưới đây không bắt buộc, nhưng rất đẹp nếu muốn làm báo cáo chỉn chu hơn.

## 10.1. Loading State

### Mục đích

Cho thấy hệ thống có phản hồi khi đang tải dữ liệu.

### Screenshot

![TODO](../assets/ui/loading-state.png)

### Notes

- ...

## 10.2. Empty State

### Mục đích

Cho thấy hệ thống xử lý khi chưa có dữ liệu.

### Screenshot

![TODO](../assets/ui/empty-state.png)

### Notes

- ...

## 10.3. Error State

### Mục đích

Cho thấy cách UI phản ứng khi API lỗi hoặc khi người dùng thiếu quyền.

### Screenshot

![TODO](../assets/ui/error-state.png)

### Notes

- ...

## 10.4. Success Interaction

### Mục đích

Cho thấy thao tác CRUD hoàn tất thành công.

### Screenshot

![TODO](../assets/ui/success-interaction.png)

### Notes

- ...

## 11. Bộ ảnh tối thiểu cho báo cáo nếu thời gian gấp

Nếu bạn cần nộp nhanh, chỉ cần chụp tối thiểu:

1. Login Page
2. Dashboard Overview
3. Latency Chart
4. Endpoint List
5. Endpoint Form
6. Policy List hoặc Policy Form
7. Alert Rule List hoặc Form
8. Contact Group List hoặc Form
9. Members List

## 12. Gợi ý trình bày trong báo cáo

Bạn có thể chia ảnh UI trong báo cáo thành 4 nhóm:

### Nhóm 1. Authentication

- login
- register

### Nhóm 2. Monitoring Dashboard

- dashboard overview
- latency chart
- active incidents

### Nhóm 3. Configuration Screens

- endpoint
- policy
- alert rule
- contact group

### Nhóm 4. Workspace Administration

- member management
- workspace context

## 13. Gợi ý chụp ảnh cho đẹp hơn

- dùng dữ liệu seed hoặc dữ liệu demo có tên rõ ràng
- tránh để màn hình quá trống
- ưu tiên chụp khi có 2-4 item đẹp mắt trong danh sách
- nếu có thể, dùng cùng một workspace demo cho toàn bộ ảnh
- chụp ở độ phân giải đủ rõ để đưa vào báo cáo A4 hoặc slide

## 14. Liên kết nên đọc cùng

- [Project Overview](project-overview.md)
- [Business Requirements](business-requirements.md)
- [Sequence Flows](sequence-flows.md)
- [Frontend Engineering Notes](../engineering/frontend.md)
