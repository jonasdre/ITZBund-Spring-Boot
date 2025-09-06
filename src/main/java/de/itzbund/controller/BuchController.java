package de.itzbund.controller;
import jakarta.validation.Valid;
import de.itzbund.mapper.BuchMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.itzbund.entity.Buch;
import de.itzbund.service.BuchService;
import de.itzbund.api.generated.dto.BuchCreateRequest;
import de.itzbund.api.generated.dto.BuchUpdateRequest;
import de.itzbund.api.generated.dto.BuchResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** REST Controller für Buch-Ressourcen. */
@RestController
@RequestMapping("/api/buecher")
@Tag(name = "Bücher", description = "CRUD und Suchoperationen für Bücher")
public class BuchController {

    private final BuchService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(BuchController.class);

    public BuchController(final BuchService service) {
        this.service = service;
    }

    /**
     * Legt ein neues Buch an.
     * @param req Request DTO
     * @return Response mit erzeugtem Buch
     */
    @PostMapping
    @Operation(
        summary = "Neues Buch anlegen",
        description = "Erstellt ein neues Buch basierend auf den übergebenen Feldern.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Buch erstellt",
                content = @Content(schema = @Schema(implementation = BuchResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validierungsfehler"),
            @ApiResponse(responseCode = "409", description = "ISBN bereits vergeben")
        }
    )
    public ResponseEntity<BuchResponse> create(@Valid @RequestBody final BuchCreateRequest req) {
        Buch saved = service.save(BuchMapper.toEntity(req));
        LOGGER.info("Buch erstellt id={}", saved.getId());
        return ResponseEntity.created(URI.create("/api/buecher/" + saved.getId()))
            .body(BuchMapper.toResponse(saved));
    }

    /**
     * Holt ein Buch nach ID.
     * @param id Buch-ID
     * @return 200 oder 404
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Buch lesen",
        description = "Liest ein Buch anhand seiner ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Gefunden"),
            @ApiResponse(responseCode = "404", description = "Nicht gefunden")
        }
    )
    public ResponseEntity<BuchResponse> get(@PathVariable final Long id) {
        return service.findById(id)
            .map(b -> ResponseEntity.ok(BuchMapper.toResponse(b)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listet Bücher optional gefiltert nach author und/oder title (enthält jeweils Teilstring).
     * @param author optionaler Author-Filter
     * @param title optionaler Titel-Filter
     * @return Liste an Antwort-DTOs
     */
    @GetMapping
    @Operation(
        summary = "Bücher auflisten / suchen",
        description = "Listet alle Bücher oder filtert optional nach Autor und/oder Titel."
    )
    public List<BuchResponse> list(@RequestParam(required = false) final String author,
                           @RequestParam(required = false) final String title) {
        List<Buch> books;
        if (author != null && title != null) {
            books = service.searchAuthorAndTitle(author, title);
        } else if (author != null) {
            books = service.findByAuthor(author);
        } else if (title != null) {
            books = service.findByTitleContainingIgnoreCase(title);
        } else {
            books = service.findAll();
        }
        return books.stream()
            .map(BuchMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Aktualisiert ein Buch mit Version (optimistic Locking).
     * @param id Buch-ID
     * @param req Update DTO inkl. erwarteter Version
     * @return aktualisiertes Buch oder Fehler/404
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Buch aktualisieren",
        description = "Aktualisiert ein vorhandenes Buch unter Verwendung von Optimistic Locking.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Aktualisiert"),
            @ApiResponse(responseCode = "404", description = "Nicht gefunden"),
            @ApiResponse(responseCode = "412", description = "Versionskonflikt"),
            @ApiResponse(responseCode = "409", description = "ISBN Konflikt")
        }
    )
    public ResponseEntity<BuchResponse> update(@PathVariable final Long id,
                                                    @Valid @RequestBody final BuchUpdateRequest req) {
        Buch updated = service.updateWithVersionCheck(
            id,
            req.getVersion(),
            entity ->
                BuchMapper.updateEntity(entity, req)
        );
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BuchMapper.toResponse(updated));
    }

    /**
     * Löscht ein Buch.
     * @param id Buch-ID
     * @return 204 oder 404
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Buch löschen",
        description = "Löscht ein Buch anhand seiner ID.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Gelöscht"),
            @ApiResponse(responseCode = "404", description = "Nicht gefunden")
        }
    )
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        if (service.existsById(id)) {
            service.deleteById(id);
            LOGGER.info("Buch gelöscht id={}", id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
