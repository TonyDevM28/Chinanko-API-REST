package chinanko.chinanko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Definimos el nombre del esquema de seguridad
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Añadimos el requerimiento de seguridad globalmente
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                
                // 2. Definimos los componentes de seguridad (JWT)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                // 3. Información general de la API
                .info(new Info()
                        .title("Chinanko API")
                        .version("1.0.0")
                        .description("API para Chinanko - turismo de pueblos mágicos. \n\n" +
                                     "**Nota:** Para probar los endpoints, obtén un token del servicio de Auth (puerto 8082) y pégalo en el botón 'Authorize'.")
                        .contact(new Contact().name("Chinanko Team").email("support@chinanko.example"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}