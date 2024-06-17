-- Add app user password resets table
CREATE TABLE app_user_password_reset (
  id BIGINT NOT NULL,
   uuid UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE,
   version INTEGER,
   app_user_id BIGINT,
   password_reset_code UUID NOT NULL,
   expired_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   CONSTRAINT pk_app_user_password_reset PRIMARY KEY (id)
);

ALTER TABLE app_user_password_reset ADD CONSTRAINT uc_appuserpasswordreset_app_user UNIQUE (app_user_id);

ALTER TABLE app_user_password_reset ADD CONSTRAINT uc_appuserpasswordreset_password_reset_code UNIQUE (password_reset_code);

ALTER TABLE app_user_password_reset ADD CONSTRAINT uc_appuserpasswordreset_uuid UNIQUE (uuid);

CREATE INDEX idx_app_user_password_reset_uuid ON app_user_password_reset(uuid);

ALTER TABLE app_user_password_reset ADD CONSTRAINT FK_APP_USER_PASSWORD_RESET_ON_APP_USER FOREIGN KEY (app_user_id) REFERENCES app_user (id);