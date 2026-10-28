# CS543 Web Services — Assignment 5
## HTTP Methods & Headers

### Team Details

**Team ID:** 18

| Roll No. | Name |
|---|---|
| 20251651034 | Dave Dev Rupeshkumar |
| 20251651053 | Kuldeep Rout |
| 20251651102 | Vikash Kumar |
| 20251651029 | Arjun Bodana |

**Service used:** EMS User Service  
**Base URL:** `/api/users`

---

## A1. Method Map

| Action | HTTP Method | URL | Success status |
|---|---|---|---|
| Create a user | POST | `/api/users` | `201 Created` |
| List users | GET | `/api/users` | `200 OK` |
| Read one user | GET | `/api/users/{id}` | `200 OK` |
| Replace/update a user | PUT | `/api/users/{id}` | `200 OK` (when the update succeeds) |
| Delete a user | DELETE | `/api/users/{id}` | `204 No Content` (when implemented/configured to return no body) |

Non-CRUD actions should use a sub-resource with POST, for example `POST /api/users/{id}/deactivate`, rather than putting an action verb in the URL.

## A2. Safety and Idempotency

| Endpoint | Safe? | Idempotent? | Reason / retry plan |
|---|---|---|---|
| `GET /api/users` | Yes | Yes | Reads the collection and must not change server state. |
| `GET /api/users/{id}` | Yes | Yes | Reads one user; repeating the same request should not change the resource. |
| `POST /api/users` | No | Not inherently | Creates a resource. Use the same `Idempotency-Key` on retries so a repeated request returns the original user instead of creating a duplicate. |
| `PUT /api/users/{id}` | No | Yes, by HTTP semantics | Replaces the resource with the supplied representation; repeating the same replacement should have the same intended effect. `If-Match` protects against overwriting a newer version. |
| `DELETE /api/users/{id}` | No | Yes, by HTTP semantics | Repeating deletion has the same intended final state: the resource is absent. The response code may differ after the first deletion. |

GET endpoints must remain read-only. POST create is the endpoint that is neither safe nor inherently idempotent; the idempotency key makes retries safe for duplicate creation.

## A3. ETag, Conditional GET and Conditional Write

A GET of the created user returned:

```http
HTTP/1.1 200
Cache-Control: private, no-cache
ETag: "52972759"
Content-Type: application/json
```

The conditional GET should send the exact ETag, including quotation marks:

```http
GET /api/users/6ace6d050fe11a1a4185c58f HTTP/1.1
If-None-Match: "52972759"
```

When the representation has not changed, the expected response is `304 Not Modified` with no response body. This avoids retransmitting a representation the client already has. The successful `304` behavior was reported as working in Postman. In the supplied curl log, the equivalent request returned `200`; therefore the raw curl capture should be refreshed if literal `curl -v` proof is required.

A conditional update with a stale ETag was tested:

```http
PUT /api/users/6ace6d050fe11a1a4185c58f HTTP/1.1
If-Match: "stale-etag-value"
Content-Type: application/json
```

Actual response:

```http
HTTP/1.1 412
ETag: "52972759"
Content-Length: 0
```

`If-Match` prevents a client from overwriting a resource after it has changed since the client last read it.

## A4. 422 Validation Error vs 400 Malformed Request

**422 example — semantically invalid user representation**

Request body omitted the required `role` and `status` fields:

```http
POST /api/users HTTP/1.1
Content-Type: application/json
Idempotency-Key: assignment5-user-101

{"name":"Assignment Five User","email":"a5-user@example.com"}
```

Actual response:

```http
HTTP/1.1 422
Content-Type: application/problem+json;charset=UTF-8
```

The response reported that `role` and `status` were required. The JSON was parseable, but its fields failed application validation.

**400 example — malformed JSON**

```http
POST /api/users HTTP/1.1
Content-Type: application/json
Idempotency-Key: assignment5-user-bad-101

{"name":
```

Actual response:

```http
HTTP/1.1 400
Content-Type: application/problem+json;charset=UTF-8
```

The body could not be parsed as valid JSON. In short, `400` is for malformed request syntax; `422` is for a syntactically valid representation that fails validation.

## A5. CORS

If a browser page on another origin calls the API and the browser blocks access even though server logs show `200`, the browser's same-origin policy/CORS enforcement is responsible. The server must return an appropriate `Access-Control-Allow-Origin` header for the requesting origin (or `*` when appropriate for a non-credentialed request). For preflighted requests, it must also answer `OPTIONS` with the relevant `Access-Control-Allow-Methods` and `Access-Control-Allow-Headers`.

The captured EMS responses included `Vary: Origin`, but the supplied curl transcript alone does not demonstrate a complete successful cross-origin preflight. Verify the actual `OPTIONS` response before claiming that the full CORS flow has been tested.

## A6. Cache-Control

- **Single-user GET:** the captured response used `Cache-Control: private, no-cache`. This allows a private client cache to store the representation but requires revalidation before reuse; the ETag enables conditional revalidation.
- **Create/update/error responses:** the captured responses used `Cache-Control: no-cache, no-store, max-age=0, must-revalidate`. `no-store` is appropriate for sensitive or transient responses that should not be retained by caches.

The exact policy should be selected per response type; user-specific data should not be shared through a public cache.

## A7. When POST Is Better Than GET for Search

GET is appropriate for ordinary searches because it is safe, can be cached, and its query URL can be bookmarked or shared. POST can be justified when search criteria are large, structured/nested, or sensitive enough that putting them in a URL is undesirable. Switching to POST gives up the normal safe-method semantics and much of GET's straightforward cacheability, bookmarking, and shareability. A POST search must still avoid changing server state if it is only a search operation.

## A8. Meaning of Location

- **On `201 Created`:** `Location` identifies the newly created resource. The captured create response returned `Location: http://localhost:8080/api/users/6ace6d050fe11a1a4185c58f`.
- **On a `3xx` redirect:** `Location` identifies the redirect target the client should request next.

---

## D2. Headers Table

The table summarizes the relevant request and response headers for the User Service. Headers listed as implementation targets should be checked against the current running service before submission.

| Endpoint / case | Request headers | Important response headers |
|---|---|---|
| `POST /api/users` | `Authorization: Bearer <token>`, `Content-Type: application/json`, `Idempotency-Key` | `Location` on create, `Content-Type: application/json`, rate-limit headers |
| Repeated `POST /api/users` | Same body and same `Idempotency-Key` | `Idempotency-Replayed: true` on the observed replay |
| `GET /api/users` | `Authorization: Bearer <token>`, optional `Accept: application/json` | `Content-Type`, rate-limit headers; check cache policy |
| `GET /api/users/{id}` | `Authorization: Bearer <token>`, optional `If-None-Match` | `ETag`, `Cache-Control: private, no-cache`, `Content-Type` |
| `PUT /api/users/{id}` | `Authorization: Bearer <token>`, `Content-Type: application/json`, `If-Match` | `ETag` on the observed `412`; successful update response should be checked |
| `DELETE /api/users/{id}` | `Authorization: Bearer <token>` | Expected `204 No Content` if the endpoint is implemented with no response body |
| Malformed JSON | `Content-Type: application/json`, `Authorization` | Observed `400`, `Content-Type: application/problem+json;charset=UTF-8` |
| Validation failure | `Content-Type: application/json`, `Authorization` | Observed `422`, `Content-Type: application/problem+json;charset=UTF-8` |
| Missing user | `Authorization: Bearer <token>` | Observed `404`, `Content-Type: application/problem+json;charset=UTF-8` |
| Missing authorization | No `Authorization` header | Expected `401 Unauthorized`; capture the response to verify |
| `OPTIONS` preflight | `Origin`, `Access-Control-Request-Method`, `Access-Control-Request-Headers` | Check `Access-Control-Allow-Origin`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers` |
| Conditional GET | `If-None-Match: <current ETag>` | `304 Not Modified` with no body when the ETag matches |
| Conditional update | `If-Match: <stale ETag>` | Observed `412 Precondition Failed`; current `ETag` returned |

## C4. Safe-Retry Plan

| Risk / operation | Mechanism | Why |
|---|---|---|
| Retrying user creation after a timeout | Reuse the same `Idempotency-Key` and identical request body | Returns the original result and avoids duplicate users. |
| Re-reading a user | `ETag` + `If-None-Match` | A matching representation can return `304` without retransmitting the body. |
| Updating a user after another editor may have changed it | `ETag` + `If-Match` | A stale validator returns `412` instead of silently overwriting newer data. |
| Repeating a GET list/read | No extra retry token needed | GET is safe and idempotent, provided the endpoint has no hidden side effects. |
| Retrying PUT or DELETE | Retry the same intended operation; use `If-Match` for guarded updates | PUT/DELETE are idempotent by intended effect, but preconditions help prevent lost updates. |

---

## Observed Response Summary

| Scenario | Observed result |
|---|---|
| User creation with valid `name`, `email`, `role`, `status` | `201 Created`, with `Location` |
| Same POST and same idempotency key | `200 OK`, same user ID, `Idempotency-Replayed: true` |
| GET existing user | `200 OK`, `ETag: "52972759"`, `Cache-Control: private, no-cache` |
| Conditional GET in supplied curl output | `200 OK`; Postman `304` was reported separately |
| PUT with stale `If-Match` | `412 Precondition Failed` |
| Malformed JSON | `400 Bad Request` |
| Missing user | `404 Not Found` |
| Missing Authorization header | `401` is required by the assignment; capture the response to verify |

