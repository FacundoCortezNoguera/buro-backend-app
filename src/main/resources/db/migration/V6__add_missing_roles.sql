-- V6: Add missing roles (CAJA and SUPERVISOR)

INSERT INTO roles (code, description) VALUES
    ('CAJA', 'Caja - Gestión de pagos'),
    ('SUPERVISOR', 'Supervisor de turno')
ON CONFLICT (code) DO NOTHING;
