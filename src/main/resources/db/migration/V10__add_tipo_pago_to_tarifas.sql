-- V10: Add tipo_pago column to tarifas_cargo

ALTER TABLE tarifas_cargo ADD COLUMN IF NOT EXISTS tipo_pago VARCHAR(10) NOT NULL DEFAULT 'DIA';

-- Update existing cargos with their payment type
-- Mantenimiento cobra por hora, el resto por día
UPDATE tarifas_cargo SET tipo_pago = 'DIA' WHERE cargo IN ('Cajero', 'Barra', 'Seguridad', 'DJ', 'Promotor/a', 'RRHH', 'Administración');

-- Add Mantenimiento cargo that pays by hour
INSERT INTO tarifas_cargo (cargo, monto_por_hora, monto_por_dia, tipo_pago)
VALUES ('Mantenimiento', 1800.00, 0, 'HORA')
ON CONFLICT (cargo) DO NOTHING;
