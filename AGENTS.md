# AGENTS.md - Expense Pulse Backend

## Build & Development Commands

```bash
# Start dev mode with live reload
./mvnw quarkus:dev

# Package application
./mvnw package

# Package as uber-jar
./mvnw package -Dquarkus.package.jar.type=uber-jar

# Run tests (all)
./mvnw test

# Run a single test class
./mvnw test -Dtest=CategoryApiTest

# Run a single test method
./mvnw test -Dtest=CategoryApiTest#testCreaCategory

# Run tests with specific pattern
./mvnw test -Dtest="*ApiTest"

# Build native executable
./mvnw package -Dnative

# Native build via container (no GraalVM needed)
./mvnw package -Dnative -Dquarkus.native.container-build=true

# Run application
java -jar target/quarkus-app/quarkus-run.jar
```

## Project Structure

```
src/main/java/org/acme/
├── api/              # REST endpoints (CategoryApi, TransactionsApi, LayoutApi)
├── category/         # Category domain (entity, service, repository, DTOs, mapper)
├── transaction/      # Transaction domain (entity, service, repository, DTOs, mapper)
├── layout/           # Layout domain (entity, service, repository, DTOs, mapper)
├── layoutItem/       # LayoutItem sub-domain
├── exception/        # Custom exceptions and JAX-RS ExceptionMappers
└── util/             # Shared utilities (mappers, pagination)
```

## Code Style Guidelines

### Imports

- Use `jakarta.*` imports (Jakarta EE, not javax)
- Group imports: `jakarta.*`, then third-party (lombok, quarkus, bson), then project imports
- Project imports use full path: `org.acme.category.dto.*`, `org.acme.exception.*`
- Never use wildcard imports for project classes

### Formatting

- Indentation: 4 spaces (no tabs)
- Line length: aim for 120 characters, max 150
- Braces: K&R style (opening brace on same line)
- Use Lombok annotations (`@Data`, `@Builder`, `@Slf4j`, `@NoArgsConstructor`, `@AllArgsConstructor`) to reduce boilerplate
- Prefer `lombok.extern.slf4j.Slf4j` over `@CommonsLog`

### Types & Validation

- Use `java.time.LocalDate`/`LocalDateTime` for dates (not String)
- Use `org.bson.types.ObjectId` for MongoDB document IDs
- Validate DTOs with Jakarta Validation: `@Valid`, `@NotBlank`, `@NotNull`, `@Positive`, `@Min`
- Annotate API parameters with `@Valid` to trigger validation
- Use `@JsonDeserialize` with builder pattern for immutable DTOs

### Naming Conventions

- **Classes**: PascalCase (`CategoryApi`, `TransactionRepository`)
- **Methods**: mix of Italian/English - follow existing pattern per domain:
  - Category: `elenco()`, `crea()`, `aggiornaCategory()`, `cancella()`
  - Transaction: `createTransaction()`, `ricerca()`, `aggiornaTransaction()`, `cancella()`
  - Layout: `getAllLayouts()`, `findLayoutByName()`, `createLayout()`, `updateLayout()`, `deleteLayout()`
- **DTOs**: `*DTO` suffix (`CategoryDTO`, `TransactionResponseDTO`)
- **Entities**: no suffix (`Category`, `Transaction`, `Layout`)
- **Response objects**: `*Response` suffix (`CategoryResponse`, `TransactionResponse`)
- **Mappers**: `*MapperImpl` suffix, implement interface `*Mapper` if needed
- **Packages**: lowercase, domain-based (`category`, `transaction`, `layout`)

### Dependency Injection & Scoping

- Use `@Inject` (Jakarta CDI), not `@Autowired`
- Service classes: `@ApplicationScoped` (default for stateless services)
- Avoid `@Model` (combines `@Named` + `@RequestScoped`) unless request-scoped state is needed
- Repository classes: Panache MongoDB (no explicit scope needed)
- Mapper classes: `@ApplicationScoped`
- REST APIs: no scope annotation needed (default is `@RequestScoped`)

### Exception Handling

- Use custom exceptions: `NotFoundException`, `ServiceException`, `BadRequestException`, `ApplicationException`
- Always preserve stack traces: `throw new ServiceException(message, cause)` not `throw new ServiceException(e.getMessage())`
- Catch `NotFoundException` separately and re-throw before catching generic `Exception`
- Map exceptions to HTTP responses via `ExceptionMapper` classes:
  - `NotFoundExceptionMapper` → 404
  - `BadRequestExceptionMapper` → 400
  - `ApplicationExceptionMapper` → 500
  - `IllegalArgumentExceptionMapper` → 400
- Log with `@Slf4j`: `log.info("[ClassName.methodName] message")`, `log.error("[ClassName.methodName] error", ex)`

### REST API Patterns

- Base path: `/api/v1` (configured in `application.yml`)
- Resource paths: `/categories`, `/transactions`, `/layouts`
- Return `Response` objects (not direct entities) for HTTP control
- Use proper status codes: 200 (OK), 201 (Created with `Response.created()`), 204 (No Content), 400 (Bad Request), 404 (Not Found), 500 (Server Error)
- Set `Location` header on 201 responses: `Response.created(URI.create("/resource/" + id)).build()`
- Document with OpenAPI: `@Operation`, `@APIResponses`, `@APIResponse`
- Use `@PathParam` for path parameters, `@Valid` for request bodies

### Persistence (MongoDB Panache)

- Entities: `@MongoEntity(collection="COLLECTION_NAME")` with Lombok `@Data`
- Use `ObjectId` for IDs, call `.toHexString()` when returning to client
- Repository methods: `persist()`, `findByIdOptional()`, `deleteById()`, `update()`, `listAll()`
- Query building: use `PanacheQuery` with `and()` for filters
- Define MongoDB indexes in `MongoIndexConfig` class

### Testing

- Framework: JUnit 5 + RestAssured (`@QuarkusTest`)
- Test class naming: `*ApiTest` for REST endpoint tests
- Use `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` for ordered tests
- Store created IDs in `static` fields to chain create→read→update→delete tests
- Test naming: `testCrea*`, `testElenco*`, `testAggiorna*`, `testDelete*`, `test*SenzaCampiObbligatori`
- Assertions: use RestAssured's `statusCode()`, `body()` with Hamcrest matchers
- DevServices: MongoDB automatically started for tests (no config needed)

### Configuration

- Main config: `src/main/resources/application.yml`
- Profile `%prod` for production settings
- MongoDB connection: use environment variable `QUARKUS_MONGODB_CONNECTION_STRING` with fallback
- CORS configured under `quarkus.http.cors.*`
- Logging: `quarkus.log.*` properties, use DEBUG for `org.acme` category

### General

- Remove commented-out code before committing
- No `System.out.println` - use `log.debug/info/warn/error`
- Use `@RegisterForReflection` on DTOs used in native mode
- JSON: use `@JsonIgnoreProperties(ignoreUnknown = true)` on entities for forward compatibility
