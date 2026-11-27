package edu.dosw.styleradar.usuarios.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private final JwtAuthenticationConverter converter =
            new SecurityConfig().jwtAuthenticationConverter();

    private Authentication convertirRoles(String... roles) {

        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256")
                .subject("144")
                .claim("roles", List.of(roles))
                .build();

        return converter.convert(jwt);
    }

    @Test
    void debeReconocerRolesNuevosDeAuth() {

        assertTrue(convertirRoles("BUYER").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_COMPRADOR")));

        assertTrue(convertirRoles("STORE_ADMIN").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN_ALMACEN")));

        assertTrue(convertirRoles("FOUNDATION_REPRESENTATIVE").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_REPRESENTANTE_FUNDACION")));

        assertTrue(convertirRoles("STYLERADAR_ADMIN").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN_STYLERADAR")));
    }

    @Test
    void debeMantenerCompatibilidadConRolesAnteriores() {

        assertTrue(convertirRoles("COMPRADOR").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_COMPRADOR")));

        assertTrue(convertirRoles("ADMIN_ALMACEN").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN_ALMACEN")));

        assertTrue(convertirRoles("REPRESENTANTE_FUNDACION").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_REPRESENTANTE_FUNDACION")));

        assertTrue(convertirRoles("ADMIN_STYLERADAR").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN_STYLERADAR")));
    }

    @Test
    void debeAceptarRolesConPrefijoRole() {

        assertTrue(convertirRoles("ROLE_BUYER").getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_COMPRADOR")));
    }

    @Test
    void debeIgnorarRolesDesconocidos() {

        assertTrue(convertirRoles("ROLE_DESCONOCIDO")
                .getAuthorities()
                .isEmpty());
    }

    @Test
    void noAsignaAutoridadesCuandoElTokenNoTieneClaimDeRoles() {
        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256")
                .subject("144")
                .build();

        Authentication authentication = converter.convert(jwt);

        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void registraCuentaDedicadaDeMinimoPrivilegioCuandoEstaConfigurada() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        UserDetailsService uds = config.buildUserDetailsService(
                encoder, "ms-auth", "secret-main", "ms-chat", "secret-chat");

        UserDetails mainUser = uds.loadUserByUsername("ms-auth");
        UserDetails chatUser = uds.loadUserByUsername("ms-chat");

        assertTrue(mainUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SERVICE")));
        assertTrue(chatUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PUBLIC_PROFILE_SERVICE")));
        assertFalse(chatUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SERVICE")));
    }

    @Test
    void noRegistraCuentaDedicadaCuandoFaltaOEsDuplicada() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        UserDetailsService sinChat = config.buildUserDetailsService(
                encoder, "ms-auth", "secret-main", "", "");
        assertThrows(UsernameNotFoundException.class, () -> sinChat.loadUserByUsername("ms-chat"));

        UserDetailsService duplicada = config.buildUserDetailsService(
                encoder, "ms-auth", "secret-main", "ms-auth", "otra-clave");
        UserDetails mainUser = duplicada.loadUserByUsername("ms-auth");
        assertTrue(mainUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SERVICE")));
    }
}
