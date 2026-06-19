ALTER TABLE check_policies
    RENAME COLUMN latency_threshold_millis TO degraded_response_time_millis;

ALTER TABLE check_policies
    ALTER COLUMN interval_seconds SET DEFAULT 60;

ALTER TABLE check_policies
    ALTER COLUMN timeout_millis SET DEFAULT 5000;

ALTER TABLE check_policies
    ALTER COLUMN retry_count SET DEFAULT 0;

ALTER TABLE check_policies
    ALTER COLUMN expected_status_code SET DEFAULT 200;

ALTER TABLE check_policies
    DROP COLUMN IF EXISTS failure_threshold;

ALTER TABLE alert_rules
    ALTER COLUMN operator DROP NOT NULL;

ALTER TABLE alert_rules
    ADD COLUMN IF NOT EXISTS severity VARCHAR(20);

UPDATE alert_rules
SET severity = CASE
    WHEN rule_type = 'STATUS_CODE_MISMATCH' THEN 'CRITICAL'
    WHEN rule_type = 'RESPONSE_TIME_EXCEEDED' THEN 'WARNING'
    WHEN rule_type = 'CONSECUTIVE_FAILURE' THEN 'WARNING'
    ELSE COALESCE(severity, 'WARNING')
END
WHERE severity IS NULL;

ALTER TABLE alert_rules
    ALTER COLUMN severity SET NOT NULL;

ALTER TABLE alert_rules
    DROP COLUMN IF EXISTS override_default_contacts;

UPDATE alert_rules
SET rule_type = 'RESPONSE_TIME'
WHERE rule_type = 'RESPONSE_TIME_EXCEEDED';

UPDATE alert_rules
SET rule_type = 'HTTP_STATUS_CODE'
WHERE rule_type = 'STATUS_CODE_MISMATCH';

UPDATE alert_rules
SET operator = NULL
WHERE rule_type = 'CONSECUTIVE_FAILURE';
