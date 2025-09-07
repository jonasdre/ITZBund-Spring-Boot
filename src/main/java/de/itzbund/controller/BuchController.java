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
import de.itzbund.api.generated.api.BuecherApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BuchController implements BuecherApi {

    private final BuchService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(BuchController.class);

    public BuchController(final BuchService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<BuchResponse> createBuch(@Valid final BuchCreateRequest buchCreateRequest) {
        Buch saved = service.save(BuchMapper.toEntity(buchCreateRequest));
        LOGGER.info("Buch erstellt id={}", saved.getId());
        return ResponseEntity.created(URI.create("/api/buecher/" + saved.getId()))
            .body(BuchMapper.toResponse(saved));
    }

    @Override
    public ResponseEntity<BuchResponse> getBuch(final Long id) {
        return service.findById(id)
            .map(b -> ResponseEntity.ok(BuchMapper.toResponse(b)))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<BuchResponse>> listBuecher(final String author, final String title) {
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
        List<BuchResponse> response = books.stream()
            .map(BuchMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BuchResponse> updateBuch(final Long id, @Valid final BuchUpdateRequest buchUpdateRequest) {
        Buch updated = service.updateWithVersionCheck(
            id,
            buchUpdateRequest.getVersion(),
            entity ->
                BuchMapper.updateEntity(entity, buchUpdateRequest)
        );
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BuchMapper.toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteBuch(final Long id) {
        if (service.existsById(id)) {
            service.deleteById(id);
            LOGGER.info("Buch gelöscht id={}", id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
