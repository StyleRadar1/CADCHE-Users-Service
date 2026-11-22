-- ============================================================
-- CADCHE - ms-usuarios
-- Creacion de cuentas administrativas para QA
--
-- Base esperada: styleradar_usuarios
-- Requiere: pgcrypto
--
-- Roles internos:
-- ADMIN_ALMACEN
-- ADMIN_STYLERADAR
-- REPRESENTANTE_FUNDACION
--
-- No modifica cuentas existentes.
-- Las contrasenas solo se muestran para cuentas nuevas.
-- ============================================================

BEGIN;

-- 1. PROTECCION DE BASE DE DATOS

DO $$
BEGIN
    IF current_database() <> 'styleradar_usuarios' THEN
        RAISE EXCEPTION
            'Ejecucion cancelada. Base esperada: styleradar_usuarios. Base actual: %',
            current_database();
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_extension
        WHERE extname = 'pgcrypto'
    ) THEN
        RAISE EXCEPTION 'La extension pgcrypto no esta instalada.';
END IF;
END;
$$;

-- 2. TABLA TEMPORAL DE CREDENCIALES

CREATE TEMP TABLE qa_cuentas_generadas (
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    rol VARCHAR(40) NOT NULL,
    password TEXT NOT NULL
) ON COMMIT PRESERVE ROWS;

-- 3. GENERADOR DE CONTRASENAS

CREATE FUNCTION pg_temp.generar_password_qa()
    RETURNS TEXT
    LANGUAGE plpgsql
AS $$
DECLARE
mayusculas CONSTANT TEXT := 'ABCDEFGHJKLMNPQRSTUVWXYZ';
    minusculas CONSTANT TEXT := 'abcdefghijkmnopqrstuvwxyz';
    numeros CONSTANT TEXT := '23456789';
    simbolos CONSTANT TEXT := '!@#$%&*-_';

    todos CONSTANT TEXT :=
        'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%&*-_';

    caracteres TEXT[];
    resultado TEXT;
    i INTEGER;
BEGIN
    -- Garantizar las cuatro categorias obligatorias.
    caracteres := ARRAY[
        substr(mayusculas,
            (get_byte(gen_random_bytes(1), 0) % length(mayusculas)) + 1, 1),
        substr(minusculas,
            (get_byte(gen_random_bytes(1), 0) % length(minusculas)) + 1, 1),
        substr(numeros,
            (get_byte(gen_random_bytes(1), 0) % length(numeros)) + 1, 1),
        substr(simbolos,
            (get_byte(gen_random_bytes(1), 0) % length(simbolos)) + 1, 1)
    ];

    -- Completar hasta 16 caracteres.
FOR i IN 1..12 LOOP
        caracteres := array_append(
            caracteres,
            substr(todos,
                (get_byte(gen_random_bytes(1), 0) % length(todos)) + 1, 1)
        );
END LOOP;

    -- Mezclar los caracteres.
SELECT string_agg(
               letra,
               ''
                   ORDER BY encode(gen_random_bytes(4), 'hex')
       )
INTO resultado
FROM unnest(caracteres) AS t(letra);

RETURN resultado;
END;
$$;

-- 4. GENERAR CREDENCIALES SOLO PARA CUENTAS FALTANTES

INSERT INTO qa_cuentas_generadas (
    nombre,
    email,
    rol,
    password
)
SELECT
    datos.nombre,
    datos.email,
    datos.rol,
    pg_temp.generar_password_qa()
FROM (
         VALUES
             (
                 'Tienda Pruebas QA',
                 'styleradar1+tienda.qa@gmail.com',
                 'ADMIN_ALMACEN'
             ),
             (
                 'Administrador QA StyleRadar',
                 'styleradar1+admin.qa@gmail.com',
                 'ADMIN_STYLERADAR'
             ),
             (
                 'Representante Fundacion QA',
                 'styleradar1+fundacion.qa@gmail.com',
                 'REPRESENTANTE_FUNDACION'
             )
     ) AS datos(nombre, email, rol)
WHERE NOT EXISTS (
    SELECT 1
    FROM public.usuarios u
    WHERE LOWER(u.email) = LOWER(datos.email)
);

-- 5. INSERTAR USUARIOS CON HASH BCRYPT

CREATE TEMP TABLE qa_cuentas_insertadas
ON COMMIT PRESERVE ROWS
AS
WITH insertados AS (
    INSERT INTO public.usuarios (
        nombre,
        email,
        telefono,
        password_hash,
        acepta_habeas_data,
        rol,
        estado,
        fecha_registro,
        correo_verificado
    )
    SELECT
        nombre,
        email,
        NULL,
        crypt(password, gen_salt('bf', 10)),
        TRUE,
        rol,
        'ACTIVA',
        NOW(),
        TRUE
    FROM qa_cuentas_generadas
    ON CONFLICT (email) DO NOTHING
    RETURNING id, email
)
SELECT id, email
FROM insertados;

COMMIT;

-- 6. RESULTADO DE CUENTAS CREADAS EN ESTA EJECUCION
--
-- IMPORTANTE:
-- Esta salida contiene contrasenas temporales.
-- No guardar resultados en archivos ni logs.
-- Compartir las credenciales exclusivamente por privado.

SELECT
    i.id AS user_id,
    g.nombre,
    g.email,
    g.rol,
    'ACTIVA' AS estado,
    TRUE AS correo_verificado,
    g.password AS contrasena_temporal
FROM qa_cuentas_generadas g
         INNER JOIN qa_cuentas_insertadas i
                    ON i.email = g.email
ORDER BY i.id;

-- 7. LIMPIEZA

DROP TABLE qa_cuentas_insertadas;
DROP TABLE qa_cuentas_generadas;
DROP FUNCTION pg_temp.generar_password_qa();
