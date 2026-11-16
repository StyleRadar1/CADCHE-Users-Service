CREATE TABLE busquedas_guardadas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    texto_busqueda VARCHAR(255),
    tipo VARCHAR(20),
    color VARCHAR(80),
    talla VARCHAR(10),
    precio_min DOUBLE PRECISION,
    precio_max DOUBLE PRECISION,
    marca VARCHAR(100),
    estilo VARCHAR(30),
    latitud DOUBLE PRECISION,
    longitud DOUBLE PRECISION,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_busquedas_guardadas_usuario_fecha
    ON busquedas_guardadas (usuario_id, fecha_creacion DESC);
