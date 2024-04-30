-- Add the new column indicating the last login
ALTER TABLE app_user ADD COLUMN last_login_at TIMESTAMP WITHOUT TIME ZONE;