package de.itzbund.mapper;

import de.itzbund.api.generated.dto.BuchCreateRequest;
import de.itzbund.api.generated.dto.BuchUpdateRequest;
import de.itzbund.api.generated.dto.BuchResponse;
import de.itzbund.entity.Buch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BuchMapperTest {

    @Test
    @DisplayName("toEntity mappt CreateRequest korrekt")
    void toEntityMapsFields() {
    BuchCreateRequest req = new BuchCreateRequest()
        .title("Test")
        .author("Author")
        .isbn("1234567890")
        .pages(10)
        .price(BigDecimal.ONE);
        Buch entity = BuchMapper.toEntity(req);
        assertEquals("Test", entity.getTitle());
        assertEquals("Author", entity.getAuthor());
        assertEquals("1234567890", entity.getIsbn());
        assertEquals(10, entity.getPages());
        assertEquals(BigDecimal.ONE, entity.getPrice());
    }

    @Test
    @DisplayName("updateEntity überträgt Felder")
    void updateEntityCopiesFields() {
        Buch entity = Buch.builder()
                .title("Old")
                .author("OldA")
                .isbn("111")
                .pages(1)
                .price(BigDecimal.ONE)
                .build();
    BuchUpdateRequest upd = new BuchUpdateRequest()
        .title("New")
        .author("NewA")
        .isbn("222")
        .pages(2)
        .price(BigDecimal.TEN)
        .version(0L);
        BuchMapper.updateEntity(entity, upd);
        assertEquals("New", entity.getTitle());
        assertEquals("NewA", entity.getAuthor());
        assertEquals("222", entity.getIsbn());
        assertEquals(2, entity.getPages());
        assertEquals(BigDecimal.TEN, entity.getPrice());
    }

    @Test
    @DisplayName("toResponse mappt Entity korrekt")
    void toResponseMapsFields() {
        Buch entity = Buch.builder()
                .id(5L)
                .title("X")
                .author("Y")
                .isbn("123")
                .pages(3)
                .price(BigDecimal.ONE)
                .version(7L)
                .build();
    BuchResponse resp = BuchMapper.toResponse(entity);
        assertEquals(5L, resp.getId());
        assertEquals("X", resp.getTitle());
        assertEquals("Y", resp.getAuthor());
        assertEquals("123", resp.getIsbn());
        assertEquals(3, resp.getPages());
        assertEquals(BigDecimal.ONE, resp.getPrice());
        assertEquals(7L, resp.getVersion());
    }
}
