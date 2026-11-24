ALTER TABLE usuarios
    ADD COLUMN provider VARCHAR(30),
    ADD COLUMN provider_id VARCHAR(255);

ALTER TABLE usuarios
    ADD CONSTRAINT chk_usuarios_external_identity_pair
        CHECK ((provider IS NULL AND provider_id IS NULL)
            OR (provider IS NOT NULL AND provider_id IS NOT NULL)),
    ADD CONSTRAINT chk_usuarios_external_provider
        CHECK (provider IS NULL OR provider = 'GOOGLE');

CREATE UNIQUE INDEX uk_usuarios_provider_provider_id
    ON usuarios (provider, provider_id)
    WHERE provider_id IS NOT NULL;
