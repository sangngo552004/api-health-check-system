# Project Status

Tài liệu này mô tả trạng thái thực tế của code tại thời điểm hiện tại, không phải trạng thái mong muốn.

## Overall

Hệ thống đang ở mức `MVP đang phát triển`.

- Lõi backend đã chạy được end-to-end cho monitoring cơ bản.
- Frontend đã có dashboard và nhiều màn CRUD thật.
- Một số luồng quan trọng vẫn còn partial hoặc mới dừng ở mức scaffolding.

## Những gì đã ổn

| Hạng mục | Trạng thái | Ghi chú |
|---|---|---|
| JWT auth | `implemented` | Có login và refresh |
| Workspace & membership | `implemented` | Có phân quyền theo workspace |
| Endpoint management | `implemented` | Có CRUD backend + frontend |
| Check policy management | `implemented` | Có CRUD backend + frontend |
| Alert rule management | `implemented` | Có CRUD backend + frontend |
| Contact group management | `implemented` | Có CRUD backend + frontend |
| Dashboard summary | `implemented` | Có stats + latency chart |
| HTTP health check | `implemented` | Có kiểm tra body, regex, latency |
| TCP health check | `implemented` | Có executor và test riêng |
| Incident open/resolve | `implemented` | Theo failure threshold và kết quả gần nhất |

## Những gì đang partial

| Hạng mục | Trạng thái | Ghi chú |
|---|---|---|
| Notification delivery | `partial` | Mới log, chưa gửi email/webhook thật |
| Alert routing | `partial` | Luồng hiện tại lấy contact group theo workspace |
| Scheduler policy usage | `partial` | Có `intervalSeconds` trong policy nhưng chưa lập lịch riêng |
| Frontend register flow | `partial` | Có UI nhưng chưa có backend API |
| Incident UI/API | `partial` | Dashboard có incident summary, chưa có module incidents hoàn chỉnh |

## Những gì chưa có

| Hạng mục | Trạng thái | Ghi chú |
|---|---|---|
| User self-registration | `missing` | Chưa có endpoint backend |
| Incident history management | `missing` | Chưa có list/filter/detail riêng |
| Real notification adapters | `missing` | Chưa có email, Slack, webhook adapter production-ready |
| Scheduler HA | `missing` | Chưa có distributed coordination |
| Maintenance window | `missing` | Chưa có domain support |

## Rủi ro tài liệu cần ghi nhớ

- Một số migration seed và placeholder UI phục vụ demo hơn là production.
- Một vài text trong frontend chưa phản ánh đúng việc module đã được nối API thật.
- Worktree hiện đang tiếp tục được phát triển, nên tài liệu này nên được cập nhật cùng với các thay đổi API lớn.
