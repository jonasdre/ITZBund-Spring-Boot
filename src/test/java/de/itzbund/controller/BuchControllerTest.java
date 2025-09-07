package de.itzbund.controller;

import de.itzbund.entity.Buch;
import de.itzbund.error.DuplicateIsbnException;
import de.itzbund.error.VersionMismatchException;
import de.itzbund.api.generated.dto.BuchUpdateRequest;
import de.itzbund.api.generated.dto.BuchResponse;
import de.itzbund.repository.BuchRepository;
import de.itzbund.service.BuchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(BuchController.class)
class BuchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuchService service;

    private Buch buch1;

    @BeforeEach
    void init() {
        buch1 = Buch.builder()
                .id(1L)
                .title("Spring Boot in Action")
                .author("Craig Walls")
                .isbn("9781617292545")
                .pages(472)
                .price(BigDecimal.valueOf(39.99))
                .version(1L)
                .build();
    }

    @Test
    @DisplayName("POST create returns 201 + body")
    void create() throws Exception {
        Mockito.when(service.save(any(Buch.class))).thenReturn(buch1);
                                String createJson = """
                                                {
                                                        "title": "Spring Boot in Action",
                                                        "author": "Craig Walls",
                                                        "isbn": "9781617292545",
                                                        "pages": 472,
                                                        "price": 39.99
                                                }
                                                """;
        mockMvc.perform(post("/api/buecher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/buecher/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Spring Boot in Action")));
    }

    @Test
    @DisplayName("GET /{id} 200 when found")
    void getFound() throws Exception {
        Mockito.when(service.findById(1L)).thenReturn(Optional.of(buch1));
        mockMvc.perform(get("/api/buecher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", is("Craig Walls")));
    }

    @Test
    @DisplayName("GET /{id} 404 when not found")
    void getNotFound() throws Exception {
        Mockito.when(service.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/buecher/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET list returns all books")
    void listAll() throws Exception {
        Buch buch2 = Buch.builder()
                .id(2L)
                .title("Clean Code")
                .author("Robert Martin")
                .isbn("9780132350884")
                .pages(464)
                .price(BigDecimal.valueOf(35.99))
                .version(1L)
                .build();
        Mockito.when(service.findAll()).thenReturn(List.of(buch1, buch2));
        mockMvc.perform(get("/api/buecher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].title", is("Clean Code")));
    }

    @Test
    @DisplayName("GET list filtered by author")
    void listByAuthor() throws Exception {
        Mockito.when(service.findByAuthor("Craig Walls"))
                .thenReturn(List.of(buch1));
        mockMvc.perform(get("/api/buecher").param("author", "Craig Walls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author", is("Craig Walls")));
    }

    @Test
    @DisplayName("GET list filtered by title")
    void listByTitle() throws Exception {
        Mockito.when(service.findByTitleContainingIgnoreCase("Spring"))
                .thenReturn(List.of(buch1));
        mockMvc.perform(get("/api/buecher").param("title", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", containsString("Spring Boot")));
    }

        @Test
        @DisplayName("GET list filtered by author AND title")
        void listByAuthorAndTitle() throws Exception {
                Mockito.when(service.searchAuthorAndTitle("Craig", "Spring"))
                        .thenReturn(List.of(buch1));
                mockMvc.perform(get("/api/buecher").param("author", "Craig").param("title", "Spring"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].author", containsString("Craig")))
                        .andExpect(jsonPath("$[0].title", containsString("Spring")));
        }

    @Test
    @DisplayName("PUT update returns 200 with updated entity")
    void updateOk() throws Exception {
        Buch updated = buch1.toBuilder()
                .title("Spring Boot in Action - 2nd Edition")
                .pages(500)
                .build();
        Mockito.when(service.updateWithVersionCheck(eq(1L), eq(1L), any()))
                .thenReturn(updated);
                                String updateJson = """
                                                {
                                                        "title": "Spring Boot in Action - 2nd Edition",
                                                        "author": "Craig Walls",
                                                        "isbn": "9781617292545",
                                                        "pages": 500,
                                                        "price": 39.99,
                                                        "version": 1
                                                }
                                                """;
        mockMvc.perform(put("/api/buecher/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Spring Boot in Action - 2nd Edition")))
                .andExpect(jsonPath("$.pages", is(500)));
    }

    @Test
    @DisplayName("PUT update returns 404 when not found")
    void updateNotFound() throws Exception {
        Mockito.when(service.updateWithVersionCheck(eq(999L), eq(1L), any()))
                .thenReturn(null);
                                String notFoundJson = """
                                                {
                                                        "title": "X",
                                                        "author": "Y",
                                                        "isbn": "1234567890",
                                                        "pages": 10,
                                                        "price": 9.99,
                                                        "version": 1
                                                }
                                                """;
        mockMvc.perform(put("/api/buecher/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notFoundJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE returns 204 when entity exists")
    void deleteOk() throws Exception {
        Mockito.when(service.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/buecher/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE returns 404 when entity missing")
    void deleteMissing() throws Exception {
        Mockito.when(service.existsById(42L)).thenReturn(false);
        mockMvc.perform(delete("/api/buecher/42"))
                .andExpect(status().isNotFound());
    }

        @Test
        @DisplayName("POST create returns 409 on duplicate ISBN")
        void createDuplicateIsbn() throws Exception {
                Mockito.when(service.save(any(Buch.class)))
                        .thenThrow(new DuplicateIsbnException("9781617292545"));
                String json = """
                        {
                          "title": "Spring Boot in Action",
                          "author": "Craig Walls",
                          "isbn": "9781617292545",
                          "pages": 100,
                          "price": 10.00
                        }
                        """;
                mockMvc.perform(post("/api/buecher")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.status", is(409)));
        }

                @Test
                @DisplayName("POST create returns 500 on unhandled exception (GlobalExceptionHandler fallback)")
                void createUnhandledException() throws Exception {
                        Mockito.when(service.save(any(Buch.class)))
                                .thenThrow(new RuntimeException("Boom"));
                        String json = """
                                {
                                  "title": "Spring Boot in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617292545",
                                  "pages": 120,
                                  "price": 49.99
                                }
                                """;
                        mockMvc.perform(post("/api/buecher")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status", is(500)))
                                .andExpect(jsonPath("$.message", containsString("Boom")));
                }

        @Test
        @DisplayName("PUT update returns 412 on version mismatch")
        void updateVersionMismatch() throws Exception {
                Mockito.when(service.updateWithVersionCheck(eq(1L), eq(1L), any()))
                        .thenThrow(new VersionMismatchException(1L, 1L, 2L));
                String json = """
                        {
                          "title": "Spring Boot in Action - 2nd Edition",
                          "author": "Craig Walls",
                          "isbn": "9781617292545",
                          "pages": 500,
                          "price": 39.99,
                          "version": 1
                        }
                        """;
                mockMvc.perform(put("/api/buecher/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isPreconditionFailed())
                        .andExpect(jsonPath("$.status", is(412)));
        }

        @Test
        @DisplayName("POST create returns 400 on validation error (missing title)")
        void createValidationError() throws Exception {
                String invalid = """
                        {
                          "title": "",
                          "author": "Craig Walls",
                          "isbn": "9781617292545",
                          "pages": 50,
                          "price": 5.00
                        }
                        """;
                mockMvc.perform(post("/api/buecher")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalid))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.fields", hasItem(containsString("title"))));
        }

        @Test
        @DisplayName("(Direct) update executes mutator lambda and changes fields")
        void updateLambdaExecuted() {
                BuchRepository repo = Mockito.mock(BuchRepository.class);
                BuchService realService = new BuchService(repo);
                Buch original = Buch.builder()
                        .id(10L)
                        .title("Old Title")
                        .author("Author")
                        .isbn("9999999999")
                        .pages(100)
                        .price(BigDecimal.TEN)
                        .version(1L)
                        .build();
                Mockito.when(repo.findById(10L)).thenReturn(Optional.of(original));
                Mockito.when(repo.save(any(Buch.class))).thenAnswer(inv -> inv.getArgument(0));

                BuchController controller = new BuchController(realService);
                BuchUpdateRequest req = new BuchUpdateRequest()
                        .title("New Title")
                        .author("Author")
                        .isbn("9999999999")
                        .pages(111)
                        .price(BigDecimal.TEN)
                        .version(1L);

                ResponseEntity<BuchResponse> response = controller.updateBuch(10L, req);

                assertEquals(200, response.getStatusCode().value());
                assertNotNull(response.getBody());
                BuchResponse body = response.getBody();
                assertNotNull(body);
                assertEquals("New Title", body.getTitle());
                assertEquals(111, body.getPages());
                assertEquals("New Title", original.getTitle());
                assertEquals(111, original.getPages());
        }
}
