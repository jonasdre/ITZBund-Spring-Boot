package de.itzbund.service;

import de.itzbund.entity.Buch;
import de.itzbund.repository.BuchRepository;
import de.itzbund.error.DuplicateIsbnException;
import de.itzbund.error.VersionMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Konsolidierte Tests für {@link BuchService}. Alle zuvor verteilten Tests
 * (Speichern, Update/Version, Delegation, Suche, Branch-Kanten) in einer Datei.
 */
class BuchServiceTest {

    // ---------------------------------------------------- Save / Duplicate Logic

    @Test
    @DisplayName("Speichern mit eindeutiger ISBN")
    void saveUniqueIsbn() {
        BuchRepository repository = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repository);
        Buch b = Buch.builder()
            .title("A")
            .author("B")
            .isbn("1234567890")
            .pages(10)
            .price(BigDecimal.ONE)
            .build();
        Mockito.when(repository.save(any(Buch.class))).thenAnswer(inv -> {
            Buch e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });
        Buch saved = service.save(b);
        assertNotNull(saved.getId());
    }

    @Test
    @DisplayName("Speichern mit doppelter ISBN wirft DuplicateIsbnException")
    void saveDuplicateIsbn() {
        BuchRepository repository = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repository);
        Buch existing = Buch.builder()
            .id(5L)
            .title("X")
            .author("Y")
            .isbn("1234567890")
            .pages(10)
            .price(BigDecimal.ONE)
            .build();
        Mockito.when(repository.findByIsbn("1234567890")).thenReturn(Optional.of(existing));
        Buch neu = Buch.builder()
            .title("Neu")
            .author("Z")
            .isbn("1234567890")
            .pages(11)
            .price(BigDecimal.TEN)
            .build();
        assertThrows(DuplicateIsbnException.class, () -> service.save(neu));
    }

    @Test
    @DisplayName("save erlaubt gleiche ISBN bei identischer ID (Update)")
    void saveAllowsSameIdDuplicateIsbn() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Buch existing = Buch.builder()
            .id(5L)
            .isbn("111")
            .title("A")
            .author("B")
            .pages(1)
            .price(BigDecimal.ONE)
            .version(0L)
            .build();
        Mockito.when(repo.findByIsbn("111")).thenReturn(Optional.of(existing));
        Mockito.when(repo.save(any(Buch.class))).thenAnswer(i -> i.getArgument(0));
        Buch update = existing.toBuilder().title("Neu").build();
        Buch saved = service.save(update);
        assertEquals("Neu", saved.getTitle());
    }

    @Test
    @DisplayName("save wirft DuplicateIsbnException bei anderer ID")
    void saveThrowsForDifferentId() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Buch existing = Buch.builder()
            .id(1L)
            .isbn("dup")
            .title("A")
            .author("B")
            .pages(1)
            .price(BigDecimal.ONE)
            .version(0L)
            .build();
        Mockito.when(repo.findByIsbn("dup")).thenReturn(Optional.of(existing));
        Buch other = Buch.builder()
            .id(2L)
            .isbn("dup")
            .title("X")
            .author("Y")
            .pages(2)
            .price(BigDecimal.TEN)
            .version(0L)
            .build();
        assertThrows(DuplicateIsbnException.class, () -> service.save(other));
    }

    @Test
    @DisplayName("save überspringt Duplicate-Prüfung bei null ISBN")
    void saveSkipsDuplicateWhenIsbnNull() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Buch b = Buch.builder()
            .title("Ohne ISBN")
            .author("Anon")
            .pages(5)
            .price(BigDecimal.ONE)
            .version(0L)
            .build();
        Mockito.when(repo.save(any(Buch.class))).thenAnswer(i -> {
            Buch e = i.getArgument(0);
            e.setId(42L);
            return e;
        });
        Buch saved = service.save(b);
        assertNotNull(saved.getId());
        Mockito.verify(repo, Mockito.never()).findByIsbn(Mockito.anyString());
    }

    // ---------------------------------------------------- Update / Version Handling

    @Test
    @DisplayName("Update mit richtiger Version")
    void updateWithCorrectVersion() {
        BuchRepository repository = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repository);
        Buch existing = Buch.builder()
            .id(1L)
            .title("Alt")
            .author("A")
            .isbn("1111111111")
            .pages(10)
            .price(BigDecimal.ONE)
            .version(2L)
            .build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(any(Buch.class))).thenAnswer(i -> i.getArgument(0));
        Buch updated = service.updateWithVersionCheck(1L, 2L, b -> b.setTitle("Neu"));
        assertEquals("Neu", updated.getTitle());
    }

    @Test
    @DisplayName("Update mit falscher Version wirft VersionMismatchException")
    void updateWithWrongVersion() {
        BuchRepository repository = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repository);
        Buch existing = Buch.builder()
            .id(1L)
            .title("Alt")
            .author("A")
            .isbn("1111111111")
            .pages(10)
            .price(BigDecimal.ONE)
            .version(3L)
            .build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));
        assertThrows(VersionMismatchException.class,
            () -> service.updateWithVersionCheck(1L, 2L, b -> { }));
    }

    @Test
    @DisplayName("updateWithVersionCheck gibt null zurück wenn Entity fehlt")
    void updateReturnsNullWhenMissing() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Mockito.when(repo.findById(99L)).thenReturn(Optional.empty());
        assertNull(service.updateWithVersionCheck(99L, 0L, b -> { }));
    }

    @Test
    @DisplayName("updateWithVersionCheck akzeptiert null expectedVersion (kein Vergleich)")
    void updateWithNullExpectedVersion() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Buch existing = Buch.builder()
            .id(10L)
            .title("Alt")
            .author("A")
            .pages(10)
            .price(BigDecimal.ONE)
            .version(7L)
            .build();
        Mockito.when(repo.findById(10L)).thenReturn(Optional.of(existing));
        Mockito.when(repo.save(any(Buch.class))).thenAnswer(i -> i.getArgument(0));
        Buch updated = service.updateWithVersionCheck(10L, null, b -> b.setTitle("Neu"));
        assertEquals("Neu", updated.getTitle());
    Mockito.verify(repo).save(existing);
    }

    // ---------------------------------------------------- Delegation (find*)

    private Buch sample() {
        return Buch.builder()
            .id(7L)
            .title("Delegation")
            .author("Tester")
            .isbn("1112223334")
            .pages(10)
            .price(BigDecimal.ONE)
            .version(0L)
            .build();
    }

    @Test
    @DisplayName("findById liefert Optional mit Entity")
    void findByIdPresent() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Buch b = sample();
        Mockito.when(repo.findById(7L)).thenReturn(Optional.of(b));
        assertTrue(service.findById(7L).isPresent());
    }

    @Test
    @DisplayName("findById liefert Optional.empty bei Nichtfund")
    void findByIdMissing() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Mockito.when(repo.findById(99L)).thenReturn(Optional.empty());
        assertTrue(service.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findByAuthor delegiert an Repository")
    void findByAuthor() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Mockito.when(repo.findByAuthor("Tester")).thenReturn(List.of(sample()));
        assertEquals(1, service.findByAuthor("Tester").size());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase delegiert an Repository")
    void findByTitleContainingIgnoreCase() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Mockito.when(repo.findByTitleContainingIgnoreCase("dele")).thenReturn(List.of(sample()));
        assertEquals(1, service.findByTitleContainingIgnoreCase("dele").size());
    }

    @Test
    @DisplayName("findAll(Specification) delegiert an Repository")
    void findAllWithSpecification() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Specification<Buch> spec = (root, q, cb) -> cb.conjunction();
        Mockito.when(repo.findAll(spec)).thenReturn(List.of());
        assertNotNull(service.findAll(spec));
        Mockito.verify(repo).findAll(spec);
    }

    // ---------------------------------------------------- Suche (kombiniert)

    @Test
    @DisplayName("searchAuthorAndTitle delegiert an Repository Methode")
    void searchAuthorAndTitle() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        Buch b = Buch.builder()
            .id(1L)
            .title("Spring Patterns")
            .author("Craig Walls")
            .isbn("1234567890")
            .pages(100)
            .price(BigDecimal.TEN)
            .version(0L)
            .build();
        Mockito.when(repo.findByAuthorContainingIgnoreCaseAndTitleContainingIgnoreCase("craig", "spring"))
            .thenReturn(List.of(b));
        List<Buch> result = service.searchAuthorAndTitle("craig", "spring");
        assertEquals(1, result.size());
        assertEquals("Spring Patterns", result.get(0).getTitle());
    Mockito.verify(repo).findByAuthorContainingIgnoreCaseAndTitleContainingIgnoreCase("craig", "spring");
    }

    // ---------------------------------------------------- Exists / Delete

    @Test
    @DisplayName("existsById delegiert ans Repository")
    void existsDelegates() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        Mockito.when(repo.existsById(5L)).thenReturn(true);
        BuchService service = new BuchService(repo);
    assertTrue(service.existsById(5L));
    Mockito.verify(repo).existsById(5L);
    }

    @Test
    @DisplayName("deleteById delegiert ans Repository")
    void deleteDelegates() {
        BuchRepository repo = Mockito.mock(BuchRepository.class);
        BuchService service = new BuchService(repo);
        service.deleteById(9L);
        Mockito.verify(repo).deleteById(9L);
    }
}
