ALTER TABLE usuarios
    ADD COLUMN fecha_cambio_estado TIMESTAMP WITH TIME ZONE,
    ADD COLUMN administrador_cambio_estado_id BIGINT,
    ADD COLUMN motivo_cambio_estado VARCHAR(1000);