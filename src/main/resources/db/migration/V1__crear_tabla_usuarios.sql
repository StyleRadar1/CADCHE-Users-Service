CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          telefono VARCHAR(20),
                          password_hash VARCHAR(255) NOT NULL,
                          acepta_habeas_data BOOLEAN NOT NULL,
                          rol VARCHAR(40) NOT NULL,
                          estado VARCHAR(20) NOT NULL,
                          fecha_registro TIMESTAMP WITH TIME ZONE NOT NULL
);
