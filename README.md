# Exam Management System (EMS)

## Project Team — Group 18

| Student ID | Team Member |
|---|---|
| 20251651034 | Dave Dev Rupeshkumar |
| 20251651053 | Kuldeep Rout |
| 20251651102 | Vikash Kumar |
| 20251651029 | Arjun Bodana |

---

## Working Demonstration

**[Watch the Project Demonstration](https://drive.google.com/file/d/1YHTr4C86DSjM63jwR5ihjT-Xy6B0uZnp)**

The demonstration video showcases the application's workflows and API implementation.

---

## About the Project

The **Exam Management System (EMS)** is a Java-based web application developed using Spring Boot. It provides REST APIs for authentication, user management, and quiz management, along with a web interface featuring role-specific dashboards.

The project demonstrates REST API design, HTTP status codes, request validation, standardized error handling, idempotency, conditional requests, caching, rate limiting, and API documentation using OpenAPI.

**Repository:** [https://github.com/devdave17/EMS](https://github.com/devdave17/EMS)

## Key Features

### 1. Authentication and Security
- User login through a REST API.
- JWT-based authentication components.
- Spring Security configuration and bearer-token filtering.
- Role-aware pages for students, teachers, and administrators.
- Protected API access based on the application's security configuration.

### 2. User Management Service
- Create new users.
- Retrieve all users.
- Retrieve an individual user by ID.
- Update existing user information.
- Delete users.
- Validate request data.
- Return a `Location` header when creating a resource.
- Support idempotency keys for user creation.
- Detect conflicting requests when an idempotency key is reused with a different payload.
- Support ETag-based conditional requests.
- Use `If-None-Match` for conditional retrieval and `304 Not Modified` responses.
- Use `If-Match` to prevent updates based on stale entity versions.
- Return `412 Precondition Failed` when an update fails the supplied ETag condition.

### 3. Quiz Management Service
- Create quizzes with a title and description.
- Retrieve quizzes by ID.
- Retrieve all quizzes.
- Filter quizzes by status.
- Update quiz status.
- Require an idempotency key when creating a quiz.
- Return `201 Created` with a `Location` header for newly created quizzes.
- Return the existing resource for an identical repeated creation request.
- Return `409 Conflict` when the same idempotency key is reused with a different request.

### 4. API Error Handling and Validation
- Centralized exception handling using Spring's controller advice mechanism.
- Problem Details-style JSON error responses.
- Standard error fields: `type`, `title`, `status`, and `detail`.
- Field-level validation errors through an `errors` array.
- Generic internal-error responses that avoid exposing implementation details.
- Appropriate HTTP status codes for common request failures.
- Validation using Jakarta Bean Validation.

### 5. Rate Limiting
- Request rate limiting to reduce excessive API requests.
- `429 Too Many Requests` response when the configured limit is exceeded.
- `Retry-After` header indicating when the client can retry.
- `X-RateLimit-Limit` and `X-RateLimit-Remaining` response headers.

### 6. HTTP and REST Concepts
- Appropriate HTTP methods and status codes.
- Resource creation using `201 Created`.
- Resource identification using the `Location` header.
- Idempotency keys for retry-safe creation requests.
- ETag-based conditional requests.
- Optimistic concurrency control.
- Cache validation using `If-None-Match`.
- Content negotiation checks using the `Accept` header.
- Standardized error representations.

### 7. Web Interface
- Thymeleaf-based web pages.
- Login page.
- Student dashboard.
- Teacher dashboard.
- Admin dashboard.
- Static CSS assets for application styling.

### 8. API Documentation
- OpenAPI 3.0.3 specification.
- Endpoint descriptions and request schemas.
- Documented response codes and error schemas.
- API specification available at `src/main/resources/openapi.yaml`.

---

## Technology Stack

| Component | Technology |
|---|---|
| Programming Language | Java 21 |
| Backend Framework | Spring Boot |
| Build Tool | Gradle |
| API Architecture | REST |
| Database Integration | Spring Data MongoDB |
| Security | Spring Security and JWT components |
| Validation | Jakarta Bean Validation |
| Web Templates | Thymeleaf |
| JSON/XML Processing | Jackson |
| API Documentation | OpenAPI 3.0.3 |
| Testing Support | JUnit Platform and Spring Boot test dependencies |

---

## Project Structure

```text
EMS/
├── src/
│   └── main/
│       ├── java/com/IIITV/EMS/
│       │   ├── Controller/
│       │   │   ├── AuthController.java
│       │   │   ├── HomeController.java
│       │   │   ├── QuizController.java
│       │   │   └── UserController.java
│       │   ├── DTO/
│       │   │   └── LoginRequest.java
│       │   ├── Model/
│       │   │   ├── IdempotencyRecord.java
│       │   │   ├── Quiz.java
│       │   │   ├── QuizStatus.java
│       │   │   ├── Role.java
│       │   │   ├── User.java
│       │   │   └── UserStatus.java
│       │   ├── Repository/
│       │   ├── Security/
│       │   ├── Services/
│       │   ├── EmsApplication.java
│       │   └── GlobalExceptionHandler.java
│       └── resources/
│           ├── openapi.yaml
│           ├── static/
│           └── templates/
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
└── gradlew.bat
```

---

## API Reference

The default local development URL is:

```text
http://localhost:8080
```

### Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Authenticate a user |

Example request:

```json
{
  "email": "student@example.com",
  "password": "your-password"
}
```

### User Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users` | Create a user |
| `GET` | `/api/users` | Retrieve all users |
| `GET` | `/api/users/{id}` | Retrieve a user by ID |
| `PUT` | `/api/users/{id}` | Update a user |
| `DELETE` | `/api/users/{id}` | Delete a user |
| `OPTIONS` | `/api/users` | Retrieve supported HTTP methods |

### Quiz Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/quizzes` | Create a quiz |
| `GET` | `/api/quizzes` | Retrieve quizzes, optionally filtered by status |
| `GET` | `/api/quizzes/{id}` | Retrieve a quiz by ID |
| `PUT` | `/api/quizzes/{id}/status` | Update quiz status |

For detailed request schemas and documented responses, refer to [`src/main/resources/openapi.yaml`](src/main/resources/openapi.yaml).

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21
- Git
- A MongoDB instance
- Internet access for downloading Gradle dependencies

### Step 1: Clone the Repository

```bash
git clone https://github.com/devdave17/EMS.git
cd EMS
```

### Step 2: Configure MongoDB

Configure the MongoDB connection using the property names expected by the application.

Ensure that:
- The MongoDB connection string is correct.
- The database user has the required permissions.
- Your IP address is allowed by the database network configuration.

**Security note:** Never commit database credentials, JWT secrets, or other sensitive configuration values to the repository.

### Step 3: Run the Application

**Windows Command Prompt:**

```bat
gradlew.bat bootRun
```

**macOS/Linux:**

```bash
./gradlew bootRun
```

After the application starts, open:

```text
http://localhost:8080
```

---

## Example API Requests

The following commands are examples for local testing. Use credentials and tokens configured for your environment.

### 1. Login

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"your-password"}'
```

### 2. Create a User

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: user-create-demo-001" \
  -d '{"name":"Demo User","email":"demo@example.com","password":"ChangeThisPassword123!","role":"STUDENT","status":"ACTIVE"}'
```

### 3. Retrieve Users

```bash
curl -i http://localhost:8080/api/users \
  -H "Accept: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Create a Quiz

```bash
curl -i -X POST http://localhost:8080/api/quizzes \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: quiz-create-demo-001" \
  -d '{"title":"Java Fundamentals","description":"Core Java concepts"}'
```

### 5. Retrieve Quizzes

```bash
curl -i http://localhost:8080/api/quizzes
```

Replace example passwords and tokens with your own test values. Actual responses depend on the database contents and application configuration.

---

## Important HTTP Behaviors

| Concept | Implementation |
|---|---|
| Resource creation | `201 Created` and `Location` header |
| Idempotent creation | `Idempotency-Key` header |
| Conflicting idempotency request | `409 Conflict` |
| Conditional retrieval | ETag and `If-None-Match` |
| Conditional update | ETag and `If-Match` |
| Unchanged resource | `304 Not Modified` |
| Failed update precondition | `412 Precondition Failed` |
| Request validation | Validation error responses |
| Standardized errors | Problem Details-style JSON |
| Rate limiting | `429 Too Many Requests` and `Retry-After` |
| API contract | OpenAPI specification |

---

## Security Considerations

- Keep database credentials and signing secrets outside source control.
- Use strong passwords and environment-specific configuration.
- Use test credentials for demonstrations.
- Do not expose sensitive user information in API responses.
- Verify security rules before deploying the application publicly.

## Repository Links

- **Source Code:** [GitHub Repository](https://github.com/devdave17/EMS)
- **Working Demonstration:** [Watch on Google Drive](https://drive.google.com/file/d/1YHTr4C86DSjM63jwR5ihjT-Xy6B0uZnp)
- **API Specification:** [`openapi.yaml`](src/main/resources/openapi.yaml)

---

## License

No license has been specified for this repository yet.
