package de.itzbund.mapper;

import de.itzbund.api.generated.dto.BuchCreateRequest;
import de.itzbund.api.generated.dto.BuchUpdateRequest;
import de.itzbund.api.generated.dto.BuchResponse;
import de.itzbund.entity.Buch;

/**
 * Utility-Mapper zwischen Buch-Entity und den generierten OpenAPI DTOs.
 */
public final class BuchMapper {
    private BuchMapper() { }

    /**
     * Erstellt aus einem {@link BuchCreateRequest} eine neue persistierbare {@link Buch} Entität.
     * Null-Felder werden unverändert (null) übernommen.
     * @param dto eingehendes Create DTO
     * @return neue Buch Entität (noch nicht gespeichert)
     */
    public static Buch toEntity(final BuchCreateRequest dto) {
        return Buch.builder()
            .title(dto.getTitle())
            .author(dto.getAuthor())
            .isbn(dto.getIsbn())
            .pages(dto.getPages())
            .price(dto.getPrice())
            .build();
    }

    /**
     * Überträgt (ersetzt) alle Felder aus einem {@link BuchUpdateRequest} auf eine bestehende {@link Buch}-Entität.
     * @param entity Ziel-Entity (wird mutiert)
     * @param dto Update-DTO mit neuen Werten
     */
    public static void updateEntity(final Buch entity, final BuchUpdateRequest dto) {
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setIsbn(dto.getIsbn());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
    }

    /**
     * Wandelt eine {@link Buch} Entität in ein {@link BuchResponse} DTO für die API-Ausgabe um.
     * @param entity Quell-Entity
     * @return Response DTO
     */
    public static BuchResponse toResponse(final Buch entity) {
        return new BuchResponse()
            .id(entity.getId())
            .title(entity.getTitle())
            .author(entity.getAuthor())
            .isbn(entity.getIsbn())
            .pages(entity.getPages())
            .price(entity.getPrice())
            .version(entity.getVersion());
    }
}
