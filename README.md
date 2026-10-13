🎓 Exam Management System (EMS) - Online Examination Service

Group - 18
Dev Dave : 20251651034
Arjun Bodana : 20251651029
Vikash Kumar : 20251651029
Kuldeep Rout : 20251651053

Welcome to the Core API for the Exam Management System (EMS). This project is a highly robust, RESTful microservice built in Python. It manages live examination sessions and timers, demonstrating production-ready backend engineering principles, network resilience, and strict API contract enforcement.

This repository focuses on the Online Examination Service, which interacts with external upstream microservices (Authentication and Exam Schedule Management) in a decoupled, fault-tolerant architecture.

✨ Key Features

This service isn't just a basic CRUD app; it implements advanced HTTP and REST semantics to ensure reliability in a distributed system:

🛡️ Network Resilience: Cross-service HTTP calls use strict timeouts, exponential backoff, and randomized jitter to survive network drops without overloading dependencies.

🔁 Idempotent Writes: Uses Idempotency-Key headers to safely retry session creations (e.g., POST /exam-sessions) without accidentally duplicating database records.

🚦 Optimistic Concurrency Control: Implements ETag-based caching and validation (If-Match, If-None-Match) to prevent mid-air collisions when multiple proctors update a session timer simultaneously.

🛑 Standardized Error Handling: All failures return a uniform RFC 7807 application/problem+json shape, ensuring frontend clients always know exactly how to parse an error.

⚖️ Rate Limiting: Built-in fixed-window rate limiting with standard headers (X-RateLimit-Limit, X-RateLimit-Remaining, Retry-After) to protect the service from abuse.

🔒 Security by Design: Clean separation of internal database models from public JSON representations. Internal keys (like user IDs and internal row IDs) never leak to the client.

🏗️ Tech Stack

Language: Python 3.9+ (Raw WSGI Application)

API Specification: OpenAPI 3.0.3 (yaml)

Testing: pytest

Validation: openapi-spec-validator, PyYAML
