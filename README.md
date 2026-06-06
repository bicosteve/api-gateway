# 🏟️ Sportsbook API Gateway

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Redis](https://img.shields.io/badge/Redis-7.0-red?logo=redis)
![License](https://img.shields.io/badge/License-Portfolio-lightgrey)

A production-grade RESTful API backend for a sports betting platform, built with **Spring Boot 3.5** and **Java 21**. This service serves as the central API gateway handling user authentication, sports event management, bet placement with complex odds computation, wallet operations, and payment processing via [Chapa](https://chapa.co/) — a leading African payment gateway.

> **Live API Docs:** Swagger UI available at `/swagger-ui.html` when running locally.

---

## ⚡ Quick Start

```bash
git clone https://github.com/bicosteve/api-gateway.git
cd api-gateway
cp .example.env .env   # fill in your credentials
./mvnw spring-boot:run
```

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Key Design Decisions](#-key-design-decisions)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## ✨ Features

### 🔐 Authentication & Authorization
- **JWT-based authentication** with dual-token strategy (short-lived access token + long-lived refresh token)
- **Refresh token rotation** stored in HttpOnly cookies for enhanced security
- **Phone number-based registration** with OTP verification via Mailgun email service
- **BCrypt password hashing** for secure credential storage
- **Stateless session management** — no server-side session storage required

### 🎯 Bet Management
- **Multi-slip bet creation** with automatic total odds and possible win computation
- **Duplicate event validation** — prevents users from placing multiple slips on the same event
- **Expired event validation** — rejects bets on events that have already started
- **Time-based filtering** — filter bets by day, week, month, or all-time
- **Cursor-based pagination** with `hasNext`/`hasPrevious` navigation
- **Complex N+1 query resolution** using custom `ResultSetExtractor` for efficient bet-slip joins

### 📅 Event Management
- **Upcoming events feed** with nested teams, markets, participants, prices, and live scores
- **Single event detail** view with full market depth
- **Complex multi-table JOINs** resolved via custom `ResultSetExtractor` implementations
- **UTC-based event filtering** — only future events are returned

### 💳 Payment Processing (Chapa Integration)
- **Deposit initiation** with Chapa-hosted checkout page
- **Webhook handling** with **HMAC-SHA256 signature verification** to prevent spoofed callbacks
- **Idempotent transaction processing** — duplicate webhooks are safely ignored
- **Automatic wallet crediting** upon successful payment verification
- **Transaction audit trail** — every deposit is logged in the transactions table

### 💰 Wallet System
- **Balance and bonus tracking** per user profile
- **Non-negative balance constraint** enforced at the database level
- **Credit/debit operations** with full transaction history

### 🛡️ Error Handling & Validation
- **Global exception handler** (`@RestControllerAdvice`) with typed exceptions for every domain scenario
- **Custom validators** — phone number format, password matching, slip uniqueness
- **Jakarta Bean Validation** annotations on all request DTOs
- **Structured error responses** with HTTP status codes, timestamps, and validation field errors

### 📖 API Documentation
- **SpringDoc OpenAPI 3** with Swagger UI
- **Detailed annotations** on every endpoint including request/response schemas, security requirements, and example payloads

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.11 |
| **Security** | Spring Security 6, JWT (jjwt 0.12.6), BCrypt |
| **Database** | MySQL 8+ (InnoDB, utf8mb4) |
| **Data Access** | Spring JdbcTemplate (raw SQL with custom RowMappers & ResultSetExtractors) |
| **Caching** | Redis (Spring Data Redis) — OTP storage with TTL |
| **Payment Gateway** | Chapa (Ethiopian payment processor) |
| **Email Service** | Mailgun (OTP delivery) |
| **API Docs** | SpringDoc OpenAPI 2.8.9 (Swagger UI) |
| **Validation** | Jakarta Validation API, Custom Validators |
| **Build Tool** | Maven (with Maven Wrapper) |
| **Utilities** | Lombok, Unirest HTTP Client |
| **Logging** | Logback (custom XML configuration with daily rotation) |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      Client (Web/Mobile)                 │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTPS
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   API Gateway (Spring Boot)              │
│                                                         │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  Controllers │  │   Security   │  │  Validation   │  │
│  │  (REST API)  │  │ (JWT Filter) │  │  (Custom +    │  │
│  │              │  │              │  │   Jakarta)    │  │
│  └──────┬───────┘  └──────────────┘  └───────────────┘  │
│         │                                               │
│  ┌──────▼───────┐  ┌──────────────┐  ┌───────────────┐  │
│  │   Services   │  │  Payments    │  │  Exceptions   │  │
│  │  (Business   │  │  (Chapa      │  │  (Global      │  │
│  │   Logic)     │  │   Service)   │  │   Handler)    │  │
│  └──────┬───────┘  └──────┬───────┘  └───────────────┘  │
│         │                 │                             │
│  ┌──────▼─────────────────▼───────┐  ┌───────────────┐  │
│  │        Repository Layer        │  │    Mappers    │  │
│  │  (JdbcTemplate + RowMappers +  │  │  (DTO + Row)  │  │
│  │   ResultSetExtractors)         │  │               │  │
│  └──────┬─────────────────┬───────┘  └───────────────┘  │
│         │                 │                             │
└─────────┼─────────────────┼─────────────────────────────┘
          │                 │
    ┌─────▼─────┐    ┌──────▼──────┐
    │   MySQL   │    │    Redis    │
    │ (Primary  │    │ (OTP Cache  │
    │  Database)│    │  with TTL)  │
    └───────────┘    └─────────────┘
```

### Layered Architecture Overview

| Layer | Responsibility |
|---|---|
| **Controllers** | REST endpoints, request/response mapping, Swagger annotations |
| **Security** | JWT filter chain, token generation/validation, authentication provider |
| **Services** | Business logic orchestration, transaction management |
| **Payments** | Chapa API integration, webhook signature verification, deposit processing |
| **Repository** | Raw SQL queries via JdbcTemplate, custom RowMappers and ResultSetExtractors |
| **Mappers** | DTO ↔ Model conversion, ResultSet → Domain object mapping |
| **Validation** | Custom constraint validators, Jakarta Bean Validation |
| **Exceptions** | Domain-specific exceptions, global error response formatting |

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user account | ❌ |
| `POST` | `/api/auth/verify-account` | Verify account with OTP code | ❌ |
| `POST` | `/api/auth/login` | Authenticate and receive JWT tokens | ❌ |
| `POST` | `/api/auth/refresh` | Refresh expired access token | ❌ |
| `GET` | `/api/auth/test` | Test endpoint (public) | ❌ |

### Events

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/event/all` | List upcoming events (paginated) | ❌ |
| `GET` | `/api/event/{eventId}` | Get single event with full market depth | ❌ |

### Bets

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/bet/create` | Place a new bet with slips | ✅ |
| `GET` | `/api/bet/all` | List user's bets (paginated, filterable) | ✅ |
| `GET` | `/api/bet/{betId}` | Get a specific bet with all slips | ✅ |

### Wallet

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/wallet/balance` | Get wallet balance and bonus | ✅ |
| `POST` | `/api/wallet/deposit` | Initiate a deposit via Chapa | ✅ |
| `POST` | `/api/wallet/webhook/chapa` | Chapa payment webhook callback (internal) | ❌ |

### Profile

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/profile` | Get authenticated user's profile | ✅ |

### Health

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/health` | Health check endpoint | ❌ |

> **Query Parameters for Pagination:**
> - `limit` — Number of items per page (default: 10, max: 50)
> - `offset` — Starting position for pagination (default: 0)
> - `page` — Page number starting from 0
> - `size` — Page size (default: 10)
> - `filter` — Time-based filter for bets: `day`, `week`, `month`, `all`

---

## 🗄️ Database Schema

```
┌──────────────┐       ┌─────────────────────┐
│   profile    │       │  profile_settings   │
├──────────────┤       ├─────────────────────┤
│ profile_id   │◄──────│ profile_id (FK)     │
│ phone_number │       │ notifications       │
│ email        │       │ theme               │
│ password_hash│       │ language            │
│ status       │       │ created_at          │
│ is_verified  │       │ updated_at          │
│ is_deleted   │       └─────────────────────┘
│ created_at   │
│ updated_at   │
└──────┬───────┘
       │
       ├──────────────────────────────────────────┐
       │                                          │
┌──────▼───────┐    ┌──────────────┐    ┌────────▼────────┐
│    bets      │    │    wallet    │    │   transactions  │
├──────────────┤    ├──────────────┤    ├─────────────────┤
│ bet_id       │    │ id           │    │ id              │
│ profile_id   │    │ profile_id   │    │ profile_id      │
│ stake        │    │ balance      │    │ reference       │
│ is_bonus     │    │ bonus        │    │ type            │
│ status       │    │ created_by   │    │ amount          │
│ total_odds   │    └──────────────┘    │ status          │
│ possible_win │                        │ created_by      │
│ created_at   │    ┌──────────────┐    └─────────────────┘
│ updated_at   │    │   deposits   │
└──────┬───────┘    ├──────────────┤    ┌─────────────────┐
       │            │ id           │    │  withdrawals    │
┌──────▼───────┐    │ profile_id   │    ├─────────────────┤
│  bet_slips   │    │ trx_ref      │    │ id              │
├──────────────┤    │ amount       │    │ profile_id      │
│ bet_slip_id  │    │ currency     │    │ tx_ref          │
│ bet_id (FK)  │    │ checkout_url │    │ amount          │
│ event_id     │    │ chapa_ref    │    │ channel         │
│ sport_id     │    │ status       │    │ status          │
│ team_id      │    │ created_at   │    │ approved_by     │
│ market_id    │    │ updated_at   │    │ reason          │
│ market_name  │    └──────────────┘    └─────────────────┘
│ participant  │
│ odds         │
│ special_bet  │
│ status       │
│ created_at   │
│ updated_at   │
└──────────────┘
```

### Key Tables

| Table | Purpose |
|---|---|
| `profile` | User accounts with phone number, email, and hashed password |
| `profile_settings` | User preferences — notifications, theme, language |
| `bets` | Bet records with stake, total odds, and possible winnings |
| `bet_slips` | Individual selections within a bet (event, market, odds) |
| `wallet` | User wallet with real balance and bonus balance |
| `deposits` | Payment deposit records linked to Chapa transactions |
| `transactions` | Audit trail for all financial operations |
| `withdrawals` | Withdrawal requests with manual approval workflow |

> **Note:** Event data (events, teams, markets, participants, prices, scores) is sourced from an external sports data provider (Rundown API) and stored in separate tables (`rundown_event`, `teams`, `markets`, `participants`, `prices`, `scores`).

---

## 🔒 Security

### Authentication Flow

```
1. REGISTER
   Client → POST /api/auth/register (phone, password, email)
   Server → Creates profile (unverified), generates OTP, stores in Redis (5hr TTL)
   Server → Sends OTP via Mailgun email

2. VERIFY
   Client → POST /api/auth/verify-account (phone, otp)
   Server → Validates OTP against Redis, marks profile as verified
   Server → Creates wallet, awards welcome freebet

3. LOGIN
   Client → POST /api/auth/login (phone, password)
   Server → Returns accessToken (1hr) + refreshToken (7 days, HttpOnly cookie)

4. AUTHENTICATED REQUEST
   Client → GET /api/bet/all (Authorization: Bearer <accessToken>)
   Server → JwtAuthenticationFilter validates token, extracts claims

5. TOKEN REFRESH
   Client → POST /api/auth/refresh (refreshToken in Authorization header)
   Server → Validates refresh token, issues new accessToken
```

### Security Measures

| Measure | Implementation |
|---|---|
| **Password Storage** | BCrypt hashing with automatic salt generation |
| **Token Signing** | HMAC-SHA256 with configurable secret key |
| **Session Management** | Stateless — no server-side sessions |
| **CSRF Protection** | Disabled (stateless API with Bearer token auth) |
| **Refresh Tokens** | Stored in HttpOnly cookies, not accessible via JavaScript |
| **Webhook Verification** | HMAC-SHA256 signature verification on Chapa callbacks |
| **OTP Storage** | Redis with 5-hour TTL, deleted after successful verification |
| **Input Validation** | Jakarta Bean Validation + custom validators on all endpoints |
| **SQL Injection** | Parameterized queries via JdbcTemplate PreparedStatement |
| **Error Exposure** | Structured error responses — no stack traces in production |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21+** (JDK)
- **MySQL 8.0+**
- **Redis 7.0+**
- **Maven 3.9+** (or use the included Maven Wrapper `./mvnw`)
- **Chapa API credentials** (for payment features)
- **Mailgun API credentials** (for OTP email delivery)
- **ngrok** (for local Chapa webhook testing)

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/bicosteve/api-gateway.git
cd api-gateway
```

**2. Configure environment variables**

Copy the example environment file and fill in your credentials:

```bash
cp .example.env .env
```

Edit `.env` with your configuration:

```properties
# Server
SERVER_PORT=8080

# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=sportsbook
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-256-bit-secret-key-here
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# Chapa Payment Gateway
CHAPA_BASE_URL=https://api.chapa.co/v1
CHAPA_SECRET_KEY=CHASECK_TEST-your_key_here
CHAPA_WEBHOOK_SECRET=your_generated_webhook_secret
CHAPA_CURRENCY=ETB
CHAPA_CALLBACK_URL=https://your-ngrok-url.ngrok-free.app/api/wallet/webhook/chapa
CHAPA_RETURN_URL=http://localhost:3000/deposit/success

# Mailgun Email
MAILGUN_API_KEY=your_mailgun_api_key
MAILGUN_DOMAIN=your_mailgun_sandbox_domain
MAILGUN_FROM_EMAIL=noreply@yourdomain.com
```

**3. Initialize the database**

```bash
# Create the database
mysql -u root -p -e "CREATE DATABASE sportsbook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Schema auto-initializes from schema.sql on first run
# Or manually run:
mysql -u root -p sportsbook < src/main/resources/schema.sql
```

**4. Start Redis**

```bash
# Using Homebrew (macOS)
brew services start redis

# Or using Docker
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

**5. Run the application**

```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or using Make
make run

# Or build and run the JAR
./mvnw clean package -DskipTests
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

The server starts on `http://localhost:8080` by default.

**6. Access Swagger UI**

Navigate to: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**7. Testing Chapa webhooks locally**

```bash
# Expose localhost to the internet for Chapa webhook delivery
ngrok http --domain=yourname.ngrok-free.app 8080

# Update CHAPA_CALLBACK_URL in .env with your ngrok URL
# Inspect incoming webhooks at http://127.0.0.1:4040
```

---

## 📁 Project Structure

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/com/bicosteve/api_gateway/
│   │   │   ├── Main.java                          # Application entry point
│   │   │   ├── config/                            # Configuration classes
│   │   │   │   ├── ChapaConfig.java               # Chapa payment config
│   │   │   │   ├── JacksonConfig.java             # JSON serialization config
│   │   │   │   ├── MailgunConfig.java             # Email service config
│   │   │   │   ├── OpenApiConfig.java             # Swagger/OpenAPI config
│   │   │   │   ├── RedisConfig.java               # Redis connection config
│   │   │   │   ├── RestTemplateConfig.java        # HTTP client with timeouts
│   │   │   │   └── SecurityConfig.java            # Spring Security filter chain
│   │   │   ├── constants/                         # Status code constants
│   │   │   │   ├── DepositStatus.java             # 0=pending, 1=success, 2=failed
│   │   │   │   ├── TransactionStatus.java
│   │   │   │   ├── TransactionType.java
│   │   │   │   └── WithdrawalStatus.java
│   │   │   ├── controllers/                       # REST API controllers
│   │   │   │   ├── AuthControllers.java           # Registration, login, OTP
│   │   │   │   ├── BetControllers.java            # Bet placement & history
│   │   │   │   ├── EventControllers.java          # Sports events
│   │   │   │   ├── HealthCheck.java               # Health endpoint
│   │   │   │   ├── ProfileController.java         # User profile
│   │   │   │   └── WalletControllers.java         # Wallet, deposits, webhook
│   │   │   ├── dto/
│   │   │   │   ├── requests/                      # Inbound request DTOs
│   │   │   │   └── response/                      # Outbound response DTOs
│   │   │   ├── enums/                             # Java enums
│   │   │   ├── exceptions/                        # Custom exceptions
│   │   │   │   └── GlobalExceptionHandler.java   # @RestControllerAdvice
│   │   │   ├── mappers/
│   │   │   │   ├── dtomappers/                    # Model → DTO converters
│   │   │   │   └── rowmappers/                    # ResultSet → Model mappers
│   │   │   ├── models/                            # Domain models (POJOs)
│   │   │   ├── payments/                          # Chapa payment integration
│   │   │   │   └── ChapaService.java             # initializeDeposit, handleWebhook
│   │   │   ├── repository/                        # JdbcTemplate data access
│   │   │   ├── security/                          # JWT + Spring Security
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtService.java
│   │   │   ├── service/                           # Business logic layer
│   │   │   ├── utils/                             # Stateless helper utilities
│   │   │   │   ├── LogContext.java                # MDC traceId / profileId
│   │   │   │   └── TrxRefGenerator.java           # Unique transaction reference
│   │   │   └── validation/                        # Custom constraint validators
│   │   │       ├── UniqueSlip.java                # Custom annotation
│   │   │       └── UniqueSlipValidator.java       # Validator implementation
│   │   └── resources/
│   │       ├── application.yml                    # Main configuration
│   │       ├── application-dev.yml                # Dev profile overrides
│   │       ├── api-gateway-logback.xml            # Logback with daily rotation
│   │       ├── schema.sql                         # Database DDL
│   │       └── data.sql                           # Seed data (optional)
│   └── test/
├── tables/                                        # SQL reference scripts
├── .example.env                                   # Environment variable template
├── Makefile                                       # Common dev commands
├── pom.xml
└── mvnw
```

---

## 🧠 Key Design Decisions

### 1. Raw JDBC over JPA for Query-Heavy Operations

**Decision:** Spring JdbcTemplate with custom `RowMapper` and `ResultSetExtractor` implementations instead of JPA/Hibernate.

**Rationale:** The sportsbook domain involves deeply nested object graphs (Event → Teams → Markets → Participants → Prices) that produce massive Cartesian products when loaded via JPA entity mappings. Custom `ResultSetExtractor` implementations provide full control over result set iteration, enabling efficient de-duplication (e.g., preventing duplicate teams/markets in a single pass). Parameterized `PreparedStatement` usage ensures SQL injection protection while maintaining fine-grained control over query execution.

### 2. Dual-Token JWT Strategy

**Decision:** Short-lived access token (1 hour) in the `Authorization` header + long-lived refresh token (7 days) in an `HttpOnly` cookie.

**Rationale:** Access tokens are short-lived to minimize the window of exposure if compromised. Refresh tokens in `HttpOnly` cookies are inaccessible to JavaScript, preventing XSS-based token theft. The refresh endpoint allows seamless token renewal without re-authentication.

### 3. Redis for OTP Storage

**Decision:** OTP codes stored in Redis with a 5-hour TTL instead of the database.

**Rationale:** OTPs are ephemeral by nature — Redis's native TTL expiration eliminates the need for cleanup jobs. `O(1)` lookup and delete operations enable fast verification. This approach decouples transient verification data from persistent user data.

### 4. HMAC-SHA256 Webhook Verification

**Decision:** Verify Chapa webhook signatures using HMAC-SHA256 before processing payment callbacks.

**Rationale:** Webhook endpoints are publicly accessible — without signature verification, attackers could forge payment confirmations. The shared secret ensures only genuine Chapa callbacks are processed. Idempotency checks (deposit status == SUCCESS) prevent double-crediting even if duplicate webhooks arrive.

### 5. Hosted Checkout over Direct API Integration

**Decision:** Use Chapa's hosted checkout page instead of collecting payment details directly.

**Rationale:** Chapa's hosted checkout offloads PCI DSS compliance entirely. Payment card details never touch the application server, eliminating a major security risk. Users also recognize and trust Chapa's checkout page, reducing drop-off rates.

### 6. Database-Level Constraints

**Decision:** Enforce business rules at the database level with `CHECK` constraints, `UNIQUE` indexes, and `FOREIGN KEY` relationships.

**Rationale:** `CHECK (balance >= 0)` on wallets prevents negative balances regardless of application bugs. `UNIQUE` constraints on phone numbers and transaction references prevent duplicates at the data layer. Cascading foreign keys ensure referential integrity for profile-dependent records.

---

## 🔮 Future Improvements

| Area | Enhancement |
|---|---|
| **Containerization** | Docker Compose setup with MySQL, Redis, and the application for one-command local development |
| **CI/CD Pipeline** | GitHub Actions workflow for automated testing, linting, and deployment |
| **Unit & Integration Tests** | Comprehensive test coverage using JUnit 5, Mockito, and `@SpringBootTest` with Testcontainers |
| **Rate Limiting** | Request throttling on authentication endpoints to prevent brute-force attacks |
| **Admin Dashboard** | Separate admin API for managing events, approving withdrawals, and viewing platform analytics |
| **WebSocket Live Scores** | Real-time score updates pushed to clients via WebSocket connections |
| **Withdrawal Processing** | Full withdrawal workflow with admin approval and Chapa disbursement integration |
| **Cursor-based Pagination** | Migrate from offset-based to cursor-based pagination for better performance at scale |
| **API Versioning** | URL-based versioning (`/api/v1/`) to support backward-compatible changes |
| **Monitoring & Observability** | Spring Actuator + Micrometer metrics with Prometheus/Grafana dashboards |
| **Distributed Caching** | Extend Redis usage for event and market caching to reduce database load |
| **Multi-Currency Support** | Extend wallet and payment processing to support additional currencies beyond ETB |

---

## 👤 Author

**Bico Steve** — Lead Developer & Architect

- **GitHub:** [github.com/bicosteve](https://github.com/bicosteve)
- **Email:** bicosteve4@gmail.com
- **Timezone:** Africa/Nairobi (UTC+3)

---

## 📄 License

This project is intended for portfolio and educational purposes.

---

<p align="center">
  <strong>Built with Java 21 · Spring Boot 3.5 · MySQL · Redis · Chapa Payments</strong>
</p>
