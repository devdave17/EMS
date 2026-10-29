# Exam Management System (EMS)

## Project Team --- Group 18

  Student ID    Team Member
  ------------- ----------------------
  20251651034   Dave Dev Rupeshkumar
  20251651053   Kuldeep Rout
  20251651102   Vikash Kumar
  20251651029   Arjun Bodana

------------------------------------------------------------------------

## Working Demonstration


## About the Project

The **Exam Management System (EMS)** is a Java-based web application
built with Spring Boot and MongoDB. It provides REST APIs and
role-specific interfaces for authentication, user management, quiz
management, student exam sessions, teacher results, and proctoring
review.

**Repository:** <https://github.com/devdave17/EMS>

## Key Features

-   JWT-based authentication components and role-aware access for
    students, teachers, and administrators.
-   User CRUD operations, request validation, idempotency keys, and
    ETag-based conditional requests.
-   Quiz creation, retrieval, status filtering, and status updates.
-   Student exam session start, session retrieval, answer saving,
    warning-count saving, and submission.
-   Proctoring fields including `totalWarnings`, `isFlagged`, and
    `reviewStatus`.
-   Teacher result listing and quiz result publishing.
-   Standardized error handling, HTTP status codes, rate limiting, and
    OpenAPI documentation.

**Proctoring note:** `PENDING_REVIEW` means a submission still requires
teacher review. Result-release rules must be enforced by the backend;
hiding a frontend button alone is not sufficient. The exact
review-resolution endpoint should be documented once implemented and
verified.

## Technology Stack

  Component              Technology
  ---------------------- -------------------------------------------
  Programming Language   Java 21
  Backend Framework      Spring Boot
  Build Tool             Gradle
  API Architecture       REST
  Database               Spring Data MongoDB
  Security               Spring Security and JWT components
  Validation             Jakarta Bean Validation
  Web Interface          Thymeleaf and JavaScript-based interfaces
  API Documentation      OpenAPI 3.0.3

## Project Structure

``` text
EMS/
├── src/main/java/com/IIITV/EMS/
│   ├── Controller/
│   │   ├── AuthController.java
│   │   ├── HomeController.java
│   │   ├── QuizController.java
│   │   ├── UserController.java
│   │   ├── StudentExamController.java
│   │   └── TeacherResultController.java
│   ├── DTO/
│   ├── Model/
│   ├── Repository/
│   ├── Security/
│   ├── Services/
│   └── GlobalExceptionHandler.java
└── src/main/resources/
    ├── openapi.yaml
    ├── static/
    └── templates/
```

*The tree shows the main areas and controllers discussed for the
project; exact files may vary by branch.*

------------------------------------------------------------------------

## API Reference

Default local URL: `http://localhost:8080`

For protected endpoints, send the bearer token expected by the
application's security configuration:

``` http
Authorization: Bearer YOUR_TOKEN
```

### Authentication

  Method   Endpoint            Description
  -------- ------------------- ---------------------
  `POST`   `/api/auth/login`   Authenticate a user

Example request:

``` json
{
  "email": "student@example.com",
  "password": "your-password"
}
```

### User Management

  Method      Endpoint            Description
  ----------- ------------------- ----------------------------------------
  `POST`      `/api/users`        Create a user
  `GET`       `/api/users`        Retrieve users
  `GET`       `/api/users/{id}`   Retrieve a user by ID
  `PUT`       `/api/users/{id}`   Update a user
  `DELETE`    `/api/users/{id}`   Delete a user
  `OPTIONS`   `/api/users`        Retrieve supported methods, if enabled

User creation may use an `Idempotency-Key`. ETag conditional requests
and their response codes depend on the configured controller behavior.

### Quiz Management

  ----------------------------------------------------------------------------
  Method                  Endpoint                     Description
  ----------------------- ---------------------------- -----------------------
  `POST`                  `/api/quizzes`               Create a quiz

  `GET`                   `/api/quizzes`               Retrieve quizzes; may
                                                       support status
                                                       filtering

  `GET`                   `/api/quizzes/{id}`          Retrieve a quiz by ID

  `PUT`                   `/api/quizzes/{id}/status`   Update quiz status
  ----------------------------------------------------------------------------

Quiz creation uses an idempotency key according to the existing API
behavior.

### Student Exams and Sessions

Base path: `/api/student/exams`

  ----------------------------------------------------------------------------------------------------
  Method                  Endpoint                                             Description
  ----------------------- ---------------------------------------------------- -----------------------
  `GET`                   `/api/student/exams`                                 Retrieve exams for the
                                                                               authenticated student

  `POST`                  `/api/student/exams/{quizId}/start`                  Start an exam session

  `GET`                   `/api/student/exams/sessions/{sessionId}`            Retrieve the student's
                                                                               exam session

  `PUT`                   `/api/student/exams/sessions/{sessionId}/answers`    Save answers for an
                                                                               in-progress session

  `PUT`                   `/api/student/exams/sessions/{sessionId}/warnings`   Save total proctoring
                                                                               warning count

  `POST`                  `/api/student/exams/sessions/{sessionId}/submit`     Submit an exam session
  ----------------------------------------------------------------------------------------------------

Example warning-count request:

``` http
PUT /api/student/exams/sessions/SESSION_ID/warnings
Content-Type: application/json
Authorization: Bearer STUDENT_TOKEN
```

``` json
{
  "totalWarnings": 5
}
```

**Important:** Save the warning count before submitting. The warning
endpoint requires the session to be `IN_PROGRESS`; calling it after
submission can return `409 Conflict`.

### Teacher Results

Base path: `/api/teacher/results`

  -----------------------------------------------------------------------------------------
  Method                  Endpoint                                  Description
  ----------------------- ----------------------------------------- -----------------------
  `GET`                   `/api/teacher/results`                    Retrieve results for
                                                                    quizzes created by the
                                                                    authenticated teacher

  `POST`                  `/api/teacher/results/{quizId}/publish`   Publish results for a
                                                                    quiz, subject to
                                                                    backend rules
  -----------------------------------------------------------------------------------------

Each submission may include these fields in the teacher results
response:

  -----------------------------------------------------------------------
  Field                               Description
  ----------------------------------- -----------------------------------
  `sessionId`                         Exam session identifier

  `studentId`                         Student identifier

  `status`                            Exam-session status

  `score`                             Score achieved

  `totalMarks`                        Maximum marks

  `correctAnswers`                    Number of correct answers

  `answeredQuestions`                 Number of answered questions

  `submittedAt`                       Submission date/time

  `totalWarnings`                     Total recorded proctoring warnings

  `isFlagged`                         Whether the submission is flagged

  `reviewStatus`                      Review state, such as
                                      `PENDING_REVIEW`, `CLEARED`,
                                      `REVIEWED`, or `NOT_REQUIRED`
  -----------------------------------------------------------------------

The exact JSON types depend on the DTO. Keep the DTO aligned with the
frontend.

**Result-release rule:** A `PENDING_REVIEW` submission must not be
treated as cleared. If the implementation blocks publication for the
entire quiz, all quiz results remain unpublished until review is
resolved. If publication is per student, enforce the restriction when
serving individual student results.

### Endpoint Summary

  Area              Base path
  ----------------- ------------------------
  Authentication    `/api/auth`
  User management   `/api/users`
  Quiz management   `/api/quizzes`
  Student exams     `/api/student/exams`
  Teacher results   `/api/teacher/results`

See [`src/main/resources/openapi.yaml`](src/main/resources/openapi.yaml)
for additional request/response schemas and documented response codes.
Keep it synchronized with controller changes.

------------------------------------------------------------------------

## Getting Started

### Prerequisites

-   JDK 21
-   Git
-   MongoDB instance
-   Internet access for Gradle dependencies

### 1. Clone the Repository

``` bash
git clone https://github.com/devdave17/EMS.git
cd EMS
```

### 2. Configure MongoDB

Configure the MongoDB connection using the property names expected by
the application. Ensure the connection string, database permissions, and
network access rules are correct.

**Security:** Never commit database credentials, JWT secrets, or other
sensitive values.

### 3. Run the Application

Windows Command Prompt:

``` bat
gradlew.bat bootRun
```

macOS/Linux:

``` bash
./gradlew bootRun
```

Open `http://localhost:8080` after the application starts.

------------------------------------------------------------------------

## Example API Requests

Use test credentials, IDs, and tokens for your environment.

### Login

``` bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"your-password"}'
```

### Retrieve Users

``` bash
curl -i http://localhost:8080/api/users \
  -H "Accept: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Create a Quiz

``` bash
curl -i -X POST http://localhost:8080/api/quizzes \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: quiz-create-demo-001" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Java Fundamentals","description":"Core Java concepts"}'
```

### Retrieve Teacher Results

``` bash
curl -i http://localhost:8080/api/teacher/results \
  -H "Accept: application/json" \
  -H "Authorization: Bearer TEACHER_TOKEN"
```

### Save Proctoring Warning Count

``` bash
curl -i -X PUT http://localhost:8080/api/student/exams/sessions/SESSION_ID/warnings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer STUDENT_TOKEN" \
  -d '{"totalWarnings":5}'
```

### Submit an Exam

``` bash
curl -i -X POST http://localhost:8080/api/student/exams/sessions/SESSION_ID/submit \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

Save warnings before submitting the exam.

### Publish Quiz Results

``` bash
curl -i -X POST http://localhost:8080/api/teacher/results/QUIZ_ID/publish \
  -H "Authorization: Bearer TEACHER_TOKEN"
```

------------------------------------------------------------------------

## Important HTTP Behaviors

  -----------------------------------------------------------------------
  Concept                             Behavior
  ----------------------------------- -----------------------------------
  Resource creation                   `201 Created` and `Location` header
                                      where configured

  Idempotent creation                 `Idempotency-Key` header

  Conflicting idempotency request     `409 Conflict`

  Conditional retrieval               ETag and `If-None-Match`, where
                                      configured

  Conditional update                  ETag and `If-Match`, where
                                      configured

  Unchanged resource                  `304 Not Modified`, where
                                      configured

  Failed update precondition          `412 Precondition Failed`, where
                                      configured

  Rate limiting                       `429 Too Many Requests` and
                                      `Retry-After`, where configured

  Validation                          Validation error responses

  Proctoring warning save after       Rejected because the session is no
  submission                          longer `IN_PROGRESS`
  -----------------------------------------------------------------------

## Security Considerations

-   Keep database credentials and signing secrets outside source
    control.
-   Use strong passwords and environment-specific configuration.
-   Do not expose sensitive user information in API responses.
-   Enforce role checks, ownership checks, and result-release rules on
    the backend.
-   Do not rely only on frontend controls to protect pending-review
    results.
-   Verify security rules before public deployment.

## Repository Links

-   **Source Code:** [GitHub
    Repository](https://github.com/devdave17/EMS)
-   **API Specification:**
    [`openapi.yaml`](src/main/resources/openapi.yaml)

------------------------------------------------------------------------

## License

No license has been specified for this repository yet.
