-- V5: Create pagos_diarios table for daily payments

CREATE TABLE IF NOT EXISTS pagos_diarios (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleados(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    concepto VARCHAR(100) NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    pagado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_pago DATE,
    turno_noche_id BIGINT REFERENCES turnos_noche(id) ON DELETE SET NULL,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_pagos_diarios_fecha ON pagos_diarios(fecha);
CREATE INDEX IF NOT EXISTS idx_pagos_diarios_empleado ON pagos_diarios(empleado_id);
CREATE INDEX IF NOT EXISTS idx_pagos_diarios_pagado ON pagos_diarios(pagado);
CREATE INDEX IF NOT EXISTS idx_pagos_diarios_fecha_range ON pagos_diarios(fecha, empleado_id);
