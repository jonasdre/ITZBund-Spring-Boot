package de.itzbund;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Einfacher Smoke-Test: Startet gesamten Kontext auf zufälligem Port
 * und prüft, dass OpenAPI-Endpunkt und Bücher-Endpoint erreichbar sind.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    @DisplayName("Smoke: OpenAPI endpoint erreichbar")
    void openApiDocsReachable() {
        ResponseEntity<String> resp = rest.getForEntity("http://localhost:" + port + "/v3/api-docs", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("openapi");
    }

    @Test
    @DisplayName("Smoke: Bücher-Endpoint erreichbar (leer oder liste)")
    void booksEndpointReachable() {
        ResponseEntity<String> resp = rest.getForEntity("http://localhost:" + port + "/api/buecher", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
