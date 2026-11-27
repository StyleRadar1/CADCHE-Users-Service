package edu.dosw.styleradar.usuarios.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import edu.dosw.styleradar.usuarios.application.dto.response.ErrorResponseDTO;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Map<String, String> EQUIVALENCIAS_ROLES = Map.of(
            "BUYER", "ROLE_COMPRADOR",
            "COMPRADOR", "ROLE_COMPRADOR",
            "STORE_ADMIN", "ROLE_ADMIN_ALMACEN",
            "ADMIN_ALMACEN", "ROLE_ADMIN_ALMACEN",
            "FOUNDATION_REPRESENTATIVE", "ROLE_REPRESENTANTE_FUNDACION",
            "REPRESENTANTE_FUNDACION", "ROLE_REPRESENTANTE_FUNDACION",
            "STYLERADAR_ADMIN", "ROLE_ADMIN_STYLERADAR",
            "ADMIN_STYLERADAR", "ROLE_ADMIN_STYLERADAR"
    );

    @Value("${INTERNAL_AUTH_USERNAME}")
    private String username;

    @Value("${INTERNAL_AUTH_PASSWORD}")
    private String password;

    @Value("${INTERNAL_PUBLIC_PROFILE_USERNAME:}")
    private String publicProfileUsername;

    @Value("${INTERNAL_PUBLIC_PROFILE_PASSWORD:}")
    private String publicProfilePassword;

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {
        return buildUserDetailsService(
                passwordEncoder,
                username,
                password,
                publicProfileUsername,
                publicProfilePassword
        );
    }

    UserDetailsService buildUserDetailsService(
            PasswordEncoder passwordEncoder,
            String mainUsername,
            String mainPassword,
            String directoryUsername,
            String directoryPassword) {

        List<UserDetails> usuarios = new ArrayList<>();
        usuarios.add(User.withUsername(mainUsername)
                .password(passwordEncoder.encode(mainPassword))
                .roles("SERVICE")
                .build());

        if (directoryUsername != null && !directoryUsername.isBlank()
                && directoryPassword != null && !directoryPassword.isBlank()
                && !directoryUsername.equals(mainUsername)) {
            usuarios.add(User.withUsername(directoryUsername)
                    .password(passwordEncoder.encode(directoryPassword))
                    .roles("PUBLIC_PROFILE_SERVICE")
                    .build());
        }

        return new InMemoryUserDetailsManager(usuarios);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return List.of();
            }

            return roles.stream()
                    .map(role -> role.startsWith("ROLE_")
                            ? role.substring(5)
                            : role)
                    .map(EQUIVALENCIAS_ROLES::get)
                    .filter(role -> role != null)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }

    // SEGURIDAD PARA COMUNICACIONES INTERNAS
    @Bean
    @Order(1)
    public SecurityFilterChain internalSecurityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            ObjectMapper objectMapper) throws Exception {

        http
                .securityMatcher(
                        "/api/v1/internal/**",
                        "/internal/api/v1/usuarios/verificar-credenciales"
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/v1/internal/**",
                        "/internal/api/v1/usuarios/verificar-credenciales"
                ))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/internal/users/*/public-profile")
                        .hasAnyRole("PUBLIC_PROFILE_SERVICE", "SERVICE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/internal/users/public-profiles")
                        .hasAnyRole("PUBLIC_PROFILE_SERVICE", "SERVICE")
                        .anyRequest().hasRole("SERVICE")
                )
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) ->
                                escribirErrorSeguridad(objectMapper, request, response,
                                        401, "Unauthorized", "AUTHENTICATION_REQUIRED",
                                        "Se requiere autenticación de servicio"))
                        .accessDeniedHandler((request, response, exception) ->
                                escribirErrorSeguridad(objectMapper, request, response,
                                        403, "Forbidden", "ACCESS_DENIED",
                                        "No tiene permiso para acceder a este recurso")))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // SEGURIDAD PARA LA API PUBLICA
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            ObjectMapper objectMapper)
            throws Exception {

        http
                .securityMatcher(
                        "/api/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/health"
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/users"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/admin/users/**"
                        ).hasRole("ADMIN_STYLERADAR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/users/*/password"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/users/*"
                        ).hasRole("COMPRADOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/users/*"
                        ).authenticated()

                        .requestMatchers("/api/v1/users/*/**")
                        .hasAnyRole("COMPRADOR", "ADMIN_STYLERADAR")

                        .requestMatchers(
                                "/api/v1/users/**"
                        ).authenticated()

                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint((request, response, exception) ->
                                escribirErrorSeguridad(objectMapper, request, response,
                                        401, "Unauthorized", "AUTHENTICATION_REQUIRED",
                                        "Se requiere autenticación"))
                        .accessDeniedHandler((request, response, exception) ->
                                escribirErrorSeguridad(objectMapper, request, response,
                                        403, "Forbidden", "ACCESS_DENIED",
                                        "No tiene permiso para acceder a este recurso"))
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) ->
                                escribirErrorSeguridad(objectMapper, request, response,
                                        401, "Unauthorized", "AUTHENTICATION_REQUIRED",
                                        "Se requiere autenticación"))
                        .accessDeniedHandler((request, response, exception) ->
                                escribirErrorSeguridad(objectMapper, request, response,
                                        403, "Forbidden", "ACCESS_DENIED",
                                        "No tiene permiso para acceder a este recurso")));

        return http.build();
    }

    // BLOQUEO DE CUALQUIER RUTA NO CONTEMPLADA
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    private void escribirErrorSeguridad(
            ObjectMapper objectMapper,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            int status,
            String title,
            String code,
            String detail) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/problem+json");
        objectMapper.writeValue(response.getOutputStream(), ErrorResponseDTO.builder()
                .type("https://api.styleradar.com/errors/"
                        + code.toLowerCase(java.util.Locale.ROOT).replace('_', '-'))
                .title(title)
                .status(status)
                .detail(detail)
                .instance(request.getRequestURI())
                .code(code)
                .errors(Map.of())
                .build());
    }
}

