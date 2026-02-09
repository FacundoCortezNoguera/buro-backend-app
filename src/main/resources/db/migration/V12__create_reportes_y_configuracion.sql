-- Tabla de configuración del sistema
CREATE TABLE IF NOT EXISTS configuracion_sistema (
    id BIGSERIAL PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT,
    descripcion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar configuración inicial de email
INSERT INTO configuracion_sistema (clave, valor, descripcion) VALUES
('EMAIL_REPORTES', '', 'Email donde se envían los reportes de cierre de noche'),
('NOMBRE_NEGOCIO', 'BURO', 'Nombre del negocio para los reportes');

-- Tabla de reportes generados
CREATE TABLE IF NOT EXISTS reportes (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    fecha_reporte DATE NOT NULL,
    titulo VARCHAR(255),
    descripcion TEXT,
    archivo_nombre VARCHAR(255),
    archivo_path VARCHAR(500),
    total_monto DECIMAL(12,2),
    cantidad_empleados INTEGER,
    enviado BOOLEAN DEFAULT FALSE,
    email_destino VARCHAR(255),
    fecha_envio TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_reportes_fecha ON reportes(fecha_reporte);
CREATE INDEX IF NOT EXISTS idx_reportes_tipo ON reportes(tipo);

-- Agregar rol DUENO
INSERT INTO roles (code, description) VALUES ('DUENO', 'Dueño del negocio')
ON CONFLICT (code) DO NOTHING;

-- Crear usuario dueño por defecto
INSERT INTO users (username, password_hash, nombre, role_id, enabled)
SELECT 'dueno', '$2a$10$N9qo8uLOickgx2ZMRZoMye3FfCJL3FWHZLvAuaVe9xRvRYfwrJGXC', 'Dueño', r.id, true
FROM roles r WHERE r.code = 'DUENO'
ON CONFLICT (username) DO NOTHING;
