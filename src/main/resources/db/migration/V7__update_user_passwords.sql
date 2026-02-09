-- V7: Update user passwords to known values for testing
-- Password for both users: "admin123"
-- BCrypt hash generated with strength 10

UPDATE users SET password_hash = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE username = 'admin';

UPDATE users SET password_hash = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE username = 'rrhh';
