-- Add app user activations table
CREATE TABLE app_user_activation (
  id BIGINT NOT NULL,
   uuid UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE,
   version INTEGER,
   app_user_id BIGINT,
   activation_code UUID NOT NULL,
   expired_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   activated_at TIMESTAMP WITHOUT TIME ZONE,
   CONSTRAINT pk_app_user_activation PRIMARY KEY (id)
);

ALTER TABLE app_user_activation ADD CONSTRAINT uc_appuseractivation_app_user UNIQUE (app_user_id);

ALTER TABLE app_user_activation ADD CONSTRAINT uc_appuseractivation_activation_code UNIQUE (activation_code);

ALTER TABLE app_user_activation ADD CONSTRAINT uc_appuseractivation_uuid UNIQUE (uuid);

CREATE INDEX idx_app_user_activation_uuid ON app_user_activation(uuid);

ALTER TABLE app_user_activation ADD CONSTRAINT FK_APP_USER_ACTIVATION_ON_APP_USER FOREIGN KEY (app_user_id) REFERENCES app_user (id);

-- Add extension to generate UUID v4 values
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Add activation for each app user with DAILY auth provider
ALTER SEQUENCE hibernate_sequence INCREMENT BY 1;

INSERT INTO app_user_activation (id, uuid, created_at, updated_at, version, app_user_id, activation_code, expired_at, activated_at)
SELECT nextval('hibernate_sequence'), uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, app_user_id, uuid_generate_v4(), CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP
FROM (SELECT DISTINCT id AS app_user_id FROM app_user WHERE auth_provider = 'DAILY') AS subquery;

ALTER SEQUENCE hibernate_sequence INCREMENT BY 50;

-- Remove the extension to generate UUID v4 values
DROP EXTENSION IF EXISTS "uuid-ossp";