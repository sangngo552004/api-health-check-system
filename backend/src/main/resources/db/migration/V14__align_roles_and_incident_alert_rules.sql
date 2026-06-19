ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

UPDATE users
SET role = 'ADMIN'
WHERE role = 'SUPER_ADMIN';

ALTER TABLE users
    ADD CONSTRAINT users_role_check
    CHECK (role IN ('ADMIN', 'USER'));

CREATE TABLE IF NOT EXISTS incident_triggered_alert_rules (
    incident_id BIGINT NOT NULL,
    alert_rule_id BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_incident_triggered_alert_rules_incident_id
    ON incident_triggered_alert_rules (incident_id);
