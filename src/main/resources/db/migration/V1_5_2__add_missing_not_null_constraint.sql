-- Add not null to version column for app user password reset table (primitives in Java cannot be null)
ALTER TABLE app_user_password_reset ALTER COLUMN version SET NOT NULL;