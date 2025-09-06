package de.itzbund.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguriert die OpenAPI Metadaten der Bücher-API (Titel, Version, Beschreibung, Kontakt).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Erstellt das OpenAPI Objekt für Swagger UI. Nicht zur Erweiterung gedacht.
     * @return OpenAPI Definition
     */
    @Bean
    public OpenAPI buecherApi() {
        Info info = new Info()
            .title("Bücher API")
            .version("1.0.0")
            .description("REST API für Verwaltung und Suche von Büchern.")
            .contact(new Contact()
                .name("ITZBund")
                .email("info@example.org"))
            .license(new License().name("Proprietary"));

        return new OpenAPI()
            .components(new Components())
            .info(info);
    }
}
