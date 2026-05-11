-- V4: Align schema với Template-based Policy và bổ sung các cột còn thiếu

-- 1. Chuyển check_policies thành Template độc lập (xóa endpoint_id constraint)
ALTER TABLE check_policies DROP CONSTRAINT IF EXISTS fk_check_policy_endpoint;
ALTER TABLE check_policies DROP COLUMN IF EXISTS endpoint_id;
ALTER TABLE check_policies ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE check_policies ADD COLUMN IF NOT EXISTS expected_status_code INTEGER;
ALTER TABLE check_policies ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE check_policies ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE check_policies ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 2. Thêm các cột còn thiếu vào monitored_endpoints
ALTER TABLE monitored_endpoints ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'UP';
ALTER TABLE monitored_endpoints ADD COLUMN IF NOT EXISTS policy_id BIGINT;
ALTER TABLE monitored_endpoints ADD COLUMN IF NOT EXISTS request_body TEXT;
ALTER TABLE monitored_endpoints ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE monitored_endpoints DROP COLUMN IF EXISTS expected_status_code;

-- 3. Tạo bảng endpoint_headers (thay thế kiểu lưu header trong endpoint)
CREATE TABLE IF NOT EXISTS endpoint_headers (
    endpoint_id BIGINT NOT NULL,
    header_key VARCHAR(255) NOT NULL,
    header_value VARCHAR(1024),
    CONSTRAINT fk_eh_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE
);

-- 4. Tạo bảng endpoint_tags
CREATE TABLE IF NOT EXISTS endpoint_tags (
    endpoint_id BIGINT NOT NULL,
    tag VARCHAR(100),
    CONSTRAINT fk_et_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE
);

-- 5. Bổ sung các cột còn thiếu vào incidents
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS failure_count INTEGER DEFAULT 0;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS severity VARCHAR(20) DEFAULT 'WARNING';
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS root_cause TEXT;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 6. Tạo bảng incident_failing_results để truy vết
CREATE TABLE IF NOT EXISTS incident_failing_results (
    incident_id BIGINT NOT NULL,
    result_id BIGINT NOT NULL,
    CONSTRAINT fk_ifr_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE,
    CONSTRAINT fk_ifr_result FOREIGN KEY (result_id) REFERENCES health_check_results(id) ON DELETE CASCADE
);

-- 7. Bổ sung cột còn thiếu vào health_check_results
ALTER TABLE health_check_results ADD COLUMN IF NOT EXISTS response_payload TEXT;

-- 8. Tạo bảng notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT,
    channel VARCHAR(50) NOT NULL,
    recipient VARCHAR(512) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE SET NULL
);
