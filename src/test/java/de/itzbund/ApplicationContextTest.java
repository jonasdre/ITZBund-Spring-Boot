package de.itzbund;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Einfacher Kontext-Test: Startet den Spring Boot ApplicationContext
 * und verifiziert, dass das Haupt-Application Bean vorhanden ist.
 */
@SpringBootTest
class ApplicationContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextStartsAndHasApplicationBean() {
        assertThat(context).isNotNull();
        assertThat(context.getBean(Application.class)).isNotNull();
    }
}
