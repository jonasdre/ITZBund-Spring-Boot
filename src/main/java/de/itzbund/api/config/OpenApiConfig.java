package de.itzbund.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration für OpenAPI/Swagger UI.
 * Definiert Metadaten für die API-Dokumentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Konfiguriert die OpenAPI-Spezifikation mit Titel, Version und Beschreibung.
     * Diese Werte überschreiben die automatisch generierten Metadaten.
     *
     * @return OpenAPI-Konfiguration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ITZBund Buecher API")
                        .version("1.0.0")
                        .description("API zur Verwaltung von Buechern."));
    }
}
