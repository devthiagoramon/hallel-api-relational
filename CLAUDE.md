# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hallel API is a Spring Boot 3.4.7 REST API for managing a Catholic community platform (Comunidade Catolica Hallel). It handles events, ministries, user management, payments (Mercado Pago), real-time messaging (WebSocket/STOMP), push notifications (Firebase FCM), file storage (Google Cloud Storage), and email notifications.

**Tech stack:** Java 21, Spring Boot 3.4.7, PostgreSQL, Flyway migrations, Gradle, Lombok, MapStruct, JWT auth, TestContainers.

## Build & Run Commands

```bash
./gradlew build                    # Build the project
./gradlew bootRun                  # Run the application (port 8080)
./gradlew test                     # Run all tests
./gradlew test --tests "*.ClassName"  # Run a single test class
./gradlew test --tests "*.ClassName.methodName"  # Run a single test method
./gradlew flywayMigrate            # Run Flyway migrations manually
```

**Dev prerequisites:** PostgreSQL running locally on port 5432, database `hallel-db`, user `postgres`, password `root`. The `dev` profile is active by default.

**Integration tests** use TestContainers (PostgreSQL 16-alpine). Extend `AbstractIntegrationTest` for automatic container setup. REST Assured is used for HTTP assertions.

## Architecture

Base package: `br.hallel.relational.api.app`

### Domain Modules

Each module follows the pattern: `controller/` -> `service/` -> `repository/` -> `model/`, with `dto/`, `dto/mapper/`, and `exception/handler/` sub-packages.

| Module | Purpose |
|--------|---------|
| `event` | Event lifecycle, participation, scales, food sales, transactions |
| `ministry` | Ministries, scales, auditions, functions, dances, repertory, scale chat |
| `user` | User profiles, roles, activity tracking |
| `association` | Associate membership and payment status |
| `payment` | Mercado Pago integration (Checkout Pro + Transparent), PIX, webhooks |
| `auth` | Login, signup, Google OAuth, token refresh |
| `security` | JWT management, RBAC, filters, SecurityConfig |
| `email` | Thymeleaf-templated email notifications |
| `messaging` | Firebase Cloud Messaging for mobile push notifications |
| `schedules` | Cron jobs (event status updates, email reminders, fee corrections) |
| `global` | Cross-cutting: WebSocket config, Google Cloud Storage, PDF generation, global exception handler |

### REST API Path Convention

Controllers are organized by role-based access:

- `/public/**` - No auth required
- `/auth/**` - Authentication endpoints (login, signup, OAuth, token refresh)
- `/user/**` - Requires `ROLE_USER`
- `/admin/**` - Requires `ROLE_ADMIN` (or specific: `ADMIN_EVENT`, `ADMIN_MINISTRY`, `ADMIN_USER`)
- `/coordinator/**` - Requires `COORDINATOR`, `VICE_COORDINATOR`, or `EXTERNAL_COORDINATOR` (separate filter chain)
- `/payment/**` - Payment webhooks

### Security Architecture

Two filter chains (see `SecurityConfig.java`):
1. **Order(1) - Coordinator chain** (`/coordinator/**`): `MinistryCoordinatorFilter` + CORS
2. **Order(2) - Main chain** (everything else): `JwtTokenFilter` + CORS

JWT tokens: 30-day expiry for users, 1-day for coordinators. Token provider in `security/utils/JwtTokenProvider.java`.

### WebSocket (STOMP)

Configured in `global/config/WebSocketConfig.java`. Endpoints:
- `/ws-scale-chat` - Ministry scale chat (requires USER role)
- `/ws-auth`, `/ws-payment`, `/ws-food-payments` - Public

Broker destinations: `/topic/*` (broadcast), `/queue/*` (unicast). App prefix: `/api`.

### Data Mapping

- **MapStruct** (preferred): `componentModel = SPRING`, `unmappedTargetPolicy = ERROR`. Mappers in `dto/mapper/` per module.
- **ModelMapper**: Legacy usage in some modules.

### Database Migrations

Flyway migrations in `src/main/resources/db/migration/` (V1 through V64+). Schema uses PostgreSQL enums extensively. `ddl-auto: none` — all schema changes must go through Flyway.

### Exception Handling

Each module has its own `@RestControllerAdvice` handler in `exception/handler/`. Global handler in `global/exception/handler/GlobalExceptionHandler.java`. Standard response format: `ExceptionResponse(message, timestamp, description)`.

### External Integrations

- **Mercado Pago**: SDK 2.2.0, webhook at `/public/payments/webhooks/mercadopago`
- **Google Cloud Storage**: Image upload/delete via `GoogleBucketService` (bucket: `hallel-bucket`)
- **Firebase FCM**: Push notifications via `hallel-messaging-firebase.json`
- **Email**: Gmail SMTP via Spring Mail + Thymeleaf templates in `src/main/resources/templates/`

## API Documentation

Swagger UI available at `/swagger-ui.html` (no auth required). OpenAPI spec at `/v3/api-docs`.
