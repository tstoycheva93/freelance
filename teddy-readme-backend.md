# TalentFlow - Backend (`freelance`)

Spring Boot backend for the TalentFlow freelance marketplace platform. Manages users, job postings, offers, real-time messaging, notifications, reviews, and administrative functions.

## Tech Stack

| Technology | Version |
|-----------|---------|
| Spring Boot | 4.0.2 |
| Java | 21 |
| Maven | (wrapper included) |
| MySQL | (via Connector/J) |
| JPA / Hibernate | (Spring Data JPA) |
| Spring Security | JWT + 2FA |
| WebSocket | STOMP over SockJS |
| JWT | jjwt 0.13.0 (HS512) |
| Thymeleaf | email templates |
| Handlebars | contract/receipt/resume templates |
| OpenHTMLtoPDF | PDF generation |
| Lombok | boilerplate reduction |
| H2 | (test scope) |
| Spring Mail | Gmail SMTP |
| Spring Actuator | health/monitoring |

## Getting Started

### Prerequisites

- JDK 21
- MySQL 8+
- Maven (or use the included `mvnw` wrapper)

### Database Setup

```sql
CREATE DATABASE freelance;
```

### Configuration

Create `src/main/resources/application-local.properties` (git-ignored):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/freelance?allowPublicKeyRetrieval=true&sslMode=DISABLED
spring.datasource.username=root
spring.datasource.password=your_password

jwt.secret.key=<base64-encoded-256-bit-secret>
jwt.expiration=1800000

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

app.base-url=http://localhost:8080
app.base-url-front=http://localhost:5173
```

### Run

```bash
# Development (uses application-local.properties as override)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Or build and run
./mvnw clean package -DskipTests
java -jar target/freelance-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

The API serves on `http://localhost:8080/api`.

## Project Structure

```
freelance/
├── src/
│   ├── main/
│   │   ├── java/com/thesis/freelance/
│   │   │   ├── FreelanceApplication.java    # Entry point (@SpringBootApplication)
│   │   │   │
│   │   │   ├── config/                      # Spring configuration
│   │   │   │   ├── SecurityConfig.java      # Security filter chain, CORS, roles
│   │   │   │   ├── WebSocketConfig.java     # STOMP endpoint + broker config
│   │   │   │   └── AsyncConfig.java         # Thread pool for async tasks
│   │   │   │
│   │   │   ├── security/                    # Authentication & authorization
│   │   │   │   ├── jwt/                     # JwtTokenProvider, JwtAuthFilter
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── MaintenanceModeFilter.java
│   │   │   │
│   │   │   ├── auth/                        # Authentication module
│   │   │   │   ├── entity/                  # User entity
│   │   │   │   ├── dto/                     # Login/Register/AuthResponse DTOs
│   │   │   │   ├── repository/              # UserRepository
│   │   │   │   ├── service/                 # AuthService (login, register, 2FA, reset)
│   │   │   │   ├── web/                     # AuthController, etc.
│   │   │   │   └── converter/               # Entity ↔ DTO converters
│   │   │   │
│   │   │   ├── common/                      # Shared infrastructure
│   │   │   │   ├── entity/                  # BaseEntity (auditing fields)
│   │   │   │   ├── enums/                   # Role, OfferStatus, FlagType, etc.
│   │   │   │   ├── exception/               # GlobalExceptionHandler, custom exceptions
│   │   │   │   └── util/                    # Utility classes
│   │   │   │
│   │   │   └── features/                    # Domain modules (22 modules)
│   │   │       ├── catalog/                 # Admin catalog management
│   │   │       ├── category/                # Category entity & CRUD
│   │   │       ├── certification/           # Freelancer certifications + expiry scheduler
│   │   │       ├── client/                  # Client profile entity
│   │   │       ├── communication/           # Batch email & push notifications
│   │   │       ├── conversation/            # Chat conversation entity
│   │   │       ├── education/               # Freelancer education history
│   │   │       ├── flag/                    # Content reporting/moderation
│   │   │       ├── freelancer/              # Freelancer profile + matching
│   │   │       ├── location/                # Location/Country entities
│   │   │       ├── message/                 # Chat messages
│   │   │       ├── notification/            # In-app notifications
│   │   │       ├── offer/                   # Offer lifecycle management
│   │   │       ├── posting/                 # Job posting CRUD
│   │   │       ├── profile/                 # Profile aggregation service
│   │   │       ├── review/                  # Review/rating system
│   │   │       ├── skill/                   # Skill entity
│   │   │       ├── statistics/              # Dashboard + platform analytics (native queries)
│   │   │       ├── system/                  # System config (maintenance, registration)
│   │   │       ├── uploadedCV/              # CV uploads
│   │   │       ├── user/                    # User entity extensions
│   │   │       └── work/                    # Freelancer work experience
│   │   │
│   │   └── resources/
│   │       ├── application.properties       # Common config (MySQL, JPA, logging)
│   │       ├── application-local.properties # Local overrides (git-ignored)
│   │       ├── data/                        # SQL seed files (11 files)
│   │       │   ├── categories.sql           # 15 categories + countries
│   │       │   ├── skills.sql               # ~100 skills
│   │       │   ├── postings.sql             # Sample job postings
│   │       │   ├── offers.sql               # Sample offers
│   │       │   ├── reviews.sql              # Sample reviews
│   │       │   └── ...                      # Freelancer skills, education, work, certs
│   │       └── templates/
│   │           ├── email/                   # 7 Thymeleaf email templates
│   │           ├── legal/                   # Contract + receipt (Handlebars → PDF)
│   │           └── resume/                  # Resume PDF template (Handlebars)
│   │
│   └── test/
│       ├── java/                            # Unit & integration tests
│       └── resources/                       # Test config (H2 DB)
│
├── tools/
│   └── postman/                             # Postman collections (API testing)
│       ├── register-tests.collection.json
│       └── email-verification-requests.json
│
├── pom.xml                                  # Maven build
├── mvnw / mvnw.cmd                          # Maven wrapper
└── HELP.md
```

## REST API Overview

All endpoints are prefixed with `/api` (context path set in `application-local.properties`).

### Authentication (`/auth/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Login with username + password; returns JWT |
| POST | `/auth/verify-2fa` | Verify 2FA code for new device |
| POST | `/auth/register` | Register new user (Freelancer or Client) |
| GET | `/auth/me` | Get current user from JWT |
| POST | `/auth/verify` | Verify email with token |
| POST | `/auth/reset-password` | Request password reset email |
| GET | `/auth/reset-password/validate` | Validate reset token |
| POST | `/auth/reset-password/confirm` | Set new password |
| GET | `/auth/register-admin/validate` | Validate admin invite token |
| POST | `/auth/register-admin` | Complete admin registration from invite |

### Postings (`/postings/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/postings` | List postings (paginated, filtered, searchable) |
| GET | `/postings/{id}` | Get posting detail |
| POST | `/postings` | Create posting (Client only) |
| PUT | `/postings/{id}` | Update posting |
| DELETE | `/postings/{id}` | Delete posting |
| POST | `/postings/{id}/save` | Save posting to favorites |
| DELETE | `/postings/{id}/save` | Unsave posting |

### Offers (`/offers/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/offers` | List current user's offers |
| GET | `/offers/{id}` | Get offer detail |
| POST | `/offers` | Create offer on a posting (Freelancer only) |
| PUT | `/offers/{id}/accept` | Accept offer (Client) |
| PUT | `/offers/{id}/reject` | Reject offer |
| PUT | `/offers/{id}/deliver` | Mark as delivered (Freelancer) |
| PUT | `/offers/{id}/complete` | Mark as completed (Client) |
| PUT | `/offers/{id}/cancel` | Cancel offer |
| PUT | `/offers/{id}/unsatisfactory` | Mark as unsatisfactory |

### Conversations & Messages (`/conversations/**`, `/messages/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/conversations` | List user's conversations |
| POST | `/conversations` | Create new conversation |
| PUT | `/conversations/{id}/read` | Mark messages as read |
| GET | `/messages/{conversationId}` | Get messages in conversation |

### Profile (`/profile/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/profile/user/{username}` | Get profile by username |
| GET | `/profile/{userId}` | Get profile by user ID |
| GET | `/profile/{userId}/reviews` | Paginated reviews for user |
| GET | `/profile/{userId}/postings` | Paginated postings by user |
| GET | `/profile/{userId}/saved-postings` | Paginated saved postings |
| GET | `/profile/people` | People directory (search, filter, sort) |

### Reviews (`/reviews/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reviews/target/{targetId}` | Reviews for a target user |
| POST | `/reviews` | Create a review |

### Notifications (`/notifications/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/notifications` | List current user's notifications |
| PUT | `/notifications/{id}/read` | Mark notification as read |
| PUT | `/notifications/read-all` | Mark all as read |

### Flags (`/flags/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/flags` | Flag/report content |
| GET | `/flags/pending` | List pending flags (Admin) |
| GET | `/flags/{id}` | Get flag detail (Admin) |
| PATCH | `/flags/{id}/review` | Review/action flag (Admin) |

### Categories & Skills (`/categories/**`, `/skills/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories` | List all categories |
| GET | `/categories/skills` | Categories with nested skills |

### Locations (`/locations/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/locations` | List all locations |
| GET | `/locations/search` | Search locations by query |

### Admin Office (`/office/**`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/office/dashboard` | KPI data + growth chart + attention panel |
| GET | `/office/statistics` | Platform analytics (funnel, growth, geo) |
| GET | `/office/users` | Paginated user list with filters |
| GET | `/office/users/{id}` | User detail + activity |
| PUT | `/office/users/{id}/suspend` | Suspend user account |
| PUT | `/office/users/{id}/reactivate` | Reactivate user |
| DELETE | `/office/users/{id}` | Delete user |
| POST | `/office/users/{id}/force-password-reset` | Force password reset |
| POST | `/office/users/{id}/force-verify` | Force email verification |
| DELETE | `/office/users/{id}/photo` | Delete user profile photo |
| GET | `/office/postings` | Paginated posting list |
| GET | `/office/postings/{id}` | Posting detail with offers |
| PUT | `/office/postings/{id}/deactivate` | Deactivate posting |
| PUT | `/office/postings/{id}/close` | Close posting |
| PUT | `/office/postings/{id}/reopen` | Reopen posting |
| GET | `/office/flags` | Paginated flag queue |
| GET | `/office/flags/{id}` | Flag detail |
| PATCH | `/office/flags/{id}/review` | Approve/dismiss flag |
| GET | `/office/catalog/categories` | Categories with in-use status |
| POST | `/office/catalog/categories` | Create category |
| DELETE | `/office/catalog/categories/{id}` | Delete category |
| GET | `/office/catalog/skills` | Skills with in-use status |
| POST | `/office/catalog/skills` | Create skill |
| DELETE | `/office/catalog/skills/{id}` | Delete skill |
| GET | `/office/system` | System configuration |
| PUT | `/office/system/registration` | Toggle registration |
| PUT | `/office/system/maintenance` | Toggle maintenance mode |
| GET | `/office/system/stats` | Server health metrics |
| POST | `/office/communication/batch-email` | Send batch email |
| POST | `/office/communication/batch-notification` | Send batch push notification |
| GET | `/office/communication/smtp-status` | SMTP connection status |
| POST | `/office/communication/test-email` | Send test email |
| POST | `/office/admins/invite` | Invite new admin |
| GET | `/office/admins/invites` | List pending invites |
| GET | `/office/admins` | List active admins |

### Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/system/public/config` | Public system config (maintenance, registration) |

## Data Model

### Core Entities

```
User (id, username, email, password, firstName, lastName, phoneNumber,
      dateOfBirth, isVerified, is2faEnabled, profilePhoto, role, deviceId)
  │
  ├── Client (id, company, website, description, userId [1:1])
  │
  └── Freelancer (id, title, bio, experienceLevel, website, userId [1:1])
        │
        ├── FreelancerSkill (freelancer, skill, proficiencyLevel)
        ├── Certification (freelancer, title, org, date, expiration)
        ├── Education (freelancer, institution, degree, field, dates)
        └── Work (freelancer, title, company, type, dates, description)

Posting (id, title, description, budget, deadline, experienceLevel,
         isActive, isClosed, isDeleted, views, client [M:1], category,
         location)
  │
  ├── PostingSkill (posting, skill, importance [REQUIRED/PREFERRED])
  ├── PostingAttachment (posting, url, filename)
  │
  └── Offer (id, price, proposal, deadline, status [PENDING/SHORTLISTED/
             ACCEPTED/REJECTED/DELIVERED/COMPLETED/CANCELED/UNSATISFACTORY],
             posting [M:1], freelancer [M:1])
                │
                └── Review (id, rating, comment, reviewer, target,
                            isClientToFreelancer, offer [1:1])

Conversation (id, posting, participants)
  └── Message (id, content, sender, sentAt, isRead, conversation [M:1])

Notification (id, type, title, content, pageUrl, isRead, user [M:1])

Flag (id, type [SPAM/INAPPROPRIATE/ABUSE/FRAUD/ILLEGAL/OTHER],
       status [PENDING/APPROVED/DECLINED],
       target [PROFILE/POSTING/REVIEW], targetId, reason, description,
       reporter [M:1], reviewer [M:1])

Category (id, name, description, displayOrder, isActive)
  └── Skill (id, name, type, popularity, aliases, category [M:1])

Location (id, name, lat, lng, country [M:1])
  └── Country (id, name, initials)

SystemConfig (id fixed, registrationEnabled, registrationMessage,
              maintenanceMode, maintenanceMessage, updatedBy)
```

### Enums

| Enum | Values |
|------|--------|
| `Role` | FREELANCER, CLIENT, ADMIN |
| `OfferStatus` | PENDING, SHORTLISTED, ACCEPTED, REJECTED, DELIVERED, COMPLETED, CANCELED, UNSATISFACTORY |
| `ExperienceLevel` | BEGINNER, INTERMEDIATE, ADVANCED, EXPERT |
| `SkillType` | TECHNICAL, TOOL, SOFT_SKILL, LANGUAGE |
| `FlagType` | SPAM, INAPPROPRIATE, ABUSE, FRAUD, ILLEGAL_ACTIVITY, OTHER |
| `FlagTarget` | PROFILE, POSTING, REVIEW |
| `FlagStatus` | PENDING, APPROVED, DECLINED |
| `NotificationType` | new_offer, offer_accepted, offer_rejected, offer_delivered, offer_canceled, offer_completed, offer_unsatisfactory, expired_certificate, expired_posting, expired_offer, new_review, custom, registration_closed, new_flag, new_user, new_posting |
| `Audience` | ALL_USERS, CLIENTS, FREELANCERS, NEW_USERS |
| `Importance` | REQUIRED, PREFERRED |

## Security Architecture

### JWT Authentication

- Algorithm: HS512 with 256-bit secret
- Token includes: userId, username, role, issuedAt, expiration
- Default expiration: 30 minutes (configurable via `jwt.expiration`)
- Filter: `JwtAuthFilter` (extends `OncePerRequestFilter`) validates every request
- Excluded paths: `/auth/login`, `/auth/register`, `/auth/verify`, `/auth/reset-password/**`, `/auth/register-admin/**`, `/system/public/config`, `/ws/**`, Actuator

### 2FA (Two-Factor Authentication)

- Triggered when login is attempted from an unrecognized device ID
- Sends 6-digit code via email using `two-factor-auth.html` template
- User confirms via `POST /auth/verify-2fa` with code + device info
- On success, device is remembered for future logins

### Role-Based Access Control

- `@PreAuthorize("hasRole('ADMIN')")` on all `/office/**` endpoints
- Client-only operations (posting creation) checked in service layer

### Maintenance Mode

- `MaintenanceModeFilter` checks `SystemConfig` singleton
- When active, all non-admin requests receive 503 with JSON maintenance payload
- Frontend's API client detects 503 and dispatches `maintenance:activated` event

### CORS

- Allowed origins: `http://localhost:5173` (dev), configurable in `SecurityConfig`

## Real-Time (WebSocket)

- STOMP protocol over SockJS at `/ws` endpoint
- Broker: `/topic` (simple message broker)
- Application destination prefix: `/ws`
- JWT authentication on connect via `connectHeaders`
- Per-user queues:
  - `/notifications/user/{userId}` — real-time notification delivery
  - `/chat/user/{userId}` — incoming chat messages
- Publishing endpoint: `/ws/chat.sendMessage`

## Scheduled Tasks

All run daily at 12:00 PM via `@Scheduled(cron = "0 0 12 * * *")`:

| Task | Description |
|------|-------------|
| `ExpiredOfferScheduler` | Marks offers past deadline as EXPIRED |
| `ExpiredPostingScheduler` | Marks postings past deadline as CLOSED |
| `CertificationScheduler` | Detects expiring certifications, sends notifications |

Additionally triggered via manual endpoints for testing.

## Email Templates

Located in `src/main/resources/templates/email/`, rendered with Thymeleaf:

| Template | Trigger | Variables |
|----------|---------|-----------|
| `verification.html` | New registration | `verificationLink` |
| `forgot-password.html` | Password reset request | `resetLink` |
| `two-factor-auth.html` | 2FA challenge | `deviceName`, `code` |
| `admin-invite.html` | Admin invitation | `inviteLink` |
| `congratulations.html` | Email verified | `appUrl` |
| `notification.html` | Generic notification | `title`, `content`, `ctaLink`, `ctaText` |
| `batch.html` | Batch email campaign | `subject`, `body`, `appUrl`, `firstName` |

## PDF Generation

Uses Handlebars templates + OpenHTMLtoPDF:

| Template | Purpose | Variables |
|----------|---------|-----------|
| `legal/contract.html` | Service contract upon offer acceptance | offerId, clientName, freelancerName, postingTitle, price, deliveryDate, signatures |
| `legal/receipt.html` | Payment receipt on completion | offerId, settledAmount, client/freelancer info |
| `resume/resume.html` | Freelancer resume export | full profile data, skills, certs, work, education |

## Seed Data

SQL files in `src/main/resources/data/`:

| File | Content |
|------|---------|
| `categories.sql` | 15 categories + 10 Balkan countries |
| `skills.sql` | ~100 skills with types and aliases |
| `postings.sql` | 3 sample job postings |
| `offers.sql` | 5 sample offers |
| `reviews.sql` | 3 sample reviews |
| `posting_attachments.sql` | 7 attachment URLs |
| `posting_skills.sql` | 8 posting-skill mappings |
| `freelancer_skills.sql` | 11 freelancer-skill mappings |
| `freelancer_certification.sql` | 10 certifications |
| `freelancer_work.sql` | 10 work entries |
| `freelancer_education.sql` | 7 education entries |

## Testing

```bash
./mvnw test
```

- JUnit 5 with Spring Boot Test
- H2 in-memory database for test isolation
- Spring Security Test for auth context
- Test config in `src/test/resources/`

## Error Handling

`GlobalExceptionHandler` (@ControllerAdvice) handles:

| Exception | HTTP Status | Response Body |
|-----------|-------------|---------------|
| `MethodArgumentNotValidException` | 422 | Field-level error map |
| `AccessDeniedException` | 403 | Standard error |
| `AuthenticationException` | 401 | Standard error |
| `EntityNotFoundException` | 404 | Standard error |
| `IllegalStateException` / `IllegalArgumentException` | 400 | Standard error |
| `DataIntegrityViolationException` | 409 | Conflict message |
| Generic `Exception` | 500 | Server error |

Standard error response format:
```json
{
  "timestamp": "2026-05-30T12:00:00",
  "status": 422,
  "error": "Validation Failed",
  "code": "VALIDATION_ERROR",
  "message": "Validation failed for fields: email",
  "suggestion": "Check the highlighted fields",
  "path": "/api/auth/register",
  "fieldErrors": {
    "email": "Email is already in use"
  }
}
```

## API Documentation (Postman)

Postman collections are available in `tools/postman/`:
- `register-tests.collection.json` — Registration flow tests
- `email-verification-requests.json` — Email verification endpoint tests

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | — | MySQL JDBC URL |
| `spring.datasource.username` | — | DB user |
| `spring.datasource.password` | — | DB password |
| `jwt.secret.key` | — | HS512 signing key (256-bit+) |
| `jwt.expiration` | 1800000 | Token TTL in ms (30 min) |
| `spring.mail.*` | — | SMTP configuration |
| `app.base-url` | http://localhost:8080 | Backend public URL |
| `app.base-url-front` | http://localhost:5173 | Frontend URL (for email links) |
| `server.servlet.context-path` | /api | API prefix |
