package de.itzbund;

import de.itzbund.api.generated.dto.BuchCreateRequest;
import de.itzbund.api.generated.dto.BuchUpdateRequest;
import de.itzbund.api.generated.dto.BuchResponse;
import de.itzbund.entity.Buch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deckt Lombok-generierte Methoden (equals/hashCode/toString) ab,
 * um Coverage für die Fachdatenklassen zu erhalten.
 */
class LombokCoverageTest {

    private Buch sampleEntity() {
        return Buch.builder()
            .id(1L)
            .title("Titel")
            .author("Autor")
            .isbn("1234567890")
            .pages(10)
            .price(BigDecimal.ONE)
            .version(0L)
            .build();
    }

    @Test
    @DisplayName("Entity equals/hashCode/toString")
    void entityEqualsHashCode() {
        Buch a = sampleEntity();
        Buch b = sampleEntity().toBuilder().build();
        Buch different = sampleEntity().toBuilder().id(2L).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, different);
        String ts = a.toString();
        assertTrue(ts.contains("Titel"));
    }

    @Test
    @DisplayName("Generated DTOs basic setters/getters")
    void generatedDtos() {
    BuchCreateRequest create = new BuchCreateRequest()
        .title("T").author("A").isbn("1111111111").pages(5).price(BigDecimal.ONE);
    assertEquals("T", create.getTitle());
    BuchUpdateRequest update = new BuchUpdateRequest()
        .title("T").author("A").isbn("1111111111").pages(5).price(BigDecimal.ONE).version(1L);
    assertEquals(1L, update.getVersion());
    BuchResponse resp = new BuchResponse()
        .id(1L).title("T").author("A").isbn("1111111111").pages(5).price(BigDecimal.ONE).version(2L);
    assertEquals(2L, resp.getVersion());
    assertTrue(resp.toString().contains("T"));
    }
}
