package de.itzbund.repository;

import de.itzbund.entity.Buch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Zentrales Repository für {@link Buch} mit zusätzlichen Such-Methoden.
 */
public interface BuchRepository extends JpaRepository<Buch, Long>, JpaSpecificationExecutor<Buch> {
    List<Buch> findByAuthor(String author);
    List<Buch> findByTitleContainingIgnoreCase(String title);
    Optional<Buch> findByIsbn(String isbn);
    List<Buch> findByAuthorContainingIgnoreCaseAndTitleContainingIgnoreCase(String author, String title);
}
