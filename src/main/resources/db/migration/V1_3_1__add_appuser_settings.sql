-- Add app user settings table
CREATE TABLE app_user_setting (
  id BIGINT NOT NULL,
   uuid UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE,
   version INTEGER,
   app_user UUID NOT NULL,
   lang VARCHAR(255) NOT NULL,
   CONSTRAINT pk_appusersetting PRIMARY KEY (id)
);

ALTER TABLE app_user_setting ADD CONSTRAINT uc_appusersetting_app_user UNIQUE (app_user);

ALTER TABLE app_user_setting ADD CONSTRAINT uc_appusersetting_uuid UNIQUE (uuid);

CREATE INDEX idx_app_user_setting_appuser ON app_user_setting(app_user);

CREATE INDEX idx_app_user_setting_uuid ON app_user_setting(uuid);

-- Add extension to generate UUID v4 values
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Add a setting row for each already present app user (only Google app users)
ALTER SEQUENCE hibernate_sequence INCREMENT BY 1;

INSERT INTO app_user_setting (id, uuid, created_at, updated_at, version, app_user, lang)
SELECT nextval('hibernate_sequence'), uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, app_user_uuid, 'en'
FROM (SELECT DISTINCT uuid AS app_user_uuid FROM app_user) AS subquery;

ALTER SEQUENCE hibernate_sequence INCREMENT BY 50;

-- Remove the extension to generate UUID v4 values
DROP EXTENSION IF EXISTS "uuid-ossp";