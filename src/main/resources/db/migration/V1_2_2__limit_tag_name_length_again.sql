-- Limit the tag name lenght to 30 characters
ALTER TABLE tag ALTER COLUMN name TYPE VARCHAR(30);