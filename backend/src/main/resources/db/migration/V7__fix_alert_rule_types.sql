-- V7: Cập nhật lại giá trị AlertRuleType cho khớp với Enum trong code

UPDATE alert_rules SET rule_type = 'RESPONSE_TIME_EXCEEDED' WHERE rule_type = 'LATENCY';
UPDATE alert_rules SET rule_type = 'STATUS_CODE_MISMATCH' WHERE rule_type = 'STATUS';
UPDATE alert_rules SET rule_type = 'STATUS_CODE_MISMATCH' WHERE rule_type = 'BODY';
