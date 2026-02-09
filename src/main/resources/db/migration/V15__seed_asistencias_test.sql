-- V15: Seed asistencias para testing de reportes
-- Asistencias de las últimas 2 semanas para empleados por DIA y por HORA

-- Limpiar asistencias existentes (si las hay)
DELETE FROM asistencias;

-- =====================================================
-- SEMANA 1: 27/01/2026 - 02/02/2026 (Lun a Dom)
-- =====================================================

-- JUEVES 30/01/2026
-- Empleados por HORA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-30', '21:55', '22:00', -5, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '30123456'; -- Juan Pérez (Barra)

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-30', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '35678901'; -- Sofía Fernández (Promotor)

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-30', '22:05', '22:00', 5, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '37890123'; -- Valentina Sánchez (Cajero)

-- Empleados por DIA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-30', '21:50', '22:00', -10, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '32345678'; -- Carlos López (Seguridad)


-- VIERNES 31/01/2026
-- Empleados por HORA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '30123456'; -- Juan Pérez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '21:58', '22:00', -2, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '31234567'; -- María González (Cajero)

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '22:03', '22:00', 3, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '33456789'; -- Ana Martínez (Barra)

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '22:15', '22:00', 15, 'TARDE', 'admin' FROM empleados WHERE documento_numero = '35678901'; -- Sofía Fernández

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '21:55', '22:00', -5, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '37890123'; -- Valentina Sánchez

-- Empleados por DIA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '21:45', '22:00', -15, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '32345678'; -- Carlos López

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '23:00', '23:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '34567890'; -- Lucas Rodríguez (DJ)

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-01-31', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '36789012'; -- Diego García (Seguridad)


-- SABADO 01/02/2026
-- Empleados por HORA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '21:50', '22:00', -10, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '30123456'; -- Juan Pérez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '31234567'; -- María González

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '22:10', '22:00', 10, 'TARDE', 'admin' FROM empleados WHERE documento_numero = '33456789'; -- Ana Martínez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '21:55', '22:00', -5, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '35678901'; -- Sofía Fernández

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '22:02', '22:00', 2, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '37890123'; -- Valentina Sánchez

-- Empleados por DIA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '21:55', '22:00', -5, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '32345678'; -- Carlos López

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '23:05', '23:00', 5, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '34567890'; -- Lucas Rodríguez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-01', '22:20', '22:00', 20, 'TARDE', 'admin' FROM empleados WHERE documento_numero = '36789012'; -- Diego García


-- DOMINGO 02/02/2026
-- Empleados por HORA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-02', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '30123456'; -- Juan Pérez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-02', '21:58', '22:00', -2, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '31234567'; -- María González

-- Empleados por DIA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-02', '21:50', '22:00', -10, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '32345678'; -- Carlos López

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-02', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '36789012'; -- Diego García


-- =====================================================
-- SEMANA 2: 03/02/2026 - 08/02/2026 (Lun a Dom)
-- =====================================================

-- JUEVES 05/02/2026
-- Empleados por HORA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-05', '21:58', '22:00', -2, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '30123456'; -- Juan Pérez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-05', '22:05', '22:00', 5, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '35678901'; -- Sofía Fernández

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-05', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '37890123'; -- Valentina Sánchez

-- Empleados por DIA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-05', '21:55', '22:00', -5, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '32345678'; -- Carlos López


-- VIERNES 06/02/2026
-- Empleados por HORA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '21:55', '22:00', -5, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '30123456'; -- Juan Pérez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '31234567'; -- María González

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '22:00', '22:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '33456789'; -- Ana Martínez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '21:50', '22:00', -10, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '35678901'; -- Sofía Fernández

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '22:08', '22:00', 8, 'TARDE', 'admin' FROM empleados WHERE documento_numero = '37890123'; -- Valentina Sánchez

-- Empleados por DIA
INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '21:50', '22:00', -10, 'TEMPRANO', 'admin' FROM empleados WHERE documento_numero = '32345678'; -- Carlos López

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '23:00', '23:00', 0, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '34567890'; -- Lucas Rodríguez

INSERT INTO asistencias (empleado_id, fecha, hora_llegada, hora_esperada, minutos_diferencia, estado, registrado_por)
SELECT id, '2026-02-06', '21:58', '22:00', -2, 'PUNTUAL', 'admin' FROM empleados WHERE documento_numero = '36789012'; -- Diego García


-- =====================================================
-- RESUMEN DE DATOS DE PRUEBA:
-- =====================================================
-- Empleados por HORA (5):
--   - Juan Pérez (Barra): $2,200/hr x 8hs = $17,600/día
--   - María González (Cajero): $2,500/hr x 8hs = $20,000/día
--   - Ana Martínez (Barra): $2,200/hr x 8hs = $17,600/día
--   - Sofía Fernández (Promotor): $2,000/hr x 6hs = $12,000/día
--   - Valentina Sánchez (Cajero): $2,500/hr x 8hs = $20,000/día
--
-- Empleados por DIA (4):
--   - Carlos López (Seguridad): $20,000/día
--   - Lucas Rodríguez (DJ): $25,000/día
--   - Diego García (Seguridad): $20,000/día
--   - Camila Torres (Admin): $24,000/día (no tiene asistencias de prueba)
-- =====================================================
