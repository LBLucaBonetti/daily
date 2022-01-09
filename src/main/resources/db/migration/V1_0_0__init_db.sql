CREATE SEQUENCE  IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE app_user (
  id BIGINT NOT NULL,
  uuid UUID NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  version INTEGER,
  uid VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT pk_appuser PRIMARY KEY (id)
);

CREATE TABLE note (
  id BIGINT NOT NULL,
  uuid UUID NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  version INTEGER,
  text VARCHAR(255) NOT NULL,
  app_user_id BIGINT,
  CONSTRAINT pk_note PRIMARY KEY (id)
);

CREATE TABLE note_tag (
  note_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  CONSTRAINT pk_notetag PRIMARY KEY (note_id, tag_id)
);

CREATE TABLE tag (
  id BIGINT NOT NULL,
  uuid UUID NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  version INTEGER,
  name VARCHAR(255) NOT NULL,
  color_hex VARCHAR(255) NOT NULL,
  app_user_id BIGINT,
  CONSTRAINT pk_tag PRIMARY KEY (id)
);

ALTER TABLE app_user ADD CONSTRAINT uc_appuser_email UNIQUE (email);

ALTER TABLE app_user ADD CONSTRAINT uc_appuser_uid UNIQUE (uid);

ALTER TABLE app_user ADD CONSTRAINT uc_appuser_uuid UNIQUE (uuid);

ALTER TABLE note ADD CONSTRAINT uc_note_uuid UNIQUE (uuid);

ALTER TABLE tag ADD CONSTRAINT uc_tag_uuid UNIQUE (uuid);

CREATE INDEX idx_appuser_uuid ON app_user(uuid);

CREATE INDEX idx_note_uuid ON note(uuid);

CREATE INDEX idx_tag_uuid ON tag(uuid);

ALTER TABLE note ADD CONSTRAINT FK_NOTE_ON_APPUSERID FOREIGN KEY (app_user_id) REFERENCES app_user (id);

ALTER TABLE tag ADD CONSTRAINT FK_TAG_ON_APPUSERID FOREIGN KEY (app_user_id) REFERENCES app_user (id);

ALTER TABLE note_tag ADD CONSTRAINT fk_notetag_on_note FOREIGN KEY (note_id) REFERENCES note (id);

ALTER TABLE note_tag ADD CONSTRAINT fk_notetag_on_tag FOREIGN KEY (tag_id) REFERENCES tag (id);