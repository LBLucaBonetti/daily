-- Remove previous index
DROP INDEX idx_app_user_setting_appuser;

DROP INDEX idx_note_appuser;

DROP INDEX idx_tag_appuser;

-- Add new foreign key column to tables
ALTER TABLE app_user_setting ADD COLUMN app_user_temp BIGINT;

ALTER TABLE note ADD COLUMN app_user_temp BIGINT;

ALTER TABLE tag ADD COLUMN app_user_temp BIGINT;

-- Update foreign keys before swapping columns
UPDATE app_user_setting aus
SET app_user_temp = (
  SELECT au.id FROM app_user AS au WHERE au.uuid = aus.app_user
);

UPDATE note n
SET app_user_temp = (
  SELECT au.id FROM app_user AS au WHERE au.uuid = n.app_user
);

UPDATE tag t
SET app_user_temp = (
  SELECT au.id FROM app_user AS au WHERE au.uuid = t.app_user
);

-- Delete the old columns
ALTER TABLE app_user_setting DROP COLUMN app_user;

ALTER TABLE note DROP COLUMN app_user;

ALTER TABLE tag DROP COLUMN app_user;

-- Rename columns
ALTER TABLE app_user_setting RENAME COLUMN app_user_temp TO app_user_id;

ALTER TABLE note RENAME COLUMN app_user_temp TO app_user_id;

ALTER TABLE tag RENAME COLUMN app_user_temp TO app_user_id;

-- Add not null to foreign key referencing app_user
ALTER TABLE app_user_setting ALTER COLUMN app_user_id SET NOT NULL;

ALTER TABLE app_user_activation ALTER COLUMN app_user_id SET NOT NULL;

ALTER TABLE note ALTER COLUMN app_user_id SET NOT NULL;

ALTER TABLE tag ALTER COLUMN app_user_id SET NOT NULL;

-- Add foreign key constraints
ALTER TABLE app_user_setting ADD CONSTRAINT FK_APP_USER_SETTING_ON_APP_USER FOREIGN KEY (app_user_id) REFERENCES app_user (id);

ALTER TABLE note ADD CONSTRAINT FK_NOTE_ON_APPUSERID FOREIGN KEY (app_user_id) REFERENCES app_user (id);

ALTER TABLE tag ADD CONSTRAINT FK_TAG_ON_APPUSERID FOREIGN KEY (app_user_id) REFERENCES app_user (id);

-- Add foreign key unique constraint for app_user_setting
ALTER TABLE app_user_setting ADD CONSTRAINT uc_appusersetting_app_user UNIQUE (app_user_id);