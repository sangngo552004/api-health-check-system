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

1. Tao endpoint trong `API Health Check System` tro toi `http://localhost:8086/api/demo/health/toggle`
2. Dat policy timeout va failure threshold phu hop
3. Goi `POST /api/demo/control/mode` voi `OK`
4. Cho scheduler ghi nhan trang thai on dinh
5. Chuyen mode sang `ERROR` hoac `SLOW`
6. Cho incident mo va webhook duoc gui
7. Chuyen ve `OK`
8. Cho incident duoc resolve
