package edu.dosw.styleradar.usuarios.application.dto.request;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void debeValidarPreferenciasDeEstilo() {
        ActualizarPreferenciasEstiloRequestDTO dto =
                new ActualizarPreferenciasEstiloRequestDTO();

        dto.setPreferencias(Set.of(Estilo.CASUAL, Estilo.URBANO));

        assertTrue(validator.validate(dto).isEmpty());

        dto.setPreferencias(Set.of());

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void debeValidarTallasHabituales() {
        ActualizarTallasHabitualesRequestDTO dto =
                new ActualizarTallasHabitualesRequestDTO();

        dto.setTallas(Set.of(Talla.M, Talla.L));
        dto.setTallasPorTipoPrenda(Map.of(
                TipoPrenda.SUPERIOR, Set.of("M"),
                TipoPrenda.INFERIOR, Set.of("32"),
                TipoPrenda.CALZADO, Set.of("40")
        ));

        assertTrue(validator.validate(dto).isEmpty());

        dto.setTallasPorTipoPrenda(Map.of(
                TipoPrenda.INFERIOR, Set.of("M")
        ));

        assertFalse(validator.validate(dto).isEmpty());

        dto.setTallasPorTipoPrenda(null);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setTallasPorTipoPrenda(Map.of(TipoPrenda.SUPERIOR, Set.of()));
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void debeValidarCambioEstadoCuenta() {
        CambioEstadoCuentaRequestDTO dto =
                new CambioEstadoCuentaRequestDTO();

        dto.setEstado(EstadoCuenta.ACTIVA);

        assertTrue(validator.validate(dto).isEmpty());

        dto.setEstado(EstadoCuenta.BLOQUEADA);
        dto.setMotivo("Incumplimiento de las normas");

        assertTrue(validator.validate(dto).isEmpty());

        dto.setMotivo(" ");

        assertFalse(validator.validate(dto).isEmpty());

        dto.setEstado(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void debeValidarConsultaDeCuentas() {
        CuentasConsultaRequestDTO dto =
                new CuentasConsultaRequestDTO();

        assertTrue(validator.validate(dto).isEmpty());

        dto.setPage(-1);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setPage(0);
        dto.setSize(0);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setSize(20);
        dto.setQ("x".repeat(101));

        assertFalse(validator.validate(dto).isEmpty());

        dto.setQ(null);
        dto.setSize(101);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setSize(20);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void debeValidarBusquedaGuardada() {
        GuardarBusquedaRequestDTO dto =
                new GuardarBusquedaRequestDTO();

        dto.setNombre("Chaquetas casuales");
        dto.setQuery("chaqueta");
        dto.setColor("Negro");
        dto.setPrecioMin(50000.0);
        dto.setPrecioMax(150000.0);
        dto.setLatitud(4.6761);
        dto.setLongitud(-74.0482);

        assertTrue(validator.validate(dto).isEmpty());

        dto.setNombre("");

        assertFalse(validator.validate(dto).isEmpty());

        dto.setNombre("Chaquetas casuales");
        dto.setPrecioMin(200000.0);
        dto.setPrecioMax(100000.0);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setPrecioMin(50000.0);
        dto.setPrecioMax(150000.0);
        dto.setLatitud(100.0);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setQuery(null);
        dto.setColor(null);
        dto.setMarca(null);
        assertTrue(dto.isQueryValido());
        assertTrue(dto.isColorValido());
        assertTrue(dto.isMarcaValida());

        dto.setQuery(" ");
        dto.setColor(" ");
        dto.setMarca(" ");
        assertFalse(dto.isQueryValido());
        assertFalse(dto.isColorValido());
        assertFalse(dto.isMarcaValida());
    }

    @Test
    void debeValidarParametrosDePersonalizacion() {
        PersonalizacionRequestDTO dto =
                new PersonalizacionRequestDTO();

        assertTrue(validator.validate(dto).isEmpty());

        dto.setPage(-1);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setPage(0);
        dto.setSize(21);

        assertFalse(validator.validate(dto).isEmpty());

        dto.setSize(20);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void debeValidarRegistroDeUsuario() {
        RegistrarUsuarioRequestDTO dto =
                new RegistrarUsuarioRequestDTO();

        dto.setNombre("Laura Gomez");
        dto.setEmail("laura@styleradar.com");
        dto.setTelefono("+57 300 123 4567");
        dto.setPassword("StyleRadar2026*");
        dto.setAceptaHabeasData(true);

        assertTrue(validator.validate(dto).isEmpty());

        dto.setEmail("correo-invalido");

        assertFalse(validator.validate(dto).isEmpty());

        dto.setEmail("laura@styleradar.com");
        dto.setPassword("123");

        assertFalse(validator.validate(dto).isEmpty());

        dto.setPassword("StyleRadar2026*");
        dto.setAceptaHabeasData(false);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void accountTypeInvalidoSeReportaEnElCampoDelContrato() {
        RegistrarUsuarioRequestDTO dto = new RegistrarUsuarioRequestDTO();
        dto.setNombre("Laura Gomez");
        dto.setEmail("laura@styleradar.com");
        dto.setPassword("StyleRadar2026*");
        dto.setAceptaHabeasData(true);
        dto.setAccountType("FOUNDATION_REPRESENTATIVE");

        assertTrue(validator.validate(dto).stream()
                .anyMatch(error -> "accountType".equals(error.getPropertyPath().toString())));
    }

    @Test
    void roleInvalidoSeReportaEnElCampoDelContrato() {
        CambiarRolUsuarioRequestDTO dto = new CambiarRolUsuarioRequestDTO("UNKNOWN_ROLE");

        assertTrue(validator.validate(dto).stream()
                .anyMatch(error -> "role".equals(error.getPropertyPath().toString())));
    }

    @Test
    void contrasenasDebenIncluirMayusculaMinusculaNumeroYSimbolo() {
        RegistrarUsuarioRequestDTO registro = new RegistrarUsuarioRequestDTO();
        registro.setNombre("Laura Gomez");
        registro.setEmail("laura@styleradar.com");
        registro.setPassword("StyleRadar2026");
        registro.setAceptaHabeasData(true);
        assertTrue(validator.validate(registro).stream()
                .anyMatch(error -> "password".equals(error.getPropertyPath().toString())));

        CambiarContrasenaRequestDTO cambio =
                new CambiarContrasenaRequestDTO("CurrentPass2026*", "WeakPass2026");
        assertTrue(validator.validate(cambio).stream()
                .anyMatch(error -> "newPassword".equals(error.getPropertyPath().toString())));

        CambiarContrasenaRequestDTO valido =
                new CambiarContrasenaRequestDTO("CurrentPass2026*", "NewPass2026*");
        assertTrue(validator.validate(valido).isEmpty());
    }

    @Test
    void debeValidarDatosPersonalesYPermitirOmitirTelefonoOBarrio() {
        ActualizarDatosUsuarioRequestDTO valido =
                new ActualizarDatosUsuarioRequestDTO("Laura Gómez", null, null);
        assertTrue(validator.validate(valido).isEmpty());

        ActualizarDatosUsuarioRequestDTO nombreVacio =
                new ActualizarDatosUsuarioRequestDTO(" ", null, null);
        assertTrue(validator.validate(nombreVacio).stream()
                .anyMatch(error -> "name".equals(error.getPropertyPath().toString())));

        ActualizarDatosUsuarioRequestDTO telefonoInvalido =
                new ActualizarDatosUsuarioRequestDTO("Laura Gómez", "teléfono", null);
        assertTrue(validator.validate(telefonoInvalido).stream()
                .anyMatch(error -> "phone".equals(error.getPropertyPath().toString())));

        ActualizarDatosUsuarioRequestDTO barrioLargo =
                new ActualizarDatosUsuarioRequestDTO("Laura Gómez", null, "x".repeat(101));
        assertTrue(validator.validate(barrioLargo).stream()
                .anyMatch(error -> "neighborhood".equals(error.getPropertyPath().toString())));
    }

    @Test
    void debeValidarCredenciales() {
        VerificarCredencialesRequestDTO dto =
                new VerificarCredencialesRequestDTO(
                        "usuario@styleradar.com",
                        "password123"
                );

        assertTrue(validator.validate(dto).isEmpty());

        VerificarCredencialesRequestDTO emailInvalido =
                new VerificarCredencialesRequestDTO(
                        "correo-invalido",
                        "password123"
                );

        assertFalse(validator.validate(emailInvalido).isEmpty());

        VerificarCredencialesRequestDTO passwordVacio =
                new VerificarCredencialesRequestDTO(
                        "usuario@styleradar.com",
                        ""
                );

        assertFalse(validator.validate(passwordVacio).isEmpty());
    }
}
