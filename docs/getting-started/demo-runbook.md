# Demo Runbook

Tai lieu nay la kich ban demo 5-7 phut de dung truoc hoi dong, uu tien de chay muot va co duong lui local neu moi truong online gap su co.

## 1. Chuan bi truoc buoi thi

Khoi dong local day du:

```powershell
docker compose up -d
cd backend
mvn spring-boot:run
```

Mo them hai terminal:

```powershell
cd frontend
npm run dev
```

```powershell
cd demo-service
mvn spring-boot:run
```

Neu muon doi mode nhanh trong luc demo:

```powershell
.\scripts\demo-set-mode.ps1 -Mode OK
.\scripts\demo-set-mode.ps1 -Mode ERROR
.\scripts\demo-set-mode.ps1 -Mode SLOW -SlowDelayMs 7000
```

## 2. Endpoint va policy de dung cho demo

Nen dung 1 endpoint chinh:

- `http://localhost:8086/api/demo/health/toggle`

Policy de nghi:

- `intervalSeconds`: 60 trong model, nhung de demo hien tai he thong dang chay theo scheduler global
- `timeoutMillis`: `3000`
- `failureThreshold`: `2`
- `expectedStatusCode`: `200`
- `latencyThresholdMillis`: `1500`

Voi cau hinh nay:

- mode `ERROR` se mo incident kha nhanh
- mode `SLOW` co the dua endpoint vao `DOWN` hoac `DEGRADED` tuy policy
- mode `OK` giup incident duoc resolve ro rang

## 3. Kich ban demo 5-7 phut

### Buoc 1. Mo bai toan

- Gioi thieu day la he thong giam sat API/service theo workspace
- Nhan manh co backend monitor, frontend dashboard va demo-service lam he thong dich

### Buoc 2. Dang nhap va chon workspace

- Dang nhap bang account demo
- Chon workspace co cau hinh endpoint monitor san

### Buoc 3. Cho thay dashboard

- Chi ra tong so endpoint
- Chi ra latency chart
- Chi ra active incidents board

### Buoc 4. Cho thay endpoint dich dang on dinh

- Dat demo-service ve `OK`
- Lam moi dashboard hoac danh sach endpoint
- Xac nhan endpoint dang on dinh

### Buoc 5. Tao su co co chu dich

- Chuyen demo-service sang `ERROR`
- Doi scheduler chay
- Vao dashboard hoac incidents de chi ra incident vua mo

### Buoc 6. Chung minh canh bao that

- Mo webhook receiver
- Cho thay payload webhook khi incident mo

### Buoc 7. Chung minh phuc hoi

- Chuyen demo-service ve `OK`
- Doi scheduler chay lai
- Lam moi dashboard/incidents
- Cho thay incident da `RESOLVED`
- Cho thay webhook recovery

## 4. Fallback local neu moi truong online loi

Neu ban online hoac webhook online co van de:

- Giữ frontend, backend, demo-service chay local
- Dung webhook receiver local hoac webhook.site neu con mang
- Neu webhook khong san sang, van co the demo incident mo/resolve va log notification o backend

## 5. URL can mo san truoc buoi thi

- Frontend app
- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/actuator/health`
- `http://localhost:8086/api/demo/control/state`
- trang webhook receiver
