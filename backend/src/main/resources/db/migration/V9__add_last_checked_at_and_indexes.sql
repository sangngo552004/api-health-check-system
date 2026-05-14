-- V9: Thêm cột last_checked_at và index hỗ trợ query cho Core Executor

-- 1. Thêm cột last_checked_at vào monitored_endpoints
ALTER TABLE monitored_endpoints ADD COLUMN last_checked_at TIMESTAMP;

-- 2. Đánh index hỗ trợ truy vấn các endpoint cần check (lọc theo is_active)
CREATE INDEX idx_endpoint_is_active ON monitored_endpoints(is_active);

-- 3. Đánh index hỗ trợ truy vấn 10 kết quả gần nhất của một endpoint
CREATE INDEX idx_result_endpoint_checked_at ON health_check_results(endpoint_id, checked_at DESC);

-- 4. Đánh index hỗ trợ truy vấn incident đang mở của một endpoint
CREATE INDEX idx_incident_endpoint_status ON incidents(endpoint_id, status);
