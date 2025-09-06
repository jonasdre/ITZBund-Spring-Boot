package de.itzbund;

import de.itzbund.api.dto.BuchDtos;
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
    @DisplayName("DTO Create/Update/Response equals/hashCode")
    void dtoEqualsHashCode() {
        BuchDtos.CreateRequest c1 = BuchDtos.CreateRequest.builder()
            .title("T")
            .author("A")
            .isbn("1111111111")
            .pages(5)
            .price(BigDecimal.ONE)
            .build();
        BuchDtos.CreateRequest c2 = BuchDtos.CreateRequest.builder()
            .title("T")
            .author("A")
            .isbn("1111111111")
            .pages(5)
            .price(BigDecimal.ONE)
            .build();
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        BuchDtos.UpdateRequest u1 = BuchDtos.UpdateRequest.builder()
            .title("T")
            .author("A")
            .isbn("1111111111")
            .pages(5)
            .price(BigDecimal.ONE)
            .version(1L)
            .build();
        BuchDtos.UpdateRequest u2 = BuchDtos.UpdateRequest.builder()
            .title("T")
            .author("A")
            .isbn("1111111111")
            .pages(5)
            .price(BigDecimal.ONE)
            .version(1L)
            .build();
        assertEquals(u1, u2);

        BuchDtos.Response r1 = BuchDtos.Response.builder()
            .id(1L)
            .title("T")
            .author("A")
            .isbn("1111111111")
            .pages(5)
            .price(BigDecimal.ONE)
            .version(2L)
            .build();
        BuchDtos.Response r2 = BuchDtos.Response.builder()
            .id(1L)
            .title("T")
            .author("A")
            .isbn("1111111111")
            .pages(5)
            .price(BigDecimal.ONE)
            .version(2L)
            .build();
        assertEquals(r1, r2);
        assertTrue(r1.toString().contains("T"));
    }
}
