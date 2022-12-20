-- Limit the tag name lenght to 31 characters
ALTER TABLE tag ALTER COLUMN name TYPE VARCHAR(31);