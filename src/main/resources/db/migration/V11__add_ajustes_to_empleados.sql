-- Add salary adjustment columns to empleados table
ALTER TABLE empleados
ADD COLUMN IF NOT EXISTS ajuste_porcentaje DECIMAL(5,2) DEFAULT 0,
ADD COLUMN IF NOT EXISTS ajuste_monto DECIMAL(10,2) DEFAULT 0;

-- Set default values for existing records
UPDATE empleados SET ajuste_porcentaje = 0 WHERE ajuste_porcentaje IS NULL;
UPDATE empleados SET ajuste_monto = 0 WHERE ajuste_monto IS NULL;
