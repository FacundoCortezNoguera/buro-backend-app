CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       code VARCHAR(30) UNIQUE NOT NULL,
                       description VARCHAR(100)
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(60) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       nombre VARCHAR(100) NOT NULL,
                       role_id BIGINT NOT NULL REFERENCES roles(id),
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE empleados (
                           id BIGSERIAL PRIMARY KEY,
                           nombre VARCHAR(60) NOT NULL,
                           apellido VARCHAR(60) NOT NULL,
                           documento_tipo VARCHAR(10),
                           documento_numero VARCHAR(20) NOT NULL,
                           rol_id BIGINT REFERENCES roles(id),
                           tipo_pago VARCHAR(10) NOT NULL, -- 'HORA' o 'DIA'
                           activo BOOLEAN NOT NULL DEFAULT TRUE,
                           fecha_alta DATE DEFAULT CURRENT_DATE
);