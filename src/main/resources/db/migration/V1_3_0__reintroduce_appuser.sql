-- Reintroduce table with constraints and indexes
CREATE TABLE app_user (
  id BIGINT NOT NULL,
  uuid UUID NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  version INTEGER NOT NULL,
  auth_provider_id TEXT,
  auth_provider VARCHAR(255) NOT NULL,
  email VARCHAR(320) NOT NULL,
  password TEXT,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  enabled BOOLEAN NOT NULL,
  CONSTRAINT pk_app_user PRIMARY KEY (id)
);

ALTER TABLE app_user ADD CONSTRAINT uc_appuser_email UNIQUE (email);

ALTER TABLE app_user ADD CONSTRAINT uc_appuser_uuid UNIQUE (uuid);

ALTER TABLE app_user ADD CONSTRAINT uc_appuser_auth_provider_id_auth_provider UNIQUE (auth_provider_id, auth_provider);

CREATE INDEX idx_appuser_uuid ON app_user(uuid);

-- Add extension to generate UUID v4 values
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users
INSERT INTO app_user (id, uuid, created_at, updated_at, version, auth_provider_id, auth_provider, email, enabled)
SELECT nextval('hibernate_sequence'), uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, app_user, 'GOOGLE', uuid_generate_v4() || '@trydaily.click', TRUE
FROM (SELECT DISTINCT app_user FROM note) AS subquery;

-- Remove the extension to generate UUID v4 values
DROP EXTENSION IF EXISTS "uuid-ossp";

-- Add a temporary column to note and tag tables to populate with the uuid of the user
ALTER TABLE note ADD COLUMN app_user_temp UUID;

ALTER TABLE tag ADD COLUMN app_user_temp UUID;

UPDATE note n
SET app_user_temp = ( SELECT au.uuid FROM app_user au WHERE au.auth_provider_id = n.app_user);

UPDATE tag t
SET app_user_temp = ( SELECT au.uuid FROM app_user au WHERE au.auth_provider_id = t.app_user);

-- Remove the old app_user to swap it with the new temporary (the type has changed from VARCHAR to UUID)
ALTER TABLE note DROP COLUMN app_user;

ALTER TABLE tag DROP COLUMN app_user;

ALTER TABLE note RENAME COLUMN app_user_temp TO app_user;

ALTER TABLE tag RENAME COLUMN app_user_temp TO app_user;

ALTER TABLE note ALTER COLUMN app_user SET NOT NULL;

ALTER TABLE tag ALTER COLUMN app_user SET NOT NULL;

-- Add indexes

CREATE INDEX idx_note_appuser ON note(app_user);

CREATE INDEX idx_tag_appuser ON tag(app_user);

-- Add not null to version column for each table (primitives in Java cannot be null)
ALTER TABLE tag ALTER COLUMN version SET NOT NULL;

ALTER TABLE note ALTER COLUMN version SET NOT NULL;