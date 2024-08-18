-- Add money table
CREATE TABLE money (
  id BIGINT NOT NULL,
   uuid UUID NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE,
   version INTEGER NOT NULL,
   operation_date date NOT NULL,
   amount DECIMAL NOT NULL,
   operation_type VARCHAR(255) NOT NULL,
   description VARCHAR(255),
   app_user_id BIGINT NOT NULL,
   CONSTRAINT pk_money PRIMARY KEY (id)
);

CREATE TABLE money_tag (
  money_id BIGINT NOT NULL,
   tag_id BIGINT NOT NULL,
   CONSTRAINT pk_moneytag PRIMARY KEY (money_id, tag_id)
);

ALTER TABLE money ADD CONSTRAINT uc_money_uuid UNIQUE (uuid);

CREATE INDEX idx_money_uuid ON money(uuid);

ALTER TABLE money ADD CONSTRAINT FK_MONEY_ON_APP_USER FOREIGN KEY (app_user_id) REFERENCES app_user (id);

ALTER TABLE money_tag ADD CONSTRAINT fk_moneytag_on_money FOREIGN KEY (money_id) REFERENCES money (id);

ALTER TABLE money_tag ADD CONSTRAINT fk_moneytag_on_tag FOREIGN KEY (tag_id) REFERENCES tag (id);