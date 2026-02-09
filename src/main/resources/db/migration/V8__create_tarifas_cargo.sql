-- V8: Create tarifas_cargo table for salary rates by position

CREATE TABLE IF NOT EXISTS tarifas_cargo (
    id BIGSERIAL PRIMARY KEY,
    cargo VARCHAR(50) NOT NULL UNIQUE,
    monto_por_hora NUMERIC(10,2) NOT NULL DEFAULT 0,
    monto_por_dia NUMERIC(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert default rates for each cargo
INSERT INTO tarifas_cargo (cargo, monto_por_hora, monto_por_dia) VALUES
    ('Cajero', 2500.00, 18000.00),
    ('Barra', 2200.00, 16000.00),
    ('Seguridad', 2800.00, 20000.00),
    ('DJ', 3500.00, 25000.00),
    ('Promotor/a', 2000.00, 14000.00),
    ('RRHH', 3000.00, 22000.00),
    ('Administración', 3200.00, 24000.00);
