-- =====================================================
-- V14: Destinatarios de notificaciones + Cambios de empleado
-- =====================================================

-- Tabla de destinatarios de notificaciones (reemplaza EMAIL_REPORTES en configuracion_sistema)
CREATE TABLE IF NOT EXISTS destinatarios_notificacion (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    recibe_cierre_noche BOOLEAN NOT NULL DEFAULT FALSE,
    recibe_reportes_mensuales BOOLEAN NOT NULL DEFAULT FALSE,
    recibe_cambios_empleados BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_destinatarios_email
    ON destinatarios_notificacion(email);

CREATE INDEX IF NOT EXISTS idx_destinatarios_activo
    ON destinatarios_notificacion(activo);

-- Migrar el email existente de configuracion_sistema si hay alguno configurado
INSERT INTO destinatarios_notificacion (nombre, email, activo, recibe_cierre_noche, recibe_reportes_mensuales, recibe_cambios_empleados)
SELECT 'Destinatario migrado', cs.valor, TRUE, TRUE, TRUE, TRUE
FROM configuracion_sistema cs
WHERE cs.clave = 'EMAIL_REPORTES'
  AND cs.valor IS NOT NULL
  AND cs.valor != '';

-- Tabla de cambios de empleado pendientes de aprobación
CREATE TABLE IF NOT EXISTS cambios_empleado (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleados(id),
    tipo_cambio VARCHAR(50) NOT NULL,
    campo_modificado VARCHAR(100),
    valor_anterior TEXT,
    valor_nuevo TEXT,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    token VARCHAR(255) UNIQUE,
    token_expiracion TIMESTAMP,
    solicitado_por VARCHAR(100),
    aprobado_por VARCHAR(100),
    fecha_aprobacion TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cambios_estado ON cambios_empleado(estado);
CREATE INDEX IF NOT EXISTS idx_cambios_token ON cambios_empleado(token);
