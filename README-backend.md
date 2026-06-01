# TalentFlow Backend

A modern Spring Boot-based freelance marketplace platform backend with JWT authentication, real-time WebSocket communication, and comprehensive user management.

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [WebSocket Events](#websocket-events)
- [Database](#database)
- [Email Configuration](#email-configuration)
- [Development Guidelines](#development-guidelines)
- [Building & Deployment](#building--deployment)

## 🎯 Overview

TalentFlow Backend is a RESTful API service that powers the freelance marketplace platform. It handles:
- User authentication and authorization (JWT-based)
- User profile management (Clients, Freelancers, Admin)
- Job posting creation and management
- Offer/proposal system for freelancers
- Real-time messaging and notifications via WebSocket
- Admin dashboard with analytics and user management
- Report system for platform moderation
- Email notifications and batch processing
- PDF document generation for contracts/proposals

## 🛠 Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 4.0.2 |
| Java | Java | 21 |
| Database | MySQL | Latest |
| ORM | JPA/Hibernate | Latest |
| Security | Spring Security + JWT | JJWT 0.13.0 |
| Real-time | WebSocket | Native Spring |
| Email | Spring Mail | Built-in |
| Async Processing | Spring Async | Built-in |
| PDF Generation | OpenHtmlToPDF | 1.0.10 |
| Template Engine | Handlebars | 4.5.0 |
| Validation | Spring Validation | Built-in |
| Data Access | Spring Data JPA | Latest |
| Auditing | Spring Data Auditing | Built-in |
| Testing | JUnit 5, MockMvc | Built-in |
| Build Tool | Maven | 3.x |

## 🏗 Architecture

### Layered Architecture
```
┌─────────────────────────────────────┐
│  Controllers (REST Endpoints)       │
├─────────────────────────────────────┤
│  Services (Business Logic)          │
├─────────────────────────────────────┤
│  Repositories (Data Access)         │
├─────────────────────────────────────┤
│  Entities (Domain Models)           │
├─────────────────────────────────────┤
│  Database (MySQL)                   │
└─────────────────────────────────────┘
```

### Key Architectural Components

**1. Security Layer**
- JWT Token Provider for token generation/validation
- JWT Authentication Filter for request filtering
- Spring Security configuration for role-based access control
- Maintenance Mode Filter for platform maintenance

**2. Configuration Layer**
- JPA Auditing configuration for timestamp tracking
- Async/Scheduled tasks configuration
- WebSocket configuration for real-time communication
- CORS configuration for frontend integration
- Email service configuration

**3. Service Layer**
- User service for profile management
- Authentication service for login/registration
- Posting service for job management
- Offer service for freelancer proposals
- Notification service for alerts
- Email service for batch notifications
- Admin service for platform management

**4. Entity/Model Layer**
- Base entity with common fields (id, createdAt, updatedAt, activityStatus)
- User (core user data)
- Client (client-specific details)
- Freelancer (freelancer-specific details)
- Posting (job postings)
- Offer (freelancer proposals)
- Skill (skill catalog)
- Review/Rating (user feedback)
- And more...

## 📋 Prerequisites

- **Java 21** - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** - Download from [Apache Maven](https://maven.apache.org/)
- **MySQL 8.0+** - Download from [MySQL Community](https://dev.mysql.com/downloads/)
- **Git** - For version control

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
cd path/to/talentflow
git clone <repository-url>
cd freelance
```

### 2. Install Dependencies

```bash
# Install all Maven dependencies
mvn clean install

# Skip tests if needed (for faster setup)
mvn clean install -DskipTests
```

### 3. Database Setup

#### Create MySQL Database

```sql
-- Create database
CREATE DATABASE talentflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional but recommended)
CREATE USER 'talentflow_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON talentflow_db.* TO 'talentflow_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Configure Database Connection

Create or update `application-local.properties`:

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/talentflow_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=talentflow_user
spring.datasource.password=your_secure_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## ⚙️ Configuration

### Application Properties

**Base Configuration** (`application.properties`):

```properties
# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# Development Tools
spring.devtools.restart.poll-interval=2s
spring.devtools.restart.quiet-period=1s

# Logging
logging.level.org.hibernate.SQL=DEBUG
```

### JWT Configuration

Update security configuration for JWT:

```properties
jwt.secret=your-super-secret-key-min-256-chars-long-for-security
jwt.expiration=86400000  # 24 hours in milliseconds
jwt.refresh-expiration=604800000  # 7 days in milliseconds
```

### Email Configuration

Configure email service in `application.properties`:

```properties
# Email Configuration
spring.mail.host=your-email-provider-host
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Email sender details
app.mail.from=noreply@talentflow.com
app.mail.from-name=TalentFlow Platform
```

### WebSocket Configuration

WebSocket is automatically configured for real-time messaging. Connection URL:

```
ws://localhost:8080/ws
```

### CORS Configuration

The backend includes CORS configuration for frontend access:

```properties
cors.allowed-origins=http://localhost:3000,http://localhost:5173
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
cors.allowed-headers=*
cors.allow-credentials=true
```

## 🏃 Running the Application

### Development Mode (With Hot Reload)

```bash
# Using Maven Spring Boot plugin
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Application will start on http://localhost:8080
```

### Production Build

```bash
# Build JAR file
mvn clean package

# Run JAR
java -jar target/freelance-0.0.1-SNAPSHOT.jar

# Or with environment variables
java -Dspring.profiles.active=prod \
     -Dspring.datasource.url=jdbc:mysql://prod-host:3306/talentflow_db \
     -jar target/freelance-0.0.1-SNAPSHOT.jar
```

### Check Application Status

```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Available actuator endpoints
curl http://localhost:8080/actuator
```

## 📁 Project Structure

```
freelance/
├── src/
│   ├── main/
│   │   ├── java/com/thesis/freelance/
│   │   │   ├── FreelanceApplication.java       # Main application entry
│   │   │   │
│   │   │   ├── auth/                           # Authentication logic
│   │   │   │   ├── controller/                 # Auth endpoints
│   │   │   │   ├── service/                    # Auth business logic
│   │   │   │   ├── dto/                        # Request/Response DTOs
│   │   │   │   └── repository/                 # Auth data access
│   │   │   │
│   │   │   ├── config/                         # Spring configurations
│   │   │   │   ├── SecurityConfig.java         # Spring Security setup
│   │   │   │   ├── WebSocketConfig.java        # WebSocket configuration
│   │   │   │   ├── JpaConfig.java              # JPA auditing
│   │   │   │   ├── AsyncConfig.java            # Async task config
│   │   │   │   └── CorsConfig.java             # CORS settings
│   │   │   │
│   │   │   ├── security/                       # Security components
│   │   │   │   ├── jwt/                        # JWT utilities
│   │   │   │   │   ├── JwtTokenProvider.java   # Token generation/validation
│   │   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   ├── MaintenanceModeFilter.java  # Maintenance mode
│   │   │   │   └── CustomUserDetails.java      # User details implementation
│   │   │   │
│   │   │   ├── features/                       # Business features
│   │   │   │   ├── user/                       # User management
│   │   │   │   │   ├── entity/User.java
│   │   │   │   │   ├── service/UserService.java
│   │   │   │   │   ├── controller/UserController.java
│   │   │   │   │   ├── dto/                    # User DTOs
│   │   │   │   │   └── repository/UserRepository.java
│   │   │   │   │
│   │   │   │   ├── posting/                    # Job postings
│   │   │   │   │   ├── entity/Posting.java
│   │   │   │   │   ├── service/PostingService.java
│   │   │   │   │   ├── controller/PostingController.java
│   │   │   │   │   └── repository/PostingRepository.java
│   │   │   │   │
│   │   │   │   ├── offer/                      # Freelancer offers
│   │   │   │   │   ├── entity/Offer.java
│   │   │   │   │   ├── service/OfferService.java
│   │   │   │   │   └── controller/OfferController.java
│   │   │   │   │
│   │   │   │   ├── notification/               # Notifications
│   │   │   │   │   ├── service/NotificationService.java
│   │   │   │   │   ├── controller/NotificationController.java
│   │   │   │   │   └── websocket/               # WebSocket handlers
│   │   │   │   │
│   │   │   │   ├── messaging/                  # Chat/Messages
│   │   │   │   │   ├── entity/Message.java
│   │   │   │   │   ├── service/MessageService.java
│   │   │   │   │   └── websocket/MessageHandler.java
│   │   │   │   │
│   │   │   │   ├── admin/                      # Admin features
│   │   │   │   │   ├── service/AdminService.java
│   │   │   │   │   ├── controller/AdminController.java
│   │   │   │   │   └── dto/                    # Admin DTOs
│   │   │   │   │
│   │   │   │   ├── skill/                      # Skill management
│   │   │   │   ├── category/                   # Category management
│   │   │   │   ├── report/                     # Report system
│   │   │   │   └── email/                      # Email notifications
│   │   │   │
│   │   │   ├── common/                         # Shared utilities
│   │   │   │   ├── entity/BaseEntity.java      # Base entity class
│   │   │   │   ├── exception/                  # Custom exceptions
│   │   │   │   ├── dto/                        # Common DTOs
│   │   │   │   ├── util/                       # Utility classes
│   │   │   │   └── constants/                  # Application constants
│   │   │   │
│   │   │   └── tools/                          # Utility tools
│   │   │
│   │   └── resources/
│   │       ├── application.properties          # Default configuration
│   │       ├── application-local.properties    # Local development config
│   │       ├── application-prod.properties     # Production config
│   │       ├── templates/                      # Email templates
│   │       └── data/                           # SQL data files
│   │
│   └── test/
│       └── java/                               # Unit tests
│
├── pom.xml                                      # Maven dependencies
├── mvnw                                         # Maven wrapper (Unix)
├── mvnw.cmd                                     # Maven wrapper (Windows)
└── README.md                                    # This file
```

## 🔌 API Endpoints

### Authentication Endpoints

```
POST   /auth/register                 - Register new user
POST   /auth/login                    - Login and get JWT token
POST   /auth/verify-email             - Verify email with token
POST   /auth/resend-verification      - Resend verification email
POST   /auth/password-reset           - Request password reset
POST   /auth/password-reset/confirm   - Confirm password reset
POST   /auth/verify-2fa               - Verify 2FA code
GET    /auth/me                       - Get current authenticated user
POST   /auth/logout                   - Logout (invalidate token)
```

### User Management

```
GET    /user/profile                  - Get user profile
PUT    /user/profile                  - Update user profile
POST   /user/change-password          - Change password
GET    /user/notification-settings    - Get notification preferences
PUT    /user/notification-settings    - Update notification preferences
GET    /user/{userId}                 - Get user details (public)
GET    /user/{userId}/profile         - Get user profile page data
```

### Job Postings

```
GET    /posting                       - List all postings (paginated, filterable)
GET    /posting/{postingId}           - Get posting details
POST   /posting                       - Create new posting (Client only)
PUT    /posting/{postingId}           - Update posting (Owner only)
DELETE /posting/{postingId}           - Delete posting (Owner only)
GET    /posting/{postingId}/offers    - Get offers for posting
PATCH  /posting/{postingId}/status    - Change posting status
```

### Freelancer Offers

```
GET    /offer                         - List user's offers
GET    /offer/{offerId}               - Get offer details
POST   /offer                         - Submit offer to posting
PUT    /offer/{offerId}               - Update offer
DELETE /offer/{offerId}               - Withdraw offer
PATCH  /offer/{offerId}/status        - Change offer status (accept/reject/etc)
```

### Messaging & Notifications

```
GET    /message/conversations         - List message conversations
GET    /message/conversation/{id}     - Get conversation messages
POST   /message                       - Send message (WebSocket preferred)
GET    /notification                  - Get user notifications
PUT    /notification/{notifId}/read   - Mark notification as read
DELETE /notification/{notifId}        - Delete notification
```

### Admin Endpoints

```
GET    /admin/users                   - List all users (Admin only)
GET    /admin/users/{userId}          - Get user details (Admin only)
PUT    /admin/users/{userId}          - Modify user (Admin only)
DELETE /admin/users/{userId}          - Delete/ban user (Admin only)
GET    /admin/postings                - List all postings (Admin only)
GET    /admin/statistics              - Get platform statistics
GET    /admin/reports                 - List user reports
PATCH  /admin/reports/{reportId}      - Process report
PUT    /admin/system-config           - Update system configuration
```

### Health & Monitoring

```
GET    /actuator/health               - Application health status
GET    /actuator/metrics              - Application metrics
GET    /actuator/info                 - Application info
```

## 🔐 Authentication

### JWT Token Flow

1. **User Registration/Login**
   - User submits credentials to `/auth/login`
   - Server validates and generates JWT token
   - Token contains: userId, email, roles, expiration

2. **Token Usage**
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

3. **Token Validation**
   - Every protected endpoint validates JWT
   - JwtAuthenticationFilter extracts and validates token
   - Expired tokens return 401 Unauthorized

4. **Token Refresh** (if implemented)
   ```
   POST /auth/refresh
   Body: { "refreshToken": "..." }
   ```

### User Roles

- **USER**: Basic user (assigned on registration)
- **CLIENT**: Can post jobs (verified client)
- **FREELANCER**: Can submit offers (profile complete)
- **ADMIN**: Full platform access

## 🔌 WebSocket Events

### Connection

```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({ 
  'Authorization': 'Bearer ' + token 
}, onConnect, onError);
```

### Event Subscriptions

```javascript
// Subscribe to personal notifications
stompClient.subscribe('/user/notifications', (message) => {
  console.log('Notification:', JSON.parse(message.body));
});

// Subscribe to conversation messages
stompClient.subscribe('/user/messages/conversation/{conversationId}', 
  (message) => {
    console.log('Message:', JSON.parse(message.body));
  }
);

// Subscribe to real-time updates
stompClient.subscribe('/user/updates', (message) => {
  console.log('Update:', JSON.parse(message.body));
});
```

### Sending Messages

```javascript
// Send message to conversation
stompClient.send('/app/message/send', {}, JSON.stringify({
  conversationId: '123',
  content: 'Hello!',
  recipientId: 'user-id'
}));
```

## 🗄 Database

### Entity Relationships

```
User (1) ──── (1) Client
     (1) ──── (1) Freelancer
     (1) ──── (*) Posting
     (1) ──── (*) Offer
     (1) ──── (*) Review
     (1) ──── (*) Notification

Posting (*) ──── (*) Skill
       (1) ──── (*) Offer
       (1) ──── (*) Message

Offer (1) ──── (1) Posting
    (1) ──── (1) Freelancer
    (1) ──── (*) Deliverable
```

### Key Tables

- **users** - Core user data
- **clients** - Client-specific info
- **freelancers** - Freelancer-specific info
- **postings** - Job postings
- **offers** - Freelancer proposals
- **messages** - Chat messages
- **notifications** - User alerts
- **reviews** - User feedback/ratings
- **skills** - Skill catalog
- **categories** - Job categories

## 📧 Email Configuration

### Email Templates

Email templates are stored in `src/main/resources/templates/`:

```
templates/
├── email/
│   ├── registration-welcome.html
│   ├── email-verification.html
│   ├── password-reset.html
│   ├── offer-notification.html
│   ├── message-notification.html
│   └── batch-digest.html
```

### Sending Emails

```java
@Service
public class EmailService {
    
    @Async
    public void sendWelcomeEmail(User user) {
        // Render template
        // Send email asynchronously
    }
    
    @Scheduled(cron = "0 0 * * * *")  // Daily at midnight
    public void sendDailyDigest() {
        // Batch send emails
    }
}
```

## 👨‍💻 Development Guidelines

### Code Organization

1. **Controllers** - Handle HTTP requests, validation, routing
2. **Services** - Implement business logic, transactions
3. **Repositories** - Data access layer (Spring Data JPA)
4. **Entities** - Domain models with JPA annotations
5. **DTOs** - Request/Response transfer objects

### Best Practices

```java
// 1. Use DTOs for API contracts (not entities)
@PostMapping("/posting")
public ResponseEntity<PostingDTO> createPosting(
    @RequestBody CreatePostingRequest request
) { }

// 2. Use Services for business logic
@Service
@Transactional
public class PostingService {
    public PostingDTO createPosting(CreatePostingRequest request) {
        // Business logic here
    }
}

// 3. Use @Validated for input validation
@PostMapping
public ResponseEntity<UserDTO> register(
    @Valid @RequestBody RegisterRequest request
) { }

// 4. Use exception handlers for consistent error responses
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(Exception e) { }
}

// 5. Use async for long-running tasks
@Service
public class EmailService {
    @Async
    public void sendBatchEmails(List<User> users) { }
}

// 6. Add meaningful logs
logger.info("User registered: {}", user.getEmail());
logger.error("Failed to process offer: {}", offerId, exception);
```

### Creating New Features

1. **Create Entity** in `features/{feature}/entity/`
   ```java
   @Entity
   @Table(name = "my_feature")
   public class MyFeature extends BaseEntity {
       // Properties
   }
   ```

2. **Create Repository** in `features/{feature}/repository/`
   ```java
   public interface MyFeatureRepository 
       extends JpaRepository<MyFeature, UUID> { }
   ```

3. **Create Service** in `features/{feature}/service/`
   ```java
   @Service
   @Transactional
   public class MyFeatureService {
       public MyFeatureDTO create(CreateRequest request) { }
   }
   ```

4. **Create Controller** in `features/{feature}/controller/`
   ```java
   @RestController
   @RequestMapping("/api/my-feature")
   public class MyFeatureController {
       @PostMapping
       public ResponseEntity<MyFeatureDTO> create(
           @Valid @RequestBody CreateRequest request
       ) { }
   }
   ```

5. **Create DTOs** in `features/{feature}/dto/`
   ```java
   @Data
   public class MyFeatureDTO { }
   ```

## 🔨 Building & Deployment

### Local Build

```bash
# Build without tests
mvn clean package -DskipTests

# Build with tests
mvn clean package

# View build output
ls -la target/
```

### Docker Build (Optional)

Create `Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/freelance-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
docker build -t talentflow-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/talentflow_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  talentflow-backend
```

### Environment Variables for Deployment

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://prod-db-host:3306/talentflow_db
SPRING_DATASOURCE_USERNAME=db_user
SPRING_DATASOURCE_PASSWORD=secure_password
JWT_SECRET=your-long-secret-key-256-chars-minimum
MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=sender@example.com
MAIL_PASSWORD=app-password
```

### Production Deployment Checklist

- [ ] Database backups configured
- [ ] JWT secret key is strong and secure
- [ ] Email credentials stored in environment variables
- [ ] CORS origins restricted to production domain
- [ ] SSL/TLS enabled
- [ ] Rate limiting configured
- [ ] Application logging configured
- [ ] Monitoring and alerting set up
- [ ] Database migrations tested
- [ ] Load testing completed

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/new-feature`
2. Make your changes and commit: `git commit -am 'Add new feature'`
3. Push to branch: `git push origin feature/new-feature`
4. Submit a pull request with description

## 📝 License

This project is part of a university thesis project. All rights reserved.

## 📞 Support

For issues or questions, please contact the development team or create an issue in the repository.

---

**Last Updated:** May 2026  
**Version:** 0.0.1-SNAPSHOT  
**Java Version:** 21  
**Spring Boot Version:** 4.0.2
