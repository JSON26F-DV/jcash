<div align="center">

<img src="src/main/resources/app/assets/circle_logo.png" alt="JCash logo" width="120" />

# JCash

**A desktop banking application built with JavaFX — accounts, cash-in, instant transfers, and email OTP recovery.**

<p>
  <img alt="Java 17" src="https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white" />
  <img alt="JavaFX 21" src="https://img.shields.io/badge/JavaFX-21.0.6-1f6feb?logo=java&logoColor=white" />
  <img alt="JDBI 3" src="https://img.shields.io/badge/JDBI-3.45.1-blue" />
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL%20%2F%20MariaDB-8.x-4479A1?logo=mysql&logoColor=white" />
  <img alt="Maven" src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white" />
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Desktop-lightgrey" />
</p>

[Overview](#overview) · [Features](#features) · [Screenshots](#screenshots) · [Tech Stack](#tech-stack) · [Getting Started](#getting-started) · [Configuration](#configuration) · [Project Structure](#project-structure) · [Security Notes](#security-notes)

</div>

---

## Overview

**JCash** is a self-contained desktop banking demo written in Java 17 and JavaFX 21. It models the core
flows of a retail banking app — account creation, PIN-based authentication, wallet top-up, peer-to-peer
transfers, transaction history — and implements a complete **email one-time-password (OTP)** pipeline for
account recovery.

The application is intentionally small and readable: thin FXML controllers, a service layer, and a DAO
layer over JDBI, backed by a single MySQL/MariaDB schema. All money movements are executed inside database
transactions with conditional balance updates, so a failed transfer cannot leave a partial debit or credit.

> **Intended use:** learning, demos, and portfolio. It is not production-hardened — see
> [Security Notes](#security-notes) before deploying it anywhere real.

---

## Screenshots

<div align="center">
  <img src="src/main/resources/app/assets/widget.png" alt="JCash landing screen" width="820" />
  <br />
  <sub>Landing screen — language selection and PIN sign-in</sub>
</div>

---

## Features

### Account & Authentication

| Feature | Description |
| --- | --- |
| **Registration** | Create an account with first name, last name, mobile number, email, and a 6-digit PIN. Duplicate mobile numbers and emails are rejected. |
| **PIN sign-in** | Login with a mobile number and 6-digit PIN. Three consecutive failures lock the session and exit the app. |
| **PIN hashing** | PINs are never stored in plaintext — they are hashed before persistence and never logged. |
| **Multi-language UI** | Landing screen switcher for English, Filipino, Bisaya, Spanish, Japanese, Korean, and Chinese. |
| **Session handling** | In-memory session tracks the signed-in user; sign-out returns to the landing screen. |

### Banking Operations

| Feature | Description |
| --- | --- |
| **Dashboard** | Live balance plus the five most recent transactions. |
| **Cash-in** | Top up the wallet with a validated positive amount and a confirmation step. |
| **Express Send (P2P)** | Transfer money to another customer by mobile number — self-transfers blocked, balance checked, optional message attached. |
| **Atomic transfers** | Debit and credit are applied in a single DB transaction with row locking and a `balance + delta >= 0` guard. |
| **Transaction history** | Full paginated history with category, description, and timestamp. |
| **Transaction details** | Per-transaction receipt view: amount, date/time, reference (transaction ID), and message. |
| **Profile & settings** | View name, email, mobile, balance, role, status, session start time, and session token preview. |
| **Policies** | Built-in Terms of Service and Privacy Policy screens. |

### Email OTP Recovery

<details open>
<summary><b>How forgot-PIN verification works</b></summary>

<br />

1. **Request** — the user enters the email address registered to their account.
2. **Generate** — a cryptographically random 6-digit code is produced with `SecureRandom`.
3. **Hash** — only the SHA-256 hash of the code is retained in memory; the plaintext code exists solely in the outgoing email.
4. **Deliver** — the code is sent over authenticated STARTTLS SMTP (Brevo relay) using an HTML template with an inline (CID) logo.
5. **Verify** — the user submits the code, which is hashed and compared against the stored hash.
6. **Reset** — only after successful verification can a new 6-digit PIN be set, with confirmation matching.

**Guard rails**

| Control | Value |
| --- | --- |
| Code length | 6 digits (`SecureRandom`) |
| Validity window | 5 minutes |
| Resend cooldown | 3 minutes, with a live countdown in the UI |
| Failed attempts | 5, then verification is invalidated |
| Transport | SMTP over STARTTLS, port 587, 15-second timeouts |

**Components:** `PasswordResetService`, `BrevoEmailService`, `ForgotPasswordController`,
`VerifyOtpController`, `ChangePasswordController`, and the template at
`src/main/resources/app/email/password-reset.html`.

> OTP state is held in memory and is deliberately ephemeral: restarting the application invalidates any
> pending code.

</details>

---

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 (`maven-compiler-plugin` release 17) |
| UI | JavaFX 21.0.6 (`javafx-controls`, `javafx-fxml`), FXML views, CSS |
| UI components | Ikonli 12.4.0 (FontAwesome 5), BootstrapFX 0.4.0 |
| Persistence | JDBI 3.45.1 (SQL Object style DAO), MySQL Connector/J 8.4.0 |
| Database | MySQL 8.x / MariaDB 10.4 |
| Email | Jakarta Mail via Angus Mail 2.0.3, Brevo SMTP relay |
| Validation | Jakarta Bean Validation 3.0.2, Hibernate Validator 8.0.2 |
| Boilerplate | Lombok 1.18.48 |
| Build & run | Maven Wrapper 3.8.5, `javafx-maven-plugin` 0.0.8 |

---

## Getting Started

### Prerequisites

- **JDK 17 or newer** — check with `java -version`
- **MySQL 8.x or MariaDB 10.4+** running locally
- No separate JavaFX SDK install is required; JavaFX artifacts are resolved by Maven.

### 1. Clone the repository

```bash
git clone https://github.com/JSON26F-DV/jcash.git
cd jcash
```

### 2. Create and seed the database

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jcash_db;"
mysql -u root -p jcash_db < src/main/java/app/database/jcash_db.sql
```

Alternatively, import `src/main/java/app/database/jcash_db.sql` through phpMyAdmin or any SQL client.
The dump creates the `users` and `transactions` tables and includes demo rows.

### 3. Point the app at your database

Database credentials are currently constants in
`src/main/java/app/config/DatabaseConnection.java`:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/jcash_db";
private static final String USER = "root";
private static final String PASS = "";
```

Change `URL`, `USER`, and `PASS` to match your local instance.

### 4. Configure email (required for OTP flows)

Copy the template and fill in your own relay credentials:

```bash
cp .env.example .env
```

```dotenv
BREVO_SENDER_EMAIL=verified-sender@example.com
BREVO_SENDER_NAME=JCash Bank
BREVO_SMTP_HOST=smtp-relay.brevo.com
BREVO_SMTP_PORT=587
BREVO_SMTP_LOGIN=your-smtp-login
BREVO_SMTP_PASSWORD=your-smtp-key
BREVO_LOGO_PATH=src/main/resources/app/assets/circle_logo.png
```

`.env` is git-ignored. Variables are read by `app/config/EnvConfig.java` — real environment variables take
precedence over the file. Every other feature works without email configured; only forgot-PIN is affected.

### 5. Run

```bash
./mvnw clean javafx:run          # Linux / macOS
mvnw.cmd clean javafx:run        # Windows
```

Main class: `app.Main`.

### Demo accounts

The seed dump ships with these accounts (mobile number + 6-digit PIN):

| Mobile number | PIN | Account holder |
| --- | --- | --- |
| `09876543212` | `654321` | Jason Begornia |
| `09945646355` | `123456` | Demo customer |
| `09123456789` | `123456` | Demo customer |
| `09876543217` | `123456` | Demo customer |

> Demo data only. Delete or replace these rows before using the app with real information.

---

## Project Structure

```
src/main/java/app
├── Main.java                  # JavaFX entry point
├── config/                    # Database + .env configuration
├── controller/
│   ├── auth/                  # Landing, register, forgot PIN, OTP, change PIN
│   ├── customer/              # Dashboard, history, details, settings
│   ├── transaction/           # Cash-in, express send, confirmation
│   ├── common/                # Header, sidebar, policy pages, routing
│   └── components/
├── dao/                       # DAO interfaces + JDBI implementations
├── dto/                       # Transfer objects and view records
├── model/                     # User, Transaction
├── services/                  # AuthService, TransactionService, PasswordResetService, BrevoEmailService
├── session/                   # UserSession
├── util/                      # PinUtil (hashing)
└── database/jcash_db.sql      # Schema + demo data

src/main/resources/app
├── view/                      # FXML layouts
├── css/                       # Stylesheets
├── assets/                    # Logo and imagery
└── email/password-reset.html  # OTP email template
```

**Design:** controllers stay thin, business rules live in the service layer, and all SQL is isolated behind
DAO interfaces. Money is handled exclusively with `BigDecimal`, and balance mutations run as a single
database transaction.

---

## Configuration

| Variable | Required | Purpose |
| --- | --- | --- |
| `BREVO_SMTP_HOST` | Yes (OTP) | SMTP relay hostname |
| `BREVO_SMTP_PORT` | Yes (OTP) | Relay port, typically `587` |
| `BREVO_SMTP_LOGIN` | Yes (OTP) | SMTP username |
| `BREVO_SMTP_PASSWORD` | Yes (OTP) | SMTP password / key |
| `BREVO_SENDER_EMAIL` | Yes (OTP) | Verified sender address |
| `BREVO_SENDER_NAME` | No | Display name on outgoing mail |
| `BREVO_LOGO_PATH` | No | Logo embedded in the OTP email (defaults to the bundled asset) |

Database settings are compile-time constants in `app/config/DatabaseConnection.java`.

---

## Security Notes

This is a demonstration project. Before using it with real data:

- **PIN hashing is unsalted SHA-256.** Use BCrypt, scrypt, or Argon2 with per-user salts.
- **Database credentials are hardcoded** to `root` with an empty password. Move them to environment
  variables or a secrets manager, and use a dedicated least-privilege database user.
- **OTP state and login lockout counters are in memory** and shared across the process. Persist them and
  scope them per account for any multi-user deployment.
- **No transport security between app and database** unless your MySQL instance is configured for TLS.
- **Never commit `.env`.** Credentials belong in the environment, not in the repository.

---

## Roadmap

- [ ] Salted, adaptive PIN hashing (BCrypt/Argon2)
- [ ] Externalized database configuration
- [ ] Persisted OTP and per-account throttling
- [ ] Automated tests (JUnit 5 is already on the classpath)
- [ ] CI pipeline for build verification
- [ ] Functional copy-reference and help actions on the details screen

---

<div align="center">
<sub>Built with JavaFX · JDBI · MariaDB</sub>
</div>
