-- V4: Create turnos_noche table for night shift records

CREATE TABLE IF NOT EXISTS turnos_noche (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleados(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_salida TIME,
    horas_trabajadas NUMERIC(5,2),
    monto_calculado NUMERIC(10,2),
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(empleado_id, fecha)
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_turnos_noche_fecha ON turnos_noche(fecha);
CREATE INDEX IF NOT EXISTS idx_turnos_noche_empleado ON turnos_noche(empleado_id);
CREATE INDEX IF NOT EXISTS idx_turnos_noche_fecha_empleado ON turnos_noche(fecha, empleado_id);
