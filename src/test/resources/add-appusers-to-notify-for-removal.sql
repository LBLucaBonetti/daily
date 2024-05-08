-- The following app users should be candidate for removal notification since their creation date is before many days ago and their last login is null
INSERT INTO app_user
VALUES (
  1,
  '9d46a12b-e242-4d87-bb0d-8ab16a9e4d81',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  NULL, -- authProviderId
  'DAILY',
  'appuser1@email.com',
  '$2a$10$LGPBmqCrMhBqfmj9XyAp8.M4rUUUKhDabwD8N4uB2hBbLpgNMqsHe',
  'FirstName1',
  'LastName1',
  TRUE, -- enabled
  NULL -- lastLoginAt
);
INSERT INTO app_user_removal_information
VALUES (
  1,
  'd209d9f6-a620-4646-a009-9b205320bb30',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  1, -- AppUser id
  NULL, -- notifiedAt
  0 -- failures
);

INSERT INTO app_user
VALUES (
  2,
  'c26bb4a3-b2f8-4bb8-8481-b5804f0b3088',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  NULL, -- authProviderId
  'GOOGLE',
  'appuser2@gmail.com',
  NULL, -- password
  'FirstName2',
  'LastName2',
  TRUE, -- enabled
  NULL -- lastLoginAt
);
INSERT INTO app_user_removal_information
VALUES (
  2,
  'f046896e-2311-4003-9d39-18cd23381e94',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  2, -- AppUser id
  NULL, -- notifiedAt
  0 -- failures
);

-- The following app users should be candidate for removal notification since their creation date and last login are before many days ago
INSERT INTO app_user
VALUES (
  3,
  'de638a41-bd2a-4247-815a-fcec35c70918',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  NULL, -- authProviderId
  'DAILY',
  'appuser3@email.com',
  '$2a$10$LGPBmqCrMhBqfmj9XyAp8.M4rUUUKhDabwD8N4uB2hBbLpgNMqsHe',
  'FirstName3',
  'LastName3',
  TRUE, -- enabled
  now() - interval '31 days' -- lastLoginAt
);
INSERT INTO app_user_removal_information
VALUES (
  3,
  '109ba03b-f2e3-4028-8308-d0aa971926ec',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  3, -- AppUser id
  NULL, -- notifiedAt
  0 -- failures
);

INSERT INTO app_user
VALUES (
  4,
  '07dda613-b5ea-40d7-82c5-02eff6bafc29',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  NULL, -- authProviderId
  'GOOGLE',
  'appuser4@gmail.com',
  NULL, -- password
  'FirstName4',
  'LastName4',
  TRUE, -- enabled
  now() - interval '31 days' -- lastLoginAt
);
INSERT INTO app_user_removal_information
VALUES (
  4,
  'c16d55fd-38a8-4023-96c9-7e02697cba59',
  now() - interval '31 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  1, -- entity version
  4, -- AppUser id
  NULL, -- notifiedAt
  0 -- failures
);