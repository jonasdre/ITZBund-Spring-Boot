package de.itzbund.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Zentrale DTO Definitionen für Buch-API.
 * Klein gehalten für Demo-Zwecke.
 */
public final class BuchDtos {

    private BuchDtos() { }

    /** Create Buch Request DTO. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class CreateRequest {
    /** Buchtitel. */
        @NotBlank
        @Size(max = 200)
        private String title;
    /** Autor des Buches. */
        @NotBlank
        @Size(max = 100)
        private String author;
    /** ISBN (10-17 Zeichen inkl. Trennstriche X/x erlaubt). */
        @Pattern(regexp = "[0-9Xx-]{10,17}", message = "ISBN muss 10–13 Stellen haben (Bindestriche erlaubt)")
        private String isbn;
    /** Seitenanzahl (>0). */
    @Positive
        private Integer pages;
    /** Preis (>0). */
    @DecimalMin(value = "0.01", message = "Preis muss > 0 sein")
        private BigDecimal price;
    }

    /** Update Buch Request DTO. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class UpdateRequest {
    /** Buchtitel. */
        @NotBlank
        @Size(max = 200)
        private String title;
    /** Autor des Buches. */
        @NotBlank
        @Size(max = 100)
        private String author;
    /** ISBN (10-17 Zeichen inkl. Trennstriche X/x erlaubt). */
    @Pattern(regexp = "[0-9Xx-]{10,17}", message = "ISBN muss 10–13 Stellen haben (Bindestriche erlaubt)")
        private String isbn;
    /** Seitenanzahl (>0). */
    @Positive
        private Integer pages;
    /** Preis (>0). */
    @DecimalMin(value = "0.01", message = "Preis muss > 0 sein")
        private BigDecimal price;
        /** Erwartete Version für optimistisches Locking. */
        @NotNull
        private Long version;
    }

    /** Response DTO. */
    @Data
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class Response {
    /** Primärschlüssel. */
        private Long id;
    /** Buchtitel. */
        private String title;
    /** Autor des Buches. */
        private String author;
    /** ISBN. */
        private String isbn;
    /** Seitenanzahl. */
        private Integer pages;
    /** Preis. */
        private BigDecimal price;
    /** Aktuelle Version für Optimistic Locking. */
        private Long version;
    }
}
