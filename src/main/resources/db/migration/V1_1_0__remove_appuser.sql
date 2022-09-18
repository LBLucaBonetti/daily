-- Add new app_user column to note and tag
-- Copy from app_user.uid to note.app_user and tag.app_user
ALTER TABLE note ADD COLUMN app_user VARCHAR(255);

UPDATE note SET app_user = (
  SELECT au.uid FROM note n1 JOIN app_user au ON n1.app_user_id = au.id
  WHERE n1.id = note.id
);

ALTER TABLE note ALTER COLUMN app_user SET NOT NULL;

ALTER TABLE tag ADD COLUMN app_user VARCHAR(255);

UPDATE tag SET app_user = (
  SELECT au.uid FROM tag t1 JOIN app_user au ON t1.app_user_id = au.id
  WHERE t1.id = tag.id
);

ALTER TABLE tag ALTER COLUMN app_user SET NOT NULL;

-- Drop app_user table and related connections
ALTER TABLE note DROP CONSTRAINT FK_NOTE_ON_APPUSERID;

ALTER TABLE tag DROP CONSTRAINT FK_TAG_ON_APPUSERID;

ALTER TABLE app_user DROP CONSTRAINT uc_appuser_email;

ALTER TABLE app_user DROP CONSTRAINT uc_appuser_uid;

ALTER TABLE app_user DROP CONSTRAINT uc_appuser_uuid;

ALTER TABLE note DROP COLUMN app_user_id;

ALTER TABLE tag DROP COLUMN app_user_id;

DROP INDEX idx_appuser_uuid;

DROP TABLE app_user;