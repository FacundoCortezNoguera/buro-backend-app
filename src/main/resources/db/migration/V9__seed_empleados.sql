-- V9: Seed empleados data for testing

-- Insert sample empleados
INSERT INTO empleados (nombre, apellido, documento_tipo, documento_numero, tipo_pago, activo, telefono, cargo, cobra_por_hora, cobra_por_dia, horas_por_dia)
VALUES
    ('Juan', 'Pérez', 'DNI', '30123456', 'HORA', true, '1155551234', 'Barra', 2200.00, NULL, 8),
    ('María', 'González', 'DNI', '31234567', 'HORA', true, '1155552345', 'Cajero', 2500.00, NULL, 8),
    ('Carlos', 'López', 'DNI', '32345678', 'DIA', true, '1155553456', 'Seguridad', NULL, 20000.00, 10),
    ('Ana', 'Martínez', 'DNI', '33456789', 'HORA', true, '1155554567', 'Barra', 2200.00, NULL, 8),
    ('Lucas', 'Rodríguez', 'DNI', '34567890', 'DIA', true, '1155555678', 'DJ', NULL, 25000.00, 6),
    ('Sofía', 'Fernández', 'DNI', '35678901', 'HORA', true, '1155556789', 'Promotor/a', 2000.00, NULL, 6),
    ('Diego', 'García', 'DNI', '36789012', 'DIA', true, '1155557890', 'Seguridad', NULL, 20000.00, 10),
    ('Valentina', 'Sánchez', 'DNI', '37890123', 'HORA', true, '1155558901', 'Cajero', 2500.00, NULL, 8),
    ('Matías', 'Romero', 'DNI', '38901234', 'HORA', false, '1155559012', 'Barra', 2200.00, NULL, 8),
    ('Camila', 'Torres', 'DNI', '39012345', 'DIA', true, '1155550123', 'Administración', NULL, 24000.00, 8);

-- Assign work days to empleados
-- Juan Pérez (Barra) - Jueves a Domingo
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '30123456'), 'JUEVES'),
    ((SELECT id FROM empleados WHERE documento_numero = '30123456'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '30123456'), 'SABADO'),
    ((SELECT id FROM empleados WHERE documento_numero = '30123456'), 'DOMINGO');

-- María González (Cajero) - Viernes a Domingo
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '31234567'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '31234567'), 'SABADO'),
    ((SELECT id FROM empleados WHERE documento_numero = '31234567'), 'DOMINGO');

-- Carlos López (Seguridad) - Jueves a Domingo
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '32345678'), 'JUEVES'),
    ((SELECT id FROM empleados WHERE documento_numero = '32345678'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '32345678'), 'SABADO'),
    ((SELECT id FROM empleados WHERE documento_numero = '32345678'), 'DOMINGO');

-- Ana Martínez (Barra) - Viernes y Sábado
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '33456789'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '33456789'), 'SABADO');

-- Lucas Rodríguez (DJ) - Viernes y Sábado
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '34567890'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '34567890'), 'SABADO');

-- Sofía Fernández (Promotor/a) - Jueves a Sábado
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '35678901'), 'JUEVES'),
    ((SELECT id FROM empleados WHERE documento_numero = '35678901'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '35678901'), 'SABADO');

-- Diego García (Seguridad) - Viernes a Domingo
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '36789012'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '36789012'), 'SABADO'),
    ((SELECT id FROM empleados WHERE documento_numero = '36789012'), 'DOMINGO');

-- Valentina Sánchez (Cajero) - Jueves a Sábado
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '37890123'), 'JUEVES'),
    ((SELECT id FROM empleados WHERE documento_numero = '37890123'), 'VIERNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '37890123'), 'SABADO');

-- Matías Romero (Barra - Inactivo) - Sábado
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '38901234'), 'SABADO');

-- Camila Torres (Administración) - Lunes a Viernes
INSERT INTO empleado_dias_trabajo (empleado_id, dia_semana) VALUES
    ((SELECT id FROM empleados WHERE documento_numero = '39012345'), 'LUNES'),
    ((SELECT id FROM empleados WHERE documento_numero = '39012345'), 'MARTES'),
    ((SELECT id FROM empleados WHERE documento_numero = '39012345'), 'MIERCOLES'),
    ((SELECT id FROM empleados WHERE documento_numero = '39012345'), 'JUEVES'),
    ((SELECT id FROM empleados WHERE documento_numero = '39012345'), 'VIERNES');
