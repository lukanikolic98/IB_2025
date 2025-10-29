INSERT INTO users (email, firstname, lastname, password, role, activated)
VALUES ('lukanikolic98@hotmail.com', 'Luka', 'Nikolic', '$2a$10$6e80QpI4vnPYC8kN1wxut.1ZxX/2mGCD3.xRHOYm.5oojvQ9KRVlm', 'ROLE_ADMIN', true);
INSERT INTO refresh_tokens (token, user_id, expiry_date)
VALUES (
    'f47ac10b-58cc-4372-a567-0e02b2c3d479',
    1,
    '2025-11-29T00:00:00'
);
