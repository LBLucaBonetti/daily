-- Set the "increment by" value of the Hibernate sequence to 50 to make it use the pooled optimizer
-- to generate next values; this configuration is reflected in @SequenceGenerator.allocationSize of
-- BaseEntity.
ALTER SEQUENCE hibernate_sequence INCREMENT BY 50;