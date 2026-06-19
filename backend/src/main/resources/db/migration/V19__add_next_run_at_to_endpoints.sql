ALTER TABLE monitored_endpoints
    ADD COLUMN IF NOT EXISTS next_run_at TIMESTAMP;

UPDATE monitored_endpoints e
SET next_run_at = CASE
    WHEN e.last_checked_at IS NULL THEN CURRENT_TIMESTAMP
    ELSE e.last_checked_at + make_interval(secs => COALESCE(p.interval_seconds, 60))
END
FROM check_policies p
WHERE e.policy_id = p.id
  AND e.next_run_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_monitored_endpoints_active_next_run
    ON monitored_endpoints (is_active, next_run_at);
