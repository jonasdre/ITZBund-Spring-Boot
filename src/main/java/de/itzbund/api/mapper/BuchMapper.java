package de.itzbund.api.mapper;

import de.itzbund.api.dto.BuchDtos;
import de.itzbund.entity.Buch;

/**
 * Mapper zwischen Buch Entity und DTOs.
 */
public final class BuchMapper {
    private BuchMapper() { }

    /** Map CreateRequest zu Entity. */
    public static Buch toEntity(final BuchDtos.CreateRequest dto) {
        return Buch.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .pages(dto.getPages())
                .price(dto.getPrice())
                .build();
    }

    /** Überträgt UpdateRequest Felder auf bestehende Entity. */
    public static void updateEntity(final Buch entity, final BuchDtos.UpdateRequest dto) {
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setIsbn(dto.getIsbn());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
    }

    /** Map Entity zu Response DTO. */
    public static BuchDtos.Response toResponse(final Buch entity) {
        return BuchDtos.Response.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .pages(entity.getPages())
                .price(entity.getPrice())
                .version(entity.getVersion())
                .build();
    }
}
