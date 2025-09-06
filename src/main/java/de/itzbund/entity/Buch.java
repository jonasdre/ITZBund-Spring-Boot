package de.itzbund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Buch {

    /** Die eindeutige Kennung für das Buch. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Der Titel des Buches. */
    @NotBlank
    @Size(max = 200)
    private String title;

    /** Der Autor des Buches. */
    @NotBlank
    @Size(max = 100)
    private String author;

    /** Die ISBN-Nummer des Buches. */
    @Size(max = 13)
    @Pattern(regexp = "[0-9Xx-]{10,17}", message = "ISBN muss 10–13 Stellen haben (Bindestriche erlaubt)")
    @Column(unique = true, length = 17)
    private String isbn;

    /** Die Anzahl der Seiten im Buch. */
    @Positive(message = "Seitenzahl muss positiv sein")
    private Integer pages;

    /** Der Preis des Buches. */
    @Positive(message = "Preis muss positiv sein")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /** Optimistische Locking Spalte. */
    @Version
    private Long version;
}
