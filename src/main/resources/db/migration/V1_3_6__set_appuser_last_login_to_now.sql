-- Set the last login to now for each app user
UPDATE app_user SET last_login_at = CURRENT_TIMESTAMP;