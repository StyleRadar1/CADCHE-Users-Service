CREATE TABLE usuario_tallas (
                                id BIGSERIAL PRIMARY KEY,
                                usuario_id BIGINT NOT NULL,
                                tipo_prenda VARCHAR(20) NOT NULL,
                                talla VARCHAR(10) NOT NULL,

                                CONSTRAINT fk_tallas_usuario
                                    FOREIGN KEY (usuario_id)
                                        REFERENCES usuarios(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT uk_usuario_tipo_talla
                                    UNIQUE (usuario_id, tipo_prenda, talla),

                                CONSTRAINT chk_tipo_prenda
                                    CHECK (tipo_prenda IN ('SUPERIOR', 'INFERIOR', 'CALZADO'))
);

CREATE INDEX idx_tallas_usuario
    ON usuario_tallas(usuario_id);