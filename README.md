# Cafe Table Reservation API

REST API for a single cafe's table reservation system, built with Spring Boot 3 (Java 17).

## Tech stack

- **Spring Web** — REST controllers
- **Spring Data JPA** + **PostgreSQL** — persistence
- **Spring Security** + **JWT (jjwt)** — stateless authentication & role-based authorization
- **Lombok** — boilerplate reduction on entities
- **Commons CSV** — bulk import of tables from CSV
- **Bean Validation** — request DTO validation

## Architecture (layered)

```
controller   ->  thin HTTP layer: validation + delegation
service      ->  business logic + transaction boundaries
repository   ->  Spring Data JPA data access
model        ->  JPA entities (never leave the service layer)
dto          ->  request/response contracts (records)
mapper       ->  entity <-> DTO conversion
security     ->  JWT filter, JwtService, UserDetails, SecurityConfig
exception    ->  domain exceptions + @RestControllerAdvice (uniform ApiError)
```

Key design rules:
- JPA entities never cross the controller boundary — only DTOs do.
- Double-booking is prevented in `ReservationService` via an overlap query.
- Bulk import reports **partial success** (`ImportResult`) instead of a binary outcome.

## Package layout

```
com.cafe.reservation
├── ReservationApiApplication
├── controller   (Auth, CafeTable, Reservation, Import)
├── service      (Auth, CafeTable, Reservation, Import)
├── repository   (User, CafeTable, Reservation)
├── model        (User, CafeTable, Reservation, enums)
├── dto          (auth, table, reservation, ImportResult)
├── mapper       (CafeTableMapper, ReservationMapper)
├── security     (JwtService, JwtAuthenticationFilter, SecurityConfig, ...)
└── exception    (GlobalExceptionHandler, ApiError, custom exceptions)
```

## Configuration

Configured via environment variables (see `src/main/resources/application.yml`):

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` / `DB_PORT` | `localhost` / `5432` | PostgreSQL host/port |
| `DB_NAME` | `cafe_reservation` | Database name |
| `DB_USER` / `DB_PASSWORD` | `postgres` / `1111` | DB credentials |
| `JWT_SECRET` | (dev default) | **Base64-encoded** 256-bit signing key — override in prod |

## Build & run

Requires JDK 17+ and Maven 3.9+.

```bash
# Start PostgreSQL (example via Docker)
docker run --name cafe-db -e POSTGRES_DB=cafe_reservation \
  -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16

# Build and run
mvn clean spring-boot:run
```

## REST endpoints

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/api/auth/login` | public | Login, returns JWT |
| GET | `/api/tables` | public | List tables |
| GET | `/api/tables/available` | public | List available tables |
| GET | `/api/tables/{id}` | public | Get table |
| POST/PUT/DELETE | `/api/tables/**` | ADMIN | Manage tables |
| GET | `/api/reviews` | public | List reviews |
| GET | `/api/reservations/my` | USER | My reservations |
| GET | `/api/reservations/{id}` | owner/ADMIN | Get reservation |
| POST | `/api/reservations` | USER | Create reservation |
| POST | `/api/reservations/{id}/cancel` | owner/ADMIN | Cancel reservation |
| POST | `/api/entities/import` | ADMIN | Import tables (CSV) |

## Import file format

Columns (header row required): `tableNumber`, `capacity`, `locationNote`, `status`.
See [`docs/sample-tables.csv`](docs/sample-tables.csv). `status` is `AVAILABLE` or `OUT_OF_SERVICE`.
