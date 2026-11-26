package edu.dosw.styleradar.usuarios.application;

import java.util.UUID;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.application.usecase.RegistrarUsuarioUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.event.UsuarioRegistradoEvent;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRegistradoEventPublisherPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarUsuarioUseCaseTest {

    private UsuarioRepositoryPort usuarioRepository;
    private UsuarioRegistradoEventPublisherPort eventoRegistroPublisher;
    private PasswordEncoder passwordEncoder;
    private RegistrarUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        eventoRegistroPublisher = mock(UsuarioRegistradoEventPublisherPort.class);
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new RegistrarUsuarioUseCaseImpl(
                usuarioRepository,
                eventoRegistroPublisher,
                passwordEncoder
        );
    }

    @Test
    void debeRegistrarUsuarioCorrectamente() {

        RegistrarUsuarioRequestDTO request = crearRequest();

        request.setHeightCm(175.0);
        request.setChestCm(96.0);
        request.setWaistCm(82.0);
        request.setHipCm(98.0);

        when(usuarioRepository.existePorEmail("laura@gmail.com"))
                .thenReturn(false);

        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);

                    return new Usuario(
                            1L,
                            usuario.getNombre(),
                            usuario.getEmail(),
                            usuario.getTelefono(),
                            usuario.getPasswordHash(),
                            usuario.getAceptaHabeasData(),
                            usuario.getRol(),
                            usuario.getEstado(),
                            usuario.getFechaRegistro(),
                            null,
                            null,
                            null,
                            usuario.getCorreoVerificado(),
                            usuario.getBarrio(),
                            usuario.getHeightCm(),
                            usuario.getChestCm(),
                            usuario.getWaistCm(),
                            usuario.getHipCm()
                    );
                });

        UsuarioResponseDTO response = useCase.registrar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laura Gómez", response.getNombre());
        assertEquals("laura@gmail.com", response.getEmail());
        assertEquals(Boolean.FALSE, response.getCorreoVerificado());
        assertEquals("Chapinero", response.getBarrio());

        verify(usuarioRepository).guardar(argThat(usuario -> {
            assertTrue(passwordEncoder.matches(
                    "StyleRadar2026*",
                    usuario.getPasswordHash()
            ));

            assertEquals(Boolean.TRUE, usuario.getAceptaHabeasData());
            assertEquals(Rol.COMPRADOR, usuario.getRol());
            assertEquals(EstadoCuenta.ACTIVA, usuario.getEstado());

            assertEquals(Boolean.FALSE, usuario.getCorreoVerificado());
            assertEquals("Chapinero", usuario.getBarrio());

            assertEquals(175.0, usuario.getHeightCm());
            assertEquals(96.0, usuario.getChestCm());
            assertEquals(82.0, usuario.getWaistCm());
            assertEquals(98.0, usuario.getHipCm());

            return true;
        }));

        ArgumentCaptor<UsuarioRegistradoEvent> eventoCaptor =
                ArgumentCaptor.forClass(UsuarioRegistradoEvent.class);
        verify(eventoRegistroPublisher).publicar(eventoCaptor.capture());
        assertDoesNotThrow(() -> UUID.fromString(eventoCaptor.getValue().eventId()));
        assertEquals(response.getFechaRegistro(), eventoCaptor.getValue().occurredAt());
        assertEquals(Rol.COMPRADOR, eventoCaptor.getValue().rol());
    }

    @Test
    void debeRegistrarUsuarioSinMedidasCorporales() {

        RegistrarUsuarioRequestDTO request = crearRequest();

        when(usuarioRepository.existePorEmail("laura@gmail.com"))
                .thenReturn(false);

        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.registrar(request);

        verify(usuarioRepository).guardar(argThat(usuario ->
                usuario.getHeightCm() == null
                        && usuario.getChestCm() == null
                        && usuario.getWaistCm() == null
                        && usuario.getHipCm() == null
        ));
    }

    @Test
    void registraCuentaStoreConRolAdminAlmacenYPublicaEseRol() {
        RegistrarUsuarioRequestDTO request = crearRequest();
        request.setAccountType("STORE");
        when(usuarioRepository.existePorEmail("laura@gmail.com")).thenReturn(false);
        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = useCase.registrar(request);

        assertEquals(Rol.ADMIN_ALMACEN, response.getRol());
        verify(usuarioRepository).guardar(argThat(usuario ->
                usuario.getRol() == Rol.ADMIN_ALMACEN));
        verify(eventoRegistroPublisher).publicar(argThat(evento ->
                evento.rol() == Rol.ADMIN_ALMACEN));
    }

    @Test
    void registraAliasStoreAdminComoCuentaDeTienda() {
        RegistrarUsuarioRequestDTO request = crearRequest();
        request.setAccountType("STORE_ADMIN");
        when(usuarioRepository.existePorEmail("laura@gmail.com")).thenReturn(false);
        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = useCase.registrar(request);

        assertEquals(Rol.ADMIN_ALMACEN, response.getRol());
        verify(eventoRegistroPublisher).publicar(argThat(evento ->
                evento.rol() == Rol.ADMIN_ALMACEN));
    }

    @Test
    void cuandoAccountTypeSeOmiteUsaBuyer() {
        RegistrarUsuarioRequestDTO request = crearRequest();
        assertEquals("BUYER", request.getAccountType());
        assertEquals("BUYER", RegistrarUsuarioRequestDTO.builder().build().getAccountType());

        when(usuarioRepository.existePorEmail("laura@gmail.com")).thenReturn(false);
        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = useCase.registrar(request);

        assertEquals(Rol.COMPRADOR, response.getRol());
    }

    @Test
    void noDebeRegistrarUnCorreoExistente() {

        RegistrarUsuarioRequestDTO request = crearRequest();

        when(usuarioRepository.existePorEmail("laura@gmail.com"))
                .thenReturn(true);

        assertThrows(
                EmailYaRegistradoException.class,
                () -> useCase.registrar(request)
        );

        verify(usuarioRepository, never()).guardar(any(Usuario.class));
        verifyNoInteractions(eventoRegistroPublisher);
    }

    private RegistrarUsuarioRequestDTO crearRequest() {

        RegistrarUsuarioRequestDTO request =
                new RegistrarUsuarioRequestDTO();

        request.setNombre("Laura Gómez");
        request.setEmail(" Laura@GMAIL.COM ");
        request.setTelefono("+57 300 123 4567");
        request.setBarrio("Chapinero");
        request.setPassword("StyleRadar2026*");
        request.setAceptaHabeasData(true);

        return request;
    }
}
