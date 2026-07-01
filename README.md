<p align="center">
  <h1 align="center">Sportsbook API Gateway</h1>

  <a href="https://github.com/bicosteve/api-gateway/actions/workflows/cicd.yml">
    <img src="https://github.com/bicosteve/api-gateway/actions/workflows/cicd.yml/badge.svg" alt="CICD" />
  </a>
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Redis-7.0-red?logo=redis" alt="Redis" />
  <img src="https://img.shields.io/badge/Docker-bixoloo%2Fapi--gateway-2496ED?logo=docker" alt="Docker" />
  <img src="https://img.shields.io/badge/tests-250%20passing-success" alt="Tests" />
  <img src="https://img.shields.io/badge/License-Portfolio-lightgrey" alt="License" />

  <br />
  <br />

  A production-grade RESTful API backend for a sports betting platform, built with <strong>Spring Boot 3.5</strong> and <strong>Java 21</strong>. It serves as the central API gateway handling user authentication, sports event management, bet placement with odds computation, wallet operations, and payment processing via <a href="https://chapa.co/">Chapa</a>.
</p>

---

## Problem

In Kenya, the Government (GoK) raised the **minimum bet amount from 1 KES to 20 KES**. That 20x jump prices out a segment of players: people who *want* to place a bet but don't have the required cash on hand to meet the new minimum stake.

**This platform addresses that gap with a bonus-first onboarding model:**

- Every newly **verified account is awarded a welcome freebet** (bonus balance) at signup.
- Bets can be placed using either **real balance or bonus balance** (`is_bonus` flag on every bet), so a cash-short user can still meet the 20 KES minimum stake using their bonus.
- The **wallet tracks `balance` and `bonus` separately**, letting the platform seed promotional funds without touching real money and giving new users a frictionless path to their first bet.

The result: users who couldn't previously afford the raised minimum can still participate, and the platform has a built-in acquisition mechanism aligned with the regulatory change.

---

## Live Demo

> **Live API Docs (Swagger UI):** `https://<your-deployed-host>/swagger-ui.html`
>
> _TODO: replace `<your-deployed-host>` with the deployed base URL. When running locally, the docs are available at [http://localhost:5001/swagger-ui.html](http://localhost:5001/swagger-ui.html)._

---

## Screenshots / Demo

> _TODO: add real assets under `docs/screenshots/` and reference them below._

| Swagger UI | Bet Slip Flow |
|---|---|
| ![Swagger UI](docs/screenshots/swagger-ui.png) | ![Bet flow](docs/screenshots/bet-flow.gif) |

_To add media:_

```bash
mkdir -p docs/screenshots
# drop swagger-ui.png, bet-flow.gif, etc. into docs/screenshots/
```

---

## Table of Contents

- [Problem](#problem)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Routes Overview](#routes-overview)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [CI/CD](#cicd)
- [Security Notes](#security-notes)
- [Known Limitations & Future Improvements](#known-limitations--future-improvements)
- [Author](#author)

---

## Features

### Authentication & Authorization
- **JWT-based authentication** with a dual-token strategy (short-lived access token + long-lived refresh token)
- Refresh token returned in an **HttpOnly cookie** for XSS protection
- **Phone number-based registration** with OTP verification delivered via Mailgun
- **BCrypt password hashing** and stateless session management

### Bet Management
- **Multi-slip bet creation** with automatic total-odds and possible-win computation
- **Duplicate-event validation** — prevents multiple slips on the same event
- **Expired-event validation** — rejects bets on events that have already started
- **Bonus vs. real balance** staking via the `is_bonus` flag (enables the freebet model above)
- **Time-based filtering** (day / week / month / all) with offset-based pagination and `hasNext` / `hasPrevious` navigation
- Efficient N+1-free bet-slip joins via custom `ResultSetExtractor`

### Event Management
- **Upcoming events feed** with nested teams, markets, participants, prices, and scores
- **Single event detail** view with full market depth
- Complex multi-table JOINs resolved via custom `ResultSetExtractor` implementations
- UTC-based filtering — only future events are returned

### Payment Processing (Chapa)
- **Deposit initiation** via Chapa-hosted checkout
- **Webhook handling** with **HMAC-SHA256 signature verification** to reject spoofed callbacks
- **Idempotent processing** — duplicate webhooks are safely ignored
- **Automatic wallet crediting** and a full transaction audit trail

### Wallet System
- Separate **real balance and bonus** tracking per profile
- **Non-negative balance constraint** enforced at the database level
- Credit/debit operations with transaction history

### Error Handling & Validation
- **Global exception handler** (`@RestControllerAdvice`) with typed domain exceptions
- **Custom validators** (phone format, password matching, slip uniqueness) + Jakarta Bean Validation
- Structured error responses with status codes, timestamps, and field errors

### API Documentation
- **SpringDoc OpenAPI 3** with Swagger UI and detailed per-endpoint annotations

---

## Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.11 |
| **Security** | Spring Security 6, JWT (jjwt 0.12.6), BCrypt |
| **Database** | MySQL 8+ (InnoDB, utf8mb4) |
| **Data Access** | Spring JdbcTemplate (raw SQL with custom RowMappers & ResultSetExtractors) |
| **Caching** | Redis (Spring Data Redis) — OTP storage with TTL |
| **Payment Gateway** | Chapa |
| **Email Service** | Mailgun (OTP delivery) |
| **API Docs** | SpringDoc OpenAPI 2.8.9 (Swagger UI) |
| **Validation** | Jakarta Validation API, custom validators |
| **Build Tool** | Maven (with Maven Wrapper) |
| **Testing** | JUnit 5, Mockito, Spring Security Test |
| **Observability** | Logback + Logstash JSON encoder → Grafana Alloy → Loki |
| **Containerization** | Docker (multi-stage), Docker Compose |
| **CI/CD** | GitHub Actions → Docker Hub → SSH deploy |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│ Client (Web/Mobile)                                     │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTPS
                           ▼
┌─────────────────────────────────────────────────────────┐
│ API Gateway (Spring Boot)                               │
│                                                         │
│ ┌─────────────┐ ┌──────────────┐ ┌───────────────┐      │
│ │ Controllers │ │   Security   │ │  Validation   │      │
│ │ (REST API)  │ │ (JWT Filter) │ │  (Custom +    │      │
│ │             │ │              │ │   Jakarta)    │      │
│ └──────┬───────┘ └──────────────┘ └───────────────┘      │
│        │                                                │
│ ┌──────▼───────┐ ┌──────────────┐ ┌───────────────┐      │
│ │  Services   │ │   Payments   │ │  Exceptions   │      │
│ │ (Business    │ │    (Chapa    │ │   (Global    │      │
│ │   Logic)     │ │   Service)   │ │   Handler)   │      │
│ └──────┬───────┘ └──────┬───────┘ └───────────────┘      │
│        │                │                               │
│ ┌──────▼────────────────▼───────┐ ┌───────────────┐      │
│ │       Repository Layer        │ │    Mappers    │      │
│ │ (JdbcTemplate + RowMappers +  │ │  (DTO + Row)  │      │
│ │     ResultSetExtractors)      │ │               │      │
│ └──────┬─────────────────┬───────┘ └───────────────┘      │
│        │                 │                              │
└─────────┼─────────────────┼─────────────────────────────┘
          │                 │
    ┌─────▼─────┐     ┌──────▼──────┐
    │   MySQL   │     │    Redis    │
    │ (Primary  │     │ (OTP Cache  │
    │ Database) │     │  with TTL)  │
    └───────────┘     └─────────────┘
```

| Layer | Responsibility |
|---|---|
| **Controllers** | REST endpoints, request/response mapping, Swagger annotations |
| **Security** | JWT filter chain, token generation/validation, authentication provider |
| **Services** | Business logic orchestration, transaction management |
| **Payments** | Chapa integration, webhook signature verification, deposit processing |
| **Repository** | Raw SQL via JdbcTemplate, custom RowMappers and ResultSetExtractors |
| **Mappers** | DTO ↔ Model conversion, ResultSet → Domain object mapping |
| **Validation** | Custom constraint validators, Jakarta Bean Validation |
| **Exceptions** | Domain-specific exceptions, global error response formatting |

In production, application logs are emitted as JSON to stdout and shipped to Grafana Loki by a **Grafana Alloy** sidecar (see `docker-compose.prod.yml` and `alloy/config.alloy`).

---

## Project Structure

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/com/bicosteve/api_gateway/
│   │   │   ├── Main.java # Application entry point
│   │   │   ├── config/ # Chapa, Jackson, Mailgun, OpenAPI, Redis, RestTemplate, Security
│   │   │   ├── constants/ # Deposit/Transaction/Withdrawal status codes
│   │   │   ├── controllers/ # Auth, Bet, Event, Health, Profile, Wallet
│   │   │   ├── dto/
│   │   │   │   ├── requests/ # Inbound request DTOs
│   │   │   │   └── response/ # Outbound response DTOs
│   │   │   ├── enums/ # Java enums
│   │   │   ├── exceptions/ # Custom exceptions + GlobalExceptionHandler
│   │   │   ├── mappers/
│   │   │   │   ├── dtomappers/ # Model → DTO converters
│   │   │   │   └── rowmappers/ # ResultSet → Model mappers
│   │   │   ├── models/ # Domain models (POJOs)
│   │   │   ├── payments/ # ChapaService (initiateDeposit, handleWebhook)
│   │   │   ├── repository/ # JdbcTemplate data access
│   │   │   ├── security/ # JWT + Spring Security
│   │   │   ├── service/ # Business logic layer
│   │   │   ├── utils/ # LogContext, TrxRefGenerator, OTP, Mailgun helpers
│   │   │   └── validation/ # UniqueSlip custom validator
│   │   └── resources/
│   │       ├── application.yml # Base configuration
│   │       ├── application-dev.yml # Dev profile
│   │       ├── application-prod.yml # Prod profile (JSON logging, actuator)
│   │       ├── schema.sql # Database DDL
│   │       └── data.sql # Seed data (optional)
│   └── test/ # 250 JUnit 5 / Mockito tests (42 classes)
├── alloy/config.alloy # Grafana Alloy log-shipping config
├── Dockerfile # Multi-stage production image
├── Dockerfile.dev # Development image (hot reload)
├── docker-compose.yml # Local dev stack (app + MySQL + Redis)
├── docker-compose.prod.yml # Production stack (image + Alloy sidecar)
├── .github/workflows/cicd.yml # CI/CD pipeline
├── Makefile # Common dev commands
├── .example.env # Environment variable template
└── pom.xml
```

---

## Routes Overview

> Paths below reflect the actual controller mappings in `src/main/java/.../controllers`.

### Authentication — `/api/auth`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user account | No |
| `POST` | `/api/auth/verify-account` | Verify account with OTP (awards welcome freebet) | No |
| `POST` | `/api/auth/login` | Authenticate; returns access token + refresh token cookie | No |

### Events — `/api/events`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/events/all` | List upcoming events (paginated) | No |
| `GET` | `/api/events/{eventId}` | Get a single event with full market depth | No |

### Bets — `/api/bet`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/bet/create` | Place a new bet with slips | Yes |
| `GET` | `/api/bet/all` | List the user's bets (paginated, filterable) | Yes |
| `GET` | `/api/bet/{betId}` | Get a specific bet with all its slips | Yes |

### Wallet — `/api/wallet`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/wallet/deposit-chapa` | Initiate a deposit via Chapa | Yes |
| `POST` | `/api/wallet/webhook/chapa` | Chapa payment webhook callback (internal) | No |

### Profile — `/api/profile`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/profile/me` | Get the authenticated user's profile | Yes |

### Health — `/api/health`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/health/test` | Health check (used by the prod container healthcheck) | No |

> **Pagination query params:** `limit` (default 10, max 50), `offset` (default 0), `page` (from 0), `size` (default 10), `filter` (`day` \| `week` \| `month` \| `all`).

---

## Database Schema

```
┌──────────────┐     ┌─────────────────────┐
│   profile    │     │  profile_settings   │
├──────────────┤     ├─────────────────────┤
│ profile_id   │◄────│ profile_id (FK)     │
│ phone_number │     │ notifications       │
│ email        │     │ theme               │
│ password_hash│     │ language            │
│ status       │     │ created_at          │
│ is_verified  │     │ updated_at          │
│ is_deleted   │     └─────────────────────┘
│ created_at   │
│ updated_at   │
└──────┬───────┘
       │
       ├──────────────────────────────────────────┐
       │                                          │
┌──────▼───────┐     ┌──────────────┐     ┌────────▼────────┐
│     bets     │     │    wallet    │     │  transactions   │
├──────────────┤     ├──────────────┤     ├─────────────────┤
│ bet_id       │     │ id           │     │ id              │
│ profile_id   │     │ profile_id   │     │ profile_id      │
│ stake        │     │ balance      │     │ reference       │
│ is_bonus     │     │ bonus        │     │ type            │
│ status       │     │ created_by   │     │ amount          │
│ total_odds   │     └──────────────┘     │ status          │
│ possible_win │                          │ created_by      │
│ created_at   │     ┌──────────────┐     └─────────────────┘
│ updated_at   │     │   deposits   │
└──────┬───────┘     ├──────────────┤     ┌─────────────────┐
       │             │ id           │     │   withdrawals   │
┌──────▼───────┐     │ profile_id   │     ├─────────────────┤
│  bet_slips   │     │ trx_ref      │     │ id              │
├──────────────┤     │ amount       │     │ profile_id      │
│ bet_slip_id  │     │ currency     │     │ tx_ref          │
│ bet_id (FK)  │     │ checkout_url │     │ amount          │
│ event_id     │     │ chapa_ref    │     │ channel         │
│ sport_id     │     │ status       │     │ status          │
│ team_id      │     │ created_at   │     │ approved_by     │
│ market_id    │     │ updated_at   │     │ reason          │
│ market_name  │     └──────────────┘     └─────────────────┘
│ participant  │
│ odds         │
│ special_bet  │
│ status       │
│ created_at   │
│ updated_at   │
└──────────────┘
```

> **Note:** Event data (events, teams, markets, participants, prices, scores) is sourced from an external sports data provider and stored in separate tables (`rundown_event`, `teams`, `markets`, `participants`, `prices`, `scores`).

---

## Getting Started

### Prerequisites

- **Java 21+** (JDK)
- **MySQL 8.0+** and **Redis 7.0+** (or use Docker Compose below)
- **Maven 3.9+** (or the included wrapper `./mvnw`)
- **Chapa** and **Mailgun** API credentials (for payments and OTP email)

### Run with Docker Compose (recommended)

```bash
git clone https://github.com/bicosteve/api-gateway.git
cd api-gateway
cp .example.env .env # fill in your credentials

# Starts the app, MySQL, and Redis together
docker compose up --build
```

The API starts on `http://localhost:5001` (override with `APP_PORT` in `.env`).

### Run locally with Maven

```bash
cp .example.env .env # fill in your credentials, then export them or use a dotenv loader

# Using the Maven Wrapper
./mvnw spring-boot:run

# Or via Make
make run
```

Then open Swagger UI: [http://localhost:5001/swagger-ui.html](http://localhost:5001/swagger-ui.html)

### Environment variables

Key variables from `.example.env`:

```properties
APP_PORT=5001

# Database
DB_URL=jdbc:mysql://localhost:3306/sportsbook?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-256-bit-secret-key-here
ACCESS_TOKEN_EXPIRATION=3600000
REFRESH_TOKEN_EXPIRATION=604800000

# Chapa
CHAPA_BASE_URL=https://api.chapa.co/v1
CHAPA_SECRET_KEY=CHASECK_TEST-your_key_here
CHAPA_WEBHOOK_SECRET=your_generated_webhook_secret
CHAPA_CURRENCY=ETB
CHAPA_CALLBACK_URL=https://your-ngrok-url.ngrok-free.app/api/wallet/webhook/chapa
CHAPA_RETURN_URL=http://localhost:3000/deposit/success

# Mailgun
MAILGUN_API_KEY=your_mailgun_api_key
MAILGUN_BASE_URL=https://api.mailgun.net
MAILGUN_SANDBOX=your_mailgun_sandbox_domain
MAILGUN_FROM=noreply@yourdomain.com
```

---

## Running Tests

The suite is **250 tests across 42 classes**, written with **JUnit 5 + Mockito**. Tests use constructor-injected mocks (no live database required), so they run fast in CI and locally.

```bash
# Run the full test suite
./mvnw test

# Or via Make
make tests

# Run a single test class
make test # runs HealthCheckTest
./mvnw test -Dtest=com.bicosteve.api_gateway.service.BetServiceTest
```

Surefire reports are written to `target/surefire-reports/`. In CI these are uploaded as artifacts and evaluated by a **100%-pass-rate quality gate** (see below).

---

## CI/CD

[![CICD](https://github.com/bicosteve/api-gateway/actions/workflows/cicd.yml/badge.svg)](https://github.com/bicosteve/api-gateway/actions/workflows/cicd.yml)

The pipeline is defined in [`.github/workflows/cicd.yml`](.github/workflows/cicd.yml) and runs on pushes to `main`, `feat/*`, `fix/*`, and on pull requests.

```
test ──► quality-gate ──► build ──► docker-build-push ──► deploy ──► summary
```

| Job | What it does |
|---|---|
| **test** | Spins up MySQL + Redis service containers and runs the full suite; uploads Surefire reports |
| **quality-gate** | Parses the reports and **fails the pipeline unless the pass rate is 100%** |
| **build** | Packages the JAR and uploads it as an artifact |
| **docker-build-push** | On push to `main`, builds the image from the tested JAR and pushes to Docker Hub (`bixoloo/api-gateway`), tagged with the commit SHA and `latest` |
| **deploy** | Manual (`workflow_dispatch`) — SSHes to the VM, pulls the image, and restarts the Compose stack (app + Grafana Alloy sidecar) |
| **summary** | Prints a consolidated result for every job |

The Docker image is built from the **exact JAR that was tested**, so the deployed artifact matches what passed CI.

---

## Security Notes

### Authentication Flow

```
1. REGISTER
   Client → POST /api/auth/register (phone, password, email)
   Server → Creates unverified profile, generates OTP, stores it in Redis (TTL)
   Server → Sends OTP via Mailgun email

2. VERIFY
   Client → POST /api/auth/verify-account (phone, otp)
   Server → Validates OTP against Redis, marks profile verified
   Server → Creates wallet and awards the welcome freebet (bonus balance)

3. LOGIN
   Client → POST /api/auth/login (phone, password)
   Server → Returns accessToken + refreshToken (HttpOnly cookie)

4. AUTHENTICATED REQUEST
   Client → GET /api/bet/all (Authorization: Bearer <accessToken>)
   Server → JwtAuthenticationFilter validates the token and extracts claims
```

### Security Measures

| Measure | Implementation |
|---|---|
| **Password Storage** | BCrypt hashing with automatic salt generation |
| **Token Signing** | HMAC-SHA256 with a configurable secret key |
| **Session Management** | Stateless — no server-side sessions |
| **CSRF** | Disabled (stateless API using Bearer tokens) |
| **Refresh Tokens** | Delivered via HttpOnly cookie, inaccessible to JavaScript |
| **Webhook Verification** | HMAC-SHA256 signature verification on Chapa callbacks |
| **OTP Storage** | Redis with TTL, deleted after successful verification |
| **Input Validation** | Jakarta Bean Validation + custom validators on all endpoints |
| **SQL Injection** | Parameterized queries via JdbcTemplate PreparedStatement |
| **Error Exposure** | Structured responses — stack traces disabled in the `prod` profile |

---

## Known Limitations & Future Improvements

| Area | Notes |
|---|---|
| **Integration tests** | Current tests mock the persistence layer. The custom `ResultSetExtractor`/`RowMapper` SQL logic is not yet exercised against a real DB — add Testcontainers-based integration tests. |
| **Webhook unit coverage** | Chapa HMAC verification is tested indirectly via the controller; add a direct unit test for the signature logic. |
| **Deploy tag default** | The manual deploy defaults to the `latest` tag; prefer defaulting to a specific commit SHA for traceability. |
| **Rate limiting** | No throttling on auth endpoints yet — add to mitigate brute-force attempts. |
| **Withdrawal processing** | Withdrawal tables exist, but the full approval + Chapa disbursement workflow is not implemented. |
| **Wallet balance endpoint** | Balance is tracked but not yet exposed via a dedicated read endpoint. |
| **Cursor pagination** | Migrate from offset-based to cursor-based pagination for scale. |
| **API versioning** | Introduce URL-based versioning (`/api/v1/`) for backward-compatible changes. |
| **Security scanning** | Add dependency/image scanning (Dependabot, Trivy, CodeQL) to the pipeline. |
| **Multi-currency** | Extend wallet and payments beyond a single currency. |

---

## Author

**Bico Steve** — Lead Developer & Architect

- **GitHub:** [github.com/bicosteve](https://github.com/bicosteve)
- **Email:** bicosteve4@gmail.com
- **Timezone:** Africa/Nairobi (UTC+3)

---

## License

This project is intended for portfolio and educational purposes.

---

<p align="center">
  <strong>Built with Java 21 · Spring Boot 3.5 · MySQL · Redis · Chapa Payments</strong>
</p>
