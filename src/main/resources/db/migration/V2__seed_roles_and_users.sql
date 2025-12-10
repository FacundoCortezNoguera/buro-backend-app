-- Roles técnicos para el sistema
INSERT INTO roles (code, description) VALUES
                                          ('ADMIN', 'Administrador del sistema'),
                                          ('RRHH',  'Recursos Humanos'),
                                          ('MOZO',  'Mozo'),
                                          ('BARRA', 'Barra');

-- OJO: estos hashes son de ejemplo. Podés reemplazar con los tuyos.
INSERT INTO users (username, password_hash, nombre, role_id, enabled)
VALUES
    ('admin', '$2a$10$PqT1Zg0gT7A5VbWqyp5G1eQ8v1a3t8xYf3Qf0Cvq7c8cW4GgQ3C9m',
     'Administrador', (SELECT id FROM roles WHERE code='ADMIN'), true),

    ('rrhh',  '$2a$10$H4L9nHj0xqJvD5tQ6k5rhu2k9JYwZQyX8tKfL8mJ8sBbnT8f7z.1W',
     'RRHH Turnos',   (SELECT id FROM roles WHERE code='RRHH'), true);