-- Base de datos: monitoreo_precios
-- Ejecutar conectado a la base de datos monitoreo_precios.

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    clave_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'ANALISTA')),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto BIGSERIAL PRIMARY KEY,
    id_categoria BIGINT REFERENCES categoria(id_categoria),
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(180) NOT NULL,
    marca VARCHAR(120),
    modelo VARCHAR(120),
    presentacion VARCHAR(120),
    capacidad VARCHAR(120),
    costo NUMERIC(10,2) NOT NULL CHECK (costo > 0),
    precio_venta NUMERIC(10,2) NOT NULL CHECK (precio_venta > 0),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fuente_scraping (
    id_fuente BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    url_base TEXT NOT NULL UNIQUE,
    selectores JSONB,
    intervalo_minutos INTEGER NOT NULL DEFAULT 60 CHECK (intervalo_minutos >= 15),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_revision TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS producto_fuente (
    id_producto BIGINT NOT NULL REFERENCES producto(id_producto),
    id_fuente BIGINT NOT NULL REFERENCES fuente_scraping(id_fuente),
    url_producto TEXT,
    PRIMARY KEY (id_producto, id_fuente)
);

CREATE TABLE IF NOT EXISTS scraping_ejecucion (
    id_ejecucion BIGSERIAL PRIMARY KEY,
    id_fuente BIGINT NOT NULL REFERENCES fuente_scraping(id_fuente),
    id_usuario BIGINT REFERENCES usuario(id_usuario),
    fecha_inicio TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMPTZ,
    estado VARCHAR(20) NOT NULL DEFAULT 'INICIADO',
    cantidad_resultados INTEGER NOT NULL DEFAULT 0,
    mensaje_error TEXT
);

CREATE TABLE IF NOT EXISTS scraping_detalle (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_ejecucion BIGINT NOT NULL REFERENCES scraping_ejecucion(id_ejecucion),
    id_producto BIGINT NOT NULL REFERENCES producto(id_producto),
    titulo_externo VARCHAR(300),
    url_producto TEXT NOT NULL,
    precio_externo NUMERIC(10,2) NOT NULL CHECK (precio_externo > 0),
    fecha_scraping TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS homologacion (
    id_homologacion BIGSERIAL PRIMARY KEY,
    id_detalle BIGINT NOT NULL REFERENCES scraping_detalle(id_detalle),
    id_producto BIGINT NOT NULL REFERENCES producto(id_producto),
    id_usuario_validador BIGINT REFERENCES usuario(id_usuario),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE','CONFIRMADO','RECHAZADO')),
    observacion TEXT,
    fecha_validacion TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS comparacion_precio (
    id_comparacion BIGSERIAL PRIMARY KEY,
    id_homologacion BIGINT NOT NULL REFERENCES homologacion(id_homologacion),
    costo_snapshot NUMERIC(10,2) NOT NULL,
    precio_propio_snapshot NUMERIC(10,2) NOT NULL,
    precio_externo_snapshot NUMERIC(10,2) NOT NULL,
    utilidad_actual NUMERIC(10,2) NOT NULL,
    margen_actual NUMERIC(8,2) NOT NULL,
    utilidad_simulada NUMERIC(10,2) NOT NULL,
    margen_simulado NUMERIC(8,2) NOT NULL,
    fecha_comparacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alerta (
    id_alerta BIGSERIAL PRIMARY KEY,
    id_comparacion BIGINT NOT NULL REFERENCES comparacion_precio(id_comparacion),
    tipo VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    id_usuario_responsable BIGINT REFERENCES usuario(id_usuario),
    observacion TEXT,
    fecha_alerta TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_atencion TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS historico_precio (
    id_historico BIGSERIAL PRIMARY KEY,
    id_producto BIGINT NOT NULL REFERENCES producto(id_producto),
    id_fuente BIGINT NOT NULL REFERENCES fuente_scraping(id_fuente),
    precio NUMERIC(10,2) NOT NULL CHECK (precio > 0),
    fecha_registro TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
