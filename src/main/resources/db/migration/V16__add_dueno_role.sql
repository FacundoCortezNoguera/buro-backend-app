-- V16: Add DUENO role for owner dashboard access

-- Add DUENO role
INSERT INTO roles (code, description) VALUES
    ('DUENO', 'Dueño - Acceso al dashboard ejecutivo')
ON CONFLICT (code) DO NOTHING;

-- Create dueno user with password 'admin123' (BCrypt hash)
-- Same password as other test users - change in production!
INSERT INTO users (username, password_hash, nombre, role_id, enabled)
VALUES (
    'dueno',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    'Dueño',
    (SELECT id FROM roles WHERE code='DUENO'),
    true
)
ON CONFLICT (username) DO NOTHING;
