-- Add extension to generate UUID v4 values
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Add removal information for each app user
ALTER SEQUENCE hibernate_sequence INCREMENT BY 1;

INSERT INTO app_user_removal_information (id, uuid, created_at, updated_at, version, app_user_id, notified_at, failures)
SELECT nextval('hibernate_sequence'), uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, app_user_id, NULL, 0
FROM (SELECT DISTINCT id AS app_user_id FROM app_user) AS subquery;

ALTER SEQUENCE hibernate_sequence INCREMENT BY 50;

-- Remove the extension to generate UUID v4 values
DROP EXTENSION IF EXISTS "uuid-ossp";