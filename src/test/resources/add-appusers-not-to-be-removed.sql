-- The following app user should not be removed because it has logged in yesterday
INSERT INTO app_user
VALUES (
  1,
  '9d46a12b-e242-4d87-bb0d-8ab16a9e4d81',
  now() - interval '33 days', -- createdAt
  now() - interval '33 days', -- updatedAt
  1, -- entity version
  NULL, -- authProviderId
  'DAILY',
  'appuser1@email.com',
  '$2a$10$LGPBmqCrMhBqfmj9XyAp8.M4rUUUKhDabwD8N4uB2hBbLpgNMqsHe',
  'FirstName1',
  'LastName1',
  TRUE, -- enabled
  now() - interval '1 day' -- lastLoginAt
);
INSERT INTO app_user_removal_information
VALUES (
  1,
  'd209d9f6-a620-4646-a009-9b205320bb30',
  now() - interval '33 days', -- createdAt
  now() - interval '33 days', -- updatedAt
  1, -- entity version
  1, -- AppUser id
  NULL, -- notifiedAt
  0 -- failures
);

-- The following app user should not be removed because even if it did not log in in the recent past times, it has not been notified yet because of failures
INSERT INTO app_user
VALUES (
  2,
  '109ba03b-f2e3-4028-8308-d0aa971926ec',
  now() - interval '33 days', -- createdAt
  now() - interval '33 days', -- updatedAt
  1, -- entity version
  NULL, -- authProviderId
  'DAILY',
  'appuser2@email.com',
  '$2a$10$LGPBmqCrMhBqfmj9XyAp8.M4rUUUKhDabwD8N4uB2hBbLpgNMqsHe',
  'FirstName2',
  'LastName2',
  TRUE, -- enabled
  now() - interval '33 days' -- lastLoginAt
);
INSERT INTO app_user_removal_information
VALUES (
  2,
  '07dda613-b5ea-40d7-82c5-02eff6bafc29',
  now() - interval '33 days', -- createdAt
  now() - interval '1 day', -- updatedAt
  2, -- entity version
  2, -- AppUser id
  NULL, -- notifiedAt
  2 -- failures
);

-- The following app user should not be removed because even if it did not log in in the recent past times and it has been notified, the time interval between removal notification and removal has not passed yet
INSERT INTO app_user
VALUES (
  3,
  'c16d55fd-38a8-4023-96c9-7e02697cba59',
  now() - interval '33 days', -- createdAt
  now() - interval '31 days', -- updatedAt
  2, -- entity version
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
  'f046896e-2311-4003-9d39-18cd23381e94',
  now() - interval '33 days', -- createdAt
  now() - interval '1 day', -- updatedAt
  2, -- entity version
  3, -- AppUser id
  now() - interval '31 days', -- notifiedAt
  2 -- failures
);