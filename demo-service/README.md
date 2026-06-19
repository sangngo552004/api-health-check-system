# Demo Service

`demo-service` la mot ung dung dich rat nho dung de `API Health Check System` giam sat trong luc demo hoac kiem thu.

## Muc tieu

Service nay giup tao ra cac tinh huong co kiem soat:

- endpoint luon `200 OK`
- endpoint phan hoi cham
- endpoint tra `500`
- endpoint luc duoc luc hong
- endpoint chuyen trang thai theo mode de mo/resolve incident chu dong

## Cach chay

```powershell
cd demo-service
mvn spring-boot:run
```

Mac dinh service chay tai `http://localhost:8086`.

## Cac endpoint de giam sat

- `GET /api/demo/health/ok`
- `GET /api/demo/health/slow`
- `GET /api/demo/health/error`
- `GET /api/demo/health/flaky`
- `GET /api/demo/health/toggle`

## Cac endpoint dieu khien demo

- `GET /api/demo/control/state`
- `POST /api/demo/control/mode`
- `POST /api/demo/control/reset`

### Vi du doi mode sang ERROR

```json
{
  "mode": "ERROR",
  "errorStatus": 500
}
```

### Vi du doi mode sang SLOW

```json
{
  "mode": "SLOW",
  "slowDelayMs": 7000
}
```

## Kich ban demo goi y

Muc tieu demo de xuyen suot 3 chuc nang chinh:

1. giam sat endpoint
2. mo incident khi rule kich hoat
3. gui mail thong bao va dong incident khi endpoint phuc hoi

### Buoc 1: Chay demo-service

```powershell
cd demo-service
mvn spring-boot:run
```

Service se chay tai `http://localhost:8086`.

### Buoc 2: Chay backend voi scheduler nhanh va bat mail

Can chay `API Health Check System` voi:

- scheduler tick nhanh, vi du `APP_SCHEDULER_INTERVAL_MS=5000`
- bat mail: `APP_NOTIFICATION_MAIL_ENABLED=true`
- cau hinh SMTP that neu muon gui mail demo thuc te

Neu khong bat mail, he thong van mo incident va ghi log notification, nhung khong gui email that.

### Buoc 3: Tao Policy

Tao mot `Check Policy` de monitor endpoint demo, vi du:

- `intervalSeconds = 15`
- `timeoutMillis = 3000`
- `retryCount = 0`
- `expectedStatusCode = 200`
- `degradedResponseTimeMillis = 1500`

Luu y:

- `Check Policy` chi dinh cach check
- incident se duoc mo boi `Alert Rule`, khong mo bang policy nua

### Buoc 4: Tao Alert Rule

Tao mot `Alert Rule` de mo incident khi endpoint loi, vi du:

- `ruleType = HTTP_STATUS_CODE`
- `operator = NE`
- `thresholdValue = 200`
- `severity = CRITICAL`
- gan `contactGroupIds` de gui email

Neu muon demo canh bao cham, co the tao them:

- `ruleType = RESPONSE_TIME`
- `operator = GTE`
- `thresholdValue = 1500`
- `severity = WARNING`

### Buoc 5: Tao Endpoint can monitor

Tao endpoint tro toi:

- `http://localhost:8086/api/demo/health/toggle`

Gan endpoint voi policy va alert rule vua tao.

### Buoc 6: Dat mode ban dau la OK

Goi:

```http
POST /api/demo/control/mode
Content-Type: application/json
```

```json
{
  "mode": "OK"
}
```

Cho 1-2 chu ky check de he thong ghi nhan endpoint dang `UP`.

### Buoc 7: Chuyen endpoint sang loi de mo incident

Goi:

```http
POST /api/demo/control/mode
Content-Type: application/json
```

```json
{
  "mode": "ERROR",
  "errorStatus": 500
}
```

Cho 1-2 chu ky check:

- endpoint se chuyen `DOWN`
- alert rule `HTTP_STATUS_CODE != 200` se kich hoat
- incident se duoc mo
- he thong se gui mail thong bao neu mail da bat

### Buoc 8: Chuyen ve OK de dong incident

Goi lai:

```http
POST /api/demo/control/mode
Content-Type: application/json
```

```json
{
  "mode": "OK"
}
```

Cho 1-2 chu ky check:

- endpoint tro lai `UP`
- incident hien tai se duoc dong
- he thong gui recovery notification

## Kich ban demo nhanh nhat

Neu thoi gian demo ngan, chi can:

1. cho thay endpoint dang `UP`
2. doi mode sang `ERROR`
3. cho thay incident `OPEN`
4. cho thay mail/log notification
5. doi mode ve `OK`
6. cho thay incident `RESOLVED`
