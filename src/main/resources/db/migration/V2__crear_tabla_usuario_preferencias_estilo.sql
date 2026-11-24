CREATE TABLE usuario_preferencias_estilo (
                                             id BIGSERIAL PRIMARY KEY,
                                             usuario_id BIGINT NOT NULL,
                                             estilo VARCHAR(30) NOT NULL,

                                             CONSTRAINT fk_preferencias_usuario
                                                 FOREIGN KEY (usuario_id)
                                                     REFERENCES usuarios(id)
                                                     ON DELETE CASCADE,

                                             CONSTRAINT uk_usuario_estilo
                                                 UNIQUE (usuario_id, estilo)
);

CREATE INDEX idx_preferencias_usuario
    ON usuario_preferencias_estilo(usuario_id);