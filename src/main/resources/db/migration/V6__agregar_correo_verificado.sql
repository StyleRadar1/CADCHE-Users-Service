ALTER TABLE usuarios
    ADD COLUMN correo_verificado BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE usuarios
    ALTER COLUMN correo_verificado SET DEFAULT FALSE;