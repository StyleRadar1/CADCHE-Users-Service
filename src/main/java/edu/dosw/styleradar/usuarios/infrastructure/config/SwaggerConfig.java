package edu.dosw.styleradar.usuarios.infrastructure.config;

import java.util.List;
import java.util.Set;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    private static final Set<String> INTERNAL_FALLBACK_PATHS = Set.of(
            "/internal/api/v1/usuarios/verificar-credenciales",
            "/api/v1/internal/usuarios/{id}/correo-verificado"
    );

    @Bean
    OpenAPI usersServiceOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT emitido por ms-auth (HS256)"))
                        .addSecuritySchemes("serviceBasicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Credenciales de servicio interno configuradas en INTERNAL_AUTH_USERNAME y INTERNAL_AUTH_PASSWORD")))
                .servers(List.of(
                        new Server().url("/")
                                .description("Servidor donde se sirve Swagger"),
                        new Server().url("http://localhost:8120")
                                .description("Servidor local ms-usuarios"),
                        new Server().url("http://localhost:8080")
                                .description("API Gateway StyleRadar"),
                        new Server().url("https://styleradar-qa-app.azurewebsites.net")
                                .description("Servidor QA ms-usuarios"),
                        new Server().url("https://styleradar-prod-app.azurewebsites.net")
                                .description("Servidor PROD ms-usuarios")))
                .info(new Info()
                        .title("StyleRadar - Users Service API (ms-usuarios)")
                        .description("Especificación OpenAPI 3.0 del microservicio de gestión de usuarios de StyleRadar. "
                                + "Gobierna el registro de compradores (SR-RF-01), preferencias de estilo (SR-RF-03), tallas habituales (SR-RF-04), "
                                + "feed personalizado (SR-RF-05), recomendaciones (SR-RF-06), búsquedas guardadas (SR-RF-28) y administración de estados de cuenta (SR-RF-66).")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipo CADche - StyleRadar DOSW")
                                .email("cadche@styleradar.com")));
    }

    @Bean
    OpenApiCustomizer usersAuthenticationDocumentation() {
        Set<String> publicPosts = Set.of("/api/v1/users");
        return api -> {
            if (api.getPaths() == null) {
                return;
            }
            api.getPaths().forEach((path, item) -> item.readOperationsMap().forEach((method, operation) -> {
                boolean publicRoute = method == PathItem.HttpMethod.POST && publicPosts.contains(path);
                String scheme = path.startsWith("/api/v1/internal/")
                        || path.startsWith("/internal/api/v1/")
                        ? "serviceBasicAuth" : "bearerAuth";
                operation.setSecurity(publicRoute
                        ? List.of()
                        : List.of(new SecurityRequirement().addList(scheme)));
            }));
            INTERNAL_FALLBACK_PATHS.forEach(api.getPaths()::remove);
        };
    }
}
