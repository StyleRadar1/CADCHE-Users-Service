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

    @Bean
    OpenAPI usersServiceOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token JWT emitido por ms-auth (HS256)")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Servidor local ms-usuarios"),
                        new Server().url("http://localhost:8080").description("API Gateway StyleRadar")))
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
        Set<String> publicPosts = Set.of("/api/v1/usuarios");
        return api -> {
            if (api.getPaths() == null) {
                return;
            }
            api.getPaths().forEach((path, item) -> item.readOperationsMap().forEach((method, operation) -> {
                boolean publicRoute = method == PathItem.HttpMethod.POST && publicPosts.contains(path);
                operation.setSecurity(publicRoute
                        ? List.of()
                        : List.of(new SecurityRequirement().addList("bearerAuth")));
            }));
        };
    }
}

