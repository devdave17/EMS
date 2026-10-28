# Assignment 6 — NOTES.md
## CS543 Web Services — EMS User Service

**Team ID:** 18  
**Repository:** https://github.com/devdave17/EMS  
**Service:** EMS User Service  
**Base URL used in the transcript:** `http://localhost:8080`

### Team members

- 20251651034 — Dave Dev Rupeshkumar
- 20251651053 — Kuldeep Rout
- 20251651102 — Vikash Kumar
- 20251651029 — Arjun Bodana

> **Scope note:** The Assignment 6 handout uses an Orders service in its examples. Our implementation is the existing EMS User Service, so the status map and examples below are adapted to `/api/users`. The status codes marked as expected mappings are recommendations for consistent behaviour; they must not be mistaken for captured runtime results. The attached curl transcript currently shows `500` for invalid user data and unsupported `Accept` values, and the XML request was not successfully negotiated.

---

## 1. Status map

The service endpoints covered here are the collection endpoints and single-user resource endpoints. Confirm the exact route mappings against the current controller before submission.

| Endpoint | Success status | Failure status | Why |
|---|---:|---:|---|
| `POST /api/users` | `201 Created` | `400 Bad Request` | The JSON body is malformed or cannot be parsed. |
| `POST /api/users` | `201 Created` | `401 Unauthorized` | The request has no required bearer token; the transcript captured this response on a protected endpoint. |
| `POST /api/users` | `201 Created` | `409 Conflict` | The email or another unique field already exists, if uniqueness is enforced. |
| `POST /api/users` | `201 Created` | `422 Unprocessable Content` | JSON parses, but one or more fields fail validation. |
| `POST /api/users` | `201 Created` | `429 Too Many Requests` | The configured rate limit has been exceeded; provide `Retry-After` if the server can state when to retry. |
| `GET /api/users` | `200 OK` | `401 Unauthorized` | The bearer token is missing; this was captured in the transcript. |
| `GET /api/users` | `200 OK` | `406 Not Acceptable` | The client requests a representation the service cannot produce. The transcript currently shows `500` for `Accept: application/pdf`, so this needs fixing and retesting. |
| `GET /api/users/{id}` | `200 OK` | `400 Bad Request` | The supplied ID has an invalid format, if the controller validates ID syntax. |
| `GET /api/users/{id}` | `200 OK` | `401 Unauthorized` | The required bearer token is missing or invalid. |
| `GET /api/users/{id}` | `200 OK` | `404 Not Found` | No user exists with the supplied ID. The transcript has previously captured a `user-not-found` response. |
| `GET /api/users/{id}` | `200 OK` | `406 Not Acceptable` | The requested response representation is unsupported. |
| `PUT /api/users/{id}` | `200 OK` | `400 Bad Request` | The request body is malformed or the ID format is invalid. |
| `PUT /api/users/{id}` | `200 OK` | `401 Unauthorized` | The required bearer token is missing or invalid. |
| `PUT /api/users/{id}` | `200 OK` | `404 Not Found` | The target user does not exist. |
| `PUT /api/users/{id}` | `200 OK` | `409 Conflict` | The update conflicts with a uniqueness or current-state rule. |
| `PUT /api/users/{id}` | `200 OK` | `422 Unprocessable Content` | One or more user fields fail validation. |
| `DELETE /api/users/{id}` | `204 No Content` | `400 Bad Request` | The supplied ID format is invalid. |
| `DELETE /api/users/{id}` | `204 No Content` | `401 Unauthorized` | The required bearer token is missing or invalid. |
| `DELETE /api/users/{id}` | `204 No Content` | `404 Not Found` | No user exists with the supplied ID. |

**Important verification note:** `POST /api/users` returning `201` with a `Location` header, `GET /api/users` returning `200`, and a missing-token request returning `401` are captured in the transcript. The transcript does **not** verify every possible failure listed above. In particular, confirm that `PUT` and `DELETE` routes exist in the current service, and test the `400`, `409`, `422`, `429`, and `406` cases before describing them as implemented.

---

## 2. Problem Details type catalogue

Use stable type strings so clients can distinguish error categories without parsing human-readable messages.

| Stable `type` value | Trigger | Intended status |
|---|---|---:|
| `malformed-request` | Invalid JSON syntax or malformed request representation. | `400` |
| `user-not-found` | A requested user ID does not identify an existing user. | `404` |
| `validation-failed` | JSON is syntactically valid, but fields such as `name`, `email`, `role`, or `status` are invalid or missing. | `422` |
| `duplicate-user` | A user conflicts with an existing unique email or other unique identifier, if uniqueness is enforced. | `409` |
| `unsupported-media-type` | The request `Content-Type` is not supported. | `415` |
| `not-acceptable` | No representation supported by the service matches the request's `Accept` header. | `406` |
| `rate-limit-exceeded` | The caller exceeds the configured request limit. | `429` |
| `internal-error` | An unexpected server-side failure occurs. | `500` |

The transcript has a captured `404` body with `type: "user-not-found"`. It also has a captured `500` Problem Details body with `type: "about:blank"` for invalid user data and unsupported `Accept` values. Therefore, the catalogue above is the **consistent target catalogue**; verify the implementation actually returns these types and statuses. Do not claim that every mapping is already working until the corresponding request has been tested.

The missing-token response currently uses a different shape (`{"status":401,"error":"Bearer token required"}`) and `Content-Type: application/json;charset=ISO-8859-1`. It should be routed through the same Problem Details format if the service's error handler/security configuration permits it.

---

## 3. Example Problem Details body and field labels

A body captured in the transcript for a missing user is:

```json
{
  "detail": "User not found",
  "title": "Not Found",
  "status": 404,
  "type": "user-not-found"
}
```

- **`type`** — Stable machine-readable error category: `user-not-found`.
- **`title`** — Short human-readable summary: `Not Found`.
- **`status`** — HTTP status code: `404`.
- **`detail`** — Human-readable explanation specific to this failure: `User not found`.

The error response was served with `Content-Type: application/problem+json;charset=UTF-8` in the captured transcript. The `detail` should not expose database internals, stack traces, credentials, or implementation-specific messages.

---

## 4. Field-level `422` with two invalid fields

The current transcript does **not** show a successful `422` validation response. Instead, its invalid-user-data request returned `500 Internal Server Error`. The following is the **expected example body after the validation handler is corrected**, not a captured response:

Request example:

```http
POST /api/users
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "name": "",
  "email": "not-an-email",
  "role": "STUDENT",
  "status": "ACTIVE"
}
```

Expected response:

```http
HTTP/1.1 422 Unprocessable Content
Content-Type: application/problem+json;charset=UTF-8
```

```json
{
  "type": "validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "One or more fields failed validation",
  "errors": [
    {
      "field": "name",
      "message": "Name must not be blank"
    },
    {
      "field": "email",
      "message": "Email must be a valid email address"
    }
  ]
}
```

The `errors` array reports both invalid fields in one response. Implement and run this test before presenting it as actual service output.

---

## 5. Safe retries and the status codes that help clients

- **`GET /api/users` and `GET /api/users/{id}`:** Safe to retry because GET is intended to read data without changing server state. A client can retry transient failures such as `503 Service Unavailable`; `429 Too Many Requests` should be retried only after the indicated delay, preferably using `Retry-After`.
- **`PUT /api/users/{id}`:** HTTP PUT is intended to be idempotent when implemented correctly. Repeating the same full replacement should leave the resource in the same state. Retry only transient failures, not an unchanged `4xx` response.
- **`DELETE /api/users/{id}`:** HTTP DELETE is intended to be idempotent, but the API's repeated-delete behaviour should be documented. Retry only transient failures; the second attempt may return `404` if the first delete succeeded.
- **`POST /api/users`:** Do not blindly retry a create because it can create duplicates. Although the transcript sends an `Idempotency-Key`, verify that the server stores and replays the original result for the same key before claiming POST is safely retryable.

Status codes are not, by themselves, a guarantee that a retry is safe. `429` and `503` can indicate a temporary condition; include `Retry-After` where appropriate. Do not retry an unchanged client error (`400`, `401`, `404`, `409`, or `422`) without changing the request or credentials.

---

## 6. Mapping internal errors to clean client errors

The transcript demonstrates a generic error response for unexpected failures:

```json
{
  "title": "Internal Server Error",
  "detail": "An unexpected error occurred",
  "type": "about:blank",
  "status": 500
}
```

This is safer than returning a stack trace or raw database exception to the caller. The caller sees a generic message, while the detailed exception should be logged server-side for debugging.

However, the invalid-user-data request and unsupported `Accept` requests both returned `500` in the captured transcript. These are not the correct final mappings for ordinary validation or content-negotiation failures:

- Invalid fields should be validated before persistence and returned as `422` with an `errors` array.
- Unsupported `Accept` should return `406 Not Acceptable` if no requested representation can be produced.

Thus, the transcript shows that internal details were hidden for these errors, but it also reveals that the failure categories need to be mapped more precisely. Do not describe the `422` and `406` fixes as verified until they return the expected responses.

---

## 7. JSON and XML content negotiation (if done)

The current transcript does **not** demonstrate successful XML negotiation. The request with `Accept: application/xml` returned `500 Internal Server Error` with a Problem Details JSON body. Therefore, JSON/XML dual-format support is **not verified** in the captured evidence.

If XML support is implemented, the same user representation should be requested as follows.

JSON request:

```http
GET /api/users/{id}
Accept: application/json
Authorization: Bearer <token>
```

Expected response header:

```http
Content-Type: application/json
```

XML request:

```http
GET /api/users/{id}
Accept: application/xml
Authorization: Bearer <token>
```

Expected response header:

```http
Content-Type: application/xml
```

The XML body should represent the same user fields (`id`, `name`, `email`, `role`, `status`) as the JSON response. If XML cannot be produced, return `406 Not Acceptable` rather than `500`. Capture actual `curl -i` output for both formats before marking this optional item complete.

---

## Evidence and remaining checks before submission

- [x] `POST /api/users` success captured as `201 Created`, with a `Location` header.
- [x] `GET /api/users` success captured as `200 OK`.
- [x] Missing bearer token captured as `401 Unauthorized` (currently not Problem Details format).
- [x] Missing user captured as `404` with `type: user-not-found` in prior transcript evidence.
- [ ] Fix and capture field-level `422` with two field errors.
- [ ] Fix and capture `406` for an unsupported `Accept` header.
- [ ] Decide and test the stable Problem Details type catalogue.
- [ ] Verify `Retry-After` for `429`/`503`, if those responses are produced.
- [ ] Capture successful JSON and XML responses if XML support is implemented.
- [ ] Ensure OpenAPI documents the actual endpoint/status/error behaviour.
