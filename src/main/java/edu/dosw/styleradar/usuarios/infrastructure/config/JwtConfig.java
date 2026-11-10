package edu.dosw.styleradar.usuarios.infrastructure.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${styleradar.security.jwt.secret-base64:}") String secretBase64) {

        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException(
                    "No se configuró la clave JWT de Authentication-Service");
        }

        byte[] secret = Base64.getDecoder().decode(secretBase64);

        if (secret.length < 32) {
            throw new IllegalStateException(
                    "La clave JWT debe tener al menos 32 bytes para HS256");
        }

        SecretKey key = new SecretKeySpec(secret, "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
