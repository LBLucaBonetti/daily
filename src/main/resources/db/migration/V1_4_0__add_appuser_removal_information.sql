-- Add app user removal information table
CREATE TABLE app_user_removal_information (
  id BIGINT NOT NULL,
   uuid UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE,
   version INTEGER NOT NULL,
   app_user_id BIGINT,
   notified_at TIMESTAMP WITHOUT TIME ZONE,
   failures INTEGER NOT NULL,
   CONSTRAINT pk_app_user_removal_information PRIMARY KEY (id)
);

ALTER TABLE app_user_removal_information ADD CONSTRAINT uc_appuserremovalinformation_app_user UNIQUE (app_user_id);

ALTER TABLE app_user_removal_information ADD CONSTRAINT uc_appuserremovalinformation_uuid UNIQUE (uuid);

ALTER TABLE app_user_removal_information ALTER COLUMN app_user_id SET NOT NULL;

CREATE INDEX idx_app_user_removal_information_uuid ON app_user_removal_information(uuid);

ALTER TABLE app_user_removal_information ADD CONSTRAINT FK_app_user_removal_information_ON_APP_USER FOREIGN KEY (app_user_id) REFERENCES app_user (id);