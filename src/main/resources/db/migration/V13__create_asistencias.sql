-- Tabla de asistencias/presencias diarias
CREATE TABLE IF NOT EXISTS asistencias (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleados(id),
    fecha DATE NOT NULL,
    hora_llegada TIME NOT NULL,
    hora_esperada TIME DEFAULT '22:00',
    minutos_diferencia INTEGER,  -- positivo = tarde, negativo = temprano
    estado VARCHAR(20) NOT NULL, -- 'TEMPRANO', 'PUNTUAL', 'TARDE'
    registrado_por VARCHAR(60),  -- username del admin que registró
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(empleado_id, fecha)  -- Un empleado solo puede tener una asistencia por día
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_asistencias_fecha ON asistencias(fecha);
CREATE INDEX IF NOT EXISTS idx_asistencias_empleado ON asistencias(empleado_id);
CREATE INDEX IF NOT EXISTS idx_asistencias_estado ON asistencias(estado);
