# CS543 Web Services — Assignment 4 Notes

**Team ID:** 18  
**Service:** EMS Quiz Service  
**Repository:** https://github.com/devdave17/EMS

| Roll Number | Name |
|---|---|
| 20251651034 | Dave Dev Rupeshkumar |
| 20251651053 | Kuldeep Rout |
| 20251651102 | Vikash Kumar |
| 20251651029 | Arjun Bodana |

## A4. Resource Table

| Method | URL | What it does | Success code | Failure codes |
|---|---|---|---|---|
| `POST` | `/api/quizzes` | Creates a quiz. Requires an `Idempotency-Key` header. | `201 Created`; `200 OK` for an idempotent replay | `400`, `409`, `422`, `500` |
| `GET` | `/api/quizzes/{id}` | Retrieves a quiz by ID. | `200 OK` | `404`, `500` |
| `GET` | `/api/quizzes?status={status}` | Lists quizzes, optionally filtered by status. | `200 OK` | `400`, `500` |
| `PUT` | `/api/quizzes/{id}/status` | Changes a quiz's status through the status sub-resource. | `200 OK` | `400`, `404`, `409`, `500` |

## A5. Hard Resource-Mapping Choice

Changing a quiz's status was the operation that mapped least naturally to a standard CRUD endpoint. I represented it as `PUT /api/quizzes/{id}/status`, because the status is a state sub-resource of a quiz and the operation updates that state. I rejected a verb-based URL such as `/api/quizzes/{id}/publish` because it would expose the action in the URL instead of identifying the resource. The service allows `DRAFT → PUBLISHED` and `PUBLISHED → CLOSED`; invalid transitions return a conflict.

## D3. Fallback Reasoning

The instructor permitted us to skip Assignment 4 Part D's outbound HTTP call, so the current Quiz Service does not rely on that external call and has no runtime fallback implemented. If a future version requires an external dependency for quiz creation, it should fail with `503 Service Unavailable` when that dependency cannot be reached rather than report success for work that did not complete.

## 1. WSDL and OpenAPI Line Counts

- **Assignment 3 WSDL:** 93 lines.
- **EMS `openapi.yaml`:** 533 lines.
- **Difference:** 440 lines; the OpenAPI file is longer.

The difference comes from the scope of the files: the EMS OpenAPI document describes authentication, user management, quiz management, reusable schemas, parameters, and responses, while the Assignment 3 WSDL describes its SOAP service contract. Two WSDL-specific declarations that OpenAPI does not need are SOAP binding details (including SOAP transport/SOAPAction) and WSDL message/port-type declarations for SOAP input and output messages.

## 2. SOAP Fault and REST Error Response

An Assignment 3 SOAP fault example was:

```xml
<soap:Fault>
    <faultcode>soap:Client</faultcode>
    <faultstring>Payment was declined</faultstring>
    <detail>
        <pay:ChargeFault>
            <pay:code>card_declined</pay:code>
            <pay:message>The payment could not be completed.</pay:message>
        </pay:ChargeFault>
    </detail>
</soap:Fault>
```

In the Quiz Service, an invalid quiz status transition is represented by **HTTP `409 Conflict`** and the service's Problem Details response shape:

```http
HTTP/1.1 409 Conflict
Content-Type: application/problem+json;charset=UTF-8
```

```json
{
  "type": "illegal-transition",
  "title": "Conflict",
  "status": 409,
  "detail": "Invalid quiz status transition: DRAFT -> CLOSED"
}
```

The detail above illustrates the invalid `DRAFT → CLOSED` transition handled by the current service. Returning an application failure inside `200 OK` is misleading because clients, gateways, and monitoring systems commonly use the HTTP status code to distinguish success from failure. A `200` response can cause an intermediary or client to treat the failed operation as successful.

## 3. UDDI: Publish, Find, Bind

**Publish** remains as publishing the API contract in the Git repository. **Find** is performed by locating the repository and its `openapi.yaml` documentation. The separate UDDI **bind** step is no longer used; a client reads the OpenAPI contract and sends HTTP requests to the documented service URL. The repository and OpenAPI document replace the practical contract-publication and discovery role of a UDDI registry for this project.

## 4. Request Validation

The request-validation responsibility is carried by Bean Validation on the `Quiz` model and the `@Valid` annotation on the `createQuiz` controller method. The model uses `@NotBlank` for the quiz title and `@Size` limits for the title and description. Without these checks, a request with a blank title or a title/description exceeding the allowed length could be accepted and stored.

## 5. Where I Would Still Choose SOAP

I would choose SOAP for a payment-partner integration that requires a formal WSDL/XSD contract and structured SOAP faults. The specific guarantee sought would be a machine-readable contract for message structure and operation/fault definitions; any additional security or reliable-messaging guarantee would require the relevant WS-* standards to be configured, because SOAP alone does not provide those guarantees automatically.
