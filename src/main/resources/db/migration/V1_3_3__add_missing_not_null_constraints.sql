-- Add not null to version column for each table (primitives in Java cannot be null)
ALTER TABLE app_user_setting ALTER COLUMN version SET NOT NULL;

ALTER TABLE app_user_activation ALTER COLUMN version SET NOT NULL;