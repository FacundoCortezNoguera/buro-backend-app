-- V3: Update empleados schema with additional fields

-- Add new columns to empleados table
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS telefono VARCHAR(20);
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS cargo VARCHAR(50);
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS cobra_por_hora NUMERIC(10,2);
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS cobra_por_dia NUMERIC(10,2);
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS horas_por_dia INTEGER DEFAULT 8;

-- Create table for employee work days (many-to-many relationship)
CREATE TABLE IF NOT EXISTS empleado_dias_trabajo (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleados(id) ON DELETE CASCADE,
    dia_semana VARCHAR(15) NOT NULL, -- LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
    UNIQUE(empleado_id, dia_semana)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_empleado_dias_trabajo_empleado ON empleado_dias_trabajo(empleado_id);
