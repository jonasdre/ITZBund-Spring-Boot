# ITZBund Spring Boot

## Quick Run

```bash
mvn spring-boot:run
# oder
mvn clean package && java -jar target/itzbund-spring-boot-projekt-1.0.0.jar
```

Swagger UI: http://localhost:8080/swagger-ui/index.html  |  OpenAPI JSON: /v3/api-docs

## Überblick

Eine Spring Boot Anwendung zur Verwaltung von Büchern mit vollständiger OpenAPI REST-API. Das Projekt demonstriert moderne Java-Entwicklung mit Spring Boot 3, Lombok, strukturierter Validierung und Tests.

## Funktionalitäten

Die Anwendung bietet ein vollständiges CRUD-System für Bücher:
- Bücher erstellen, lesen, aktualisieren und löschen
- Suchfunktionen nach Autor und Titel
- Automatische API-Dokumentation mit OpenAPI
- H2-Datenbank für lokale Entwicklung

## Technischer Stack

**Backend-Framework:**
- Spring Boot 3.3.3 mit Java 17
- Spring Data JPA für Datenpersistierung
- H2-Datenbank (In-Memory für Entwicklung)

**Code-Optimierung:**
- Lombok zur Reduktion von Boilerplate
- springdoc-openapi zur automatischen API-Dokumentation (Swagger UI)

**Testing & Qualität:**
- JUnit 5 mit umfassender Test-Suite
- Mockito für Unit-Tests
- Checkstyle für Code-Standards
- JaCoCo für Test-Coverage-Analyse
  - Coverage Quality Gate: Maven verify erzwingt LINE-Coverage >= 80% (jacoco:check). Build schlägt fehl, wenn Schwelle unterschritten wird.

## Projektarchitektur

```
src/main/java/de/itzbund/
├── Application.java          # Spring Boot Starter
├── api/
│   ├── dto/BuchDtos.java     # Create/Update/Response DTOs
│   ├── mapper/BuchMapper.java# Entity↔DTO Mapping
│   └── error/GlobalExceptionHandler.java # Zentrale Fehlerbehandlung
├── controller/
│   └── BuchController.java   # REST-Endpunkte (/api/buecher)
├── entity/
│   └── Buch.java             # JPA Entity (mit @Version für Optimistic Locking)
├── service/
│   ├── BuchService.java      # Geschäftslogik + Duplicate ISBN & Version Check
│   ├── exception/            # Domänenspezifische Exceptions
├── repository/
│   └── BuchRepository.java   # JPA Repository Interface
```

Hinweis: Die API verwendet dedizierte DTOs (`BuchDtos`) für Create/Update/Response um Versionierung (Optimistic Locking via `version` Feld) und Validierungsregeln klar von der Persistenz zu trennen. `@JsonInclude(Include.NON_NULL)` sorgt für schlanke JSON-Antworten.

## REST-API Spezifikation

| Methode | Endpunkt | Beschreibung | Query / Body |
|---------|----------|--------------|--------------|
| GET | `/api/buecher` | Alle oder gefilterte Bücher | `author`, `title` (optional) |
| POST | `/api/buecher` | Neues Buch anlegen | JSON Body (siehe unten) |
| GET | `/api/buecher/{id}` | Einzelnes Buch | Pfadvariable `id` |
| PUT | `/api/buecher/{id}` | Buch aktualisieren (inkl. erwarteter Version) | Pfadvariable `id`, JSON Body |
| DELETE | `/api/buecher/{id}` | Buch löschen | Pfadvariable `id` |

**Beispiel JSON-Payload (Create):**
```json
{
  "title": "Spring Boot in Action",
  "author": "Craig Walls",
  "isbn": "9781617292545",
  "pages": 472,
  "price": 39.99
}
```

Update-Beispiel (PUT) mit Optimistic Locking (Version Pflichtfeld):
```json
{
  "title": "Spring Boot in Action - 2nd Edition",
  "author": "Craig Walls",
  "isbn": "9781617292545",
  "pages": 500,
  "price": 45.99,
  "version": 1
}
```

## Fehler- & Antwortcodes

| HTTP Code | Situation | Beschreibung | Typische Response (verkürzt) |
|-----------|-----------|--------------|------------------------------|
| 200 OK | Erfolgreiche Lese-/List-Operation | Daten gefunden / Liste (auch leer) | `{ "id": 1, "title": "..." }` oder `[]` |
| 201 Created | Erfolgreich angelegt (POST) | Location-Header zeigt Ressource | `{ "id": 5, "title": "..." }` |
| 204 No Content | Erfolgreich gelöscht | Kein Body | *(leer)* |
| 400 Bad Request | Validierungsfehler Request-Body | Pflichtfelder / Constraints verletzt | `{ "status":400, "error":"validation", "messages":["title: darf nicht leer sein"] }` |
| 404 Not Found | Buch existiert nicht | ID unbekannt | `{ "status":404, "error":"not_found", "message":"Buch 99 nicht gefunden" }` |
| 409 Conflict | Fachkonflikt (Duplicate ISBN / VersionMismatch) | ISBN bereits vergeben ODER Version passt nicht | `{ "status":409, "error":"duplicate_isbn" }` / `{ "status":409, "error":"version_mismatch" }` |
| 500 Internal Server Error | Unerwarteter Fehler | Fallback Handler | `{ "status":500, "error":"internal" }` |

Fehlerstrukturen werden zentral in `GlobalExceptionHandler` erzeugt. Vereinfachtes Schema:

```json
{
  "status": 409,
  "error": "duplicate_isbn",
  "message": "ISBN bereits vorhanden",
  "timestamp": "2025-09-06T14:25:10.123Z"
}
```

Validierungsfehler bündeln mehrere Feldmeldungen (`messages` Array). Business-Konflikte (Duplicate / Version) liefern sprechenden `error`-Key für Clients.

## Setup und Installation

**Voraussetzungen:**
- Java 17 oder höher
- Maven 3.6+

**Schnellstart:**
```bash
# Repository klonen
git clone <repository-url>
cd ITZBund-Spring-Boot-Projekt

# Anwendung starten
mvn spring-boot:run
```

**Wichtige URLs (Standardport 8080):**
- REST-API: `http://localhost:8080/api/buecher`
- H2-Konsole: `http://localhost:8080/h2-console` (falls aktiviert)
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

**Datenbank-Verbindung (H2):**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `itzbund`
- Passwort: (leer)

## Entwicklung und Testing

**Test-Ausführung:**
```bash
mvn test                         # Alle Tests
mvn test -Dtest=ApplicationTest  # Spring Context Test
mvn test -Dtest=BuchControllerTest # Controller Tests
```

**Code-Qualität:**
```bash
mvn checkstyle:check       # Style-Prüfung
mvn jacoco:report          # Coverage-Report
mvn clean verify           # Vollständige Validierung
```

**Vorhandene Tests (Auswahl):**
- `ApplicationTest` – Minimaler Context Load
- `SmokeApplicationTest` – Startet ganze App, prüft Erreichbarkeit `/v3/api-docs` & `/api/buecher`
- `BuchControllerTest` – REST-API (MockMvc) inkl. Validierung, Fehlerszenarien, Suchpfade (author/title/beides)
- `BuchService*` Tests – Geschäftslogik (Suche, Duplicate ISBN, Versionen, Delegation)
- `BuchMapperTest`, `LombokCoverageTest` – Mapping & Lombok Methoden

## Build und Deployment

Die Anwendung verwendet Maven für Build-Management und enthält eine Jenkins-Pipeline für CI/CD.

**Build-Prozess:**
1. **Compile** - `mvn clean compile`
2. **Test** - `mvn test` mit JUnit-Reports
3. **Quality Check** - `mvn checkstyle:check`
4. **Package** - `mvn package` erstellt ausführbare JAR

**Pipeline-Konfiguration (Jenkinsfile):**
- Automatischer Checkout aus Git
- Build- und Test-Execution
- Code-Quality-Analyse mit Checkstyle
- Artefakt-Archivierung

**Ausführung als JAR:**
```bash
mvn clean package
java -jar target/itzbund-spring-boot-projekt-1.0.0.jar
```

## Konfiguration

**Datenbank-Setup:**
Die Anwendung ist standardmäßig für H2-In-Memory konfiguriert. Produktionsumgebungen können über `application.properties` auf PostgreSQL umgestellt werden.

**Lombok-Integration:**
Für Entwicklung ist das Lombok-Plugin in der IDE erforderlich sowie aktivierte Annotation-Processing.

**Design-Prinzipien:**
- Schlanke Schichten (Controller → Service → Repository)
- DTO-Schicht zur Entkopplung & Validierung
- Optimistic Locking (`@Version`)
- Testabdeckung für zentrale Pfade + Smoke Test
- Lesbarkeit und einfache Erweiterbarkeit

## Production Hinweise

Für produktive Deployments sollten einige Properties angepasst bzw. ergänzt werden:

```properties
spring.jpa.hibernate.ddl-auto=validate   # statt update
spring.datasource.url=jdbc:postgresql://HOST:5432/DB
spring.datasource.username=...          
spring.datasource.password=...          
management.endpoints.web.exposure.include=health,info
```

Zusätzlich: Datenbank-Migrationen via Flyway oder Liquibase einführen, Security (Spring Security) für geschützte Endpunkte, Logging-Konfiguration (JSON / zentrale Aggregation) und evtl. Observability (Micrometer + Prometheus/Grafana) ergänzen.

## Smoke Test

`SmokeApplicationTest` dient als schneller Integrationsindikator: Start der Applikation, Aufruf der OpenAPI-Dokumentation & Basis-Endpoint. Er erhöht Robustheit bei Refactorings.

## OpenAPI Hinweise
Alle relevanten Felder der DTOs erscheinen im Schema. Interne technische Felder können durch DTO-Anpassungen oder Jackson-Annotationen ausgeblendet werden.

## Ausblick
- Sicherheitslayer (Spring Security / Auth)
- Flyway/Liquibase Migrationen statt `ddl-auto=update`
- Pagination & Caching für Listen-Endpunkte
