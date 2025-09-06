package de.itzbund.service;

import de.itzbund.entity.Buch;
import de.itzbund.repository.BuchRepository;
import de.itzbund.service.exception.DuplicateIsbnException;
import de.itzbund.service.exception.VersionMismatchException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

@Service
public class BuchService {

    /** Repository für den Datenzugriff auf {@link Buch}. */
    private final BuchRepository repository;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BuchService.class);

    /**
     * Konstruktor Injection.
     * @param repository Buch-Repository
     */
    public BuchService(final BuchRepository repository) {
        this.repository = repository;
    }

    /** Speichert oder aktualisiert ein Buch (inkl. Duplicate-ISBN-Prüfung). */
    public Buch save(final Buch buch) {
        if (buch.getIsbn() != null) {
            repository.findByIsbn(buch.getIsbn())
                .filter(existing -> !existing.getId().equals(buch.getId()))
                .ifPresent(b -> {
                    throw new DuplicateIsbnException(buch.getIsbn());
                });
        }
        Buch saved = repository.save(buch);
        LOGGER.debug("Gespeichert Buch id={} isbn={}", saved.getId(), saved.getIsbn());
        return saved;
    }

    /** Sucht ein Buch per ID. */
    public Optional<Buch> findById(final Long id) {
        return repository.findById(id);
    }

    /** Liefert alle Bücher. */
    public List<Buch> findAll() {
        return repository.findAll();
    }

    /** Liefert alle Bücher anhand einer Specification. */
    public List<Buch> findAll(final Specification<Buch> spec) {
        return repository.findAll(spec);
    }

    /** Sucht Bücher nach Autor (exakte Übereinstimmung). */
    public List<Buch> findByAuthor(final String author) {
        return repository.findByAuthor(author);
    }

    /** Sucht Bücher deren Titel den übergebenen Teilstring (case-insensitive) enthält. */
    public List<Buch> findByTitleContainingIgnoreCase(final String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Kombinierte Suche über Author und Title (beide enthalten, case-insensitive).
     * @param author teilweiser Autor
     * @param title teilweiser Titel
     * @return Liste gefundener Bücher
     */
    public List<Buch> searchAuthorAndTitle(final String author, final String title) {
        return repository.findByAuthorContainingIgnoreCaseAndTitleContainingIgnoreCase(author, title);
    }

    /**
     * Aktualisiert ein Buch falls vorhanden und Version passend ist.
     * @param id Buch-ID
     * @param expectedVersion erwartete Version (Pflicht extern)
     * @param mutator Änderungslambda
     * @return aktualisierte Entität oder null falls nicht gefunden
     * @throws VersionMismatchException bei Versionskonflikt
     */
    public Buch updateWithVersionCheck(final Long id,
                                       final Long expectedVersion,
                                       final java.util.function.Consumer<Buch> mutator) {
        Buch entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        Long current = entity.getVersion();
        if (expectedVersion != null && !expectedVersion.equals(current)) {
            throw new VersionMismatchException(id, expectedVersion, current);
        }
        mutator.accept(entity);
        return save(entity);
    }

    /** Prüft ob ein Buch mit der ID existiert. */
    public boolean existsById(final Long id) {
        return repository.existsById(id);
    }

    /** Löscht ein Buch per ID. */
    public void deleteById(final Long id) {
        repository.deleteById(id);
    }
}
