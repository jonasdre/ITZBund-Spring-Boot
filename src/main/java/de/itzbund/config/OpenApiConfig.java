package de.itzbund.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration für OpenAPI/Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Erstellt eine benutzerdefinierte OpenAPI-Konfiguration.
     *
     * @return die OpenAPI-Konfiguration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ITZBund Bücher Verwaltung API")
                        .version("1.0.0")
                        .description("API zur Verwaltung von Büchern im ITZBund System"));
    }
}
