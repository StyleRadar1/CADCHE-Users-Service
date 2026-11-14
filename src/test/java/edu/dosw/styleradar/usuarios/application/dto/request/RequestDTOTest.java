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
        dto.setPassword("StyleRadar2026");
        dto.setAceptaHabeasData(true);

        assertTrue(validator.validate(dto).isEmpty());

        dto.setEmail("correo-invalido");

        assertFalse(validator.validate(dto).isEmpty());

        dto.setEmail("laura@styleradar.com");
        dto.setPassword("123");

        assertFalse(validator.validate(dto).isEmpty());

        dto.setPassword("StyleRadar2026");
        dto.setAceptaHabeasData(false);

        assertFalse(validator.validate(dto).isEmpty());
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
