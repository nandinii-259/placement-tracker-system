# Placement Tracker System — Controllers (Part 1: Exception Handling & Company)

## 1. Objective

Build the Controller layer, starting with a proper exception-handling foundation and the first working, tested endpoint (Company), before expanding to the remaining modules in later sessions.

## 2. Why DTOs Instead of Exposing Entities Directly

Entities are never returned or accepted directly through the API, for three reasons:
- **Security/privacy**: entities like `User` contain sensitive fields (e.g. password) that must never appear in API responses.
- **Over-exposure of structure**: entities have JPA relationships (e.g. `Student.user`) that could cause unpredictable, deeply nested JSON if serialized directly.
- **Decoupling**: DTOs let the database schema evolve independently of the public API contract.

Two DTOs per resource:
- **RequestDto**: the exact shape accepted from the client, annotated with Jakarta Validation constraints (`@NotBlank`, `@Size`).
- **ResponseDto**: the exact shape returned to the client, immutable (getters only, set via constructor).

## 3. Exception Handling Architecture

```
exception/
├── ResourceNotFoundException.java   (extends RuntimeException)
├── BusinessRuleException.java       (extends RuntimeException)
└── GlobalExceptionHandler.java      (@RestControllerAdvice)
```

All 7 Service classes were updated to throw these specific exceptions instead of the generic `RuntimeException`/`IllegalStateException`/`IllegalArgumentException` used as placeholders in Phase 10.

`GlobalExceptionHandler`, annotated `@RestControllerAdvice`, centrally intercepts exceptions thrown by any Controller and converts them into a consistent JSON error shape (`timestamp`, `status`, `error`, `message`):

| Exception | HTTP Status |
|---|---|
| ResourceNotFoundException | 404 Not Found |
| BusinessRuleException | 400 Bad Request |
| MethodArgumentNotValidException (thrown by @Valid failures) | 400 Bad Request, with a list of all failed field messages |
| Exception (catch-all) | 500 Internal Server Error, with a generic safe message (no internal details leaked) |

## 4. Debugging Note: Validation Initially Returned 500, Not 400

On first test, an empty `name` field returned `500 Internal Server Error` instead of the expected `400`. Root cause: `@Valid` failures throw `MethodArgumentNotValidException`, a distinct exception type that had no specific handler and was falling through to the generic catch-all. Fixed by adding a dedicated `@ExceptionHandler(MethodArgumentNotValidException.class)` that extracts all field error messages and returns them with a proper 400 status.

## 5. CompanyController

```
GET    /api/companies       -> list all companies
GET    /api/companies/{id}  -> get one company (404 if not found)
POST   /api/companies       -> create a company (201, validated via @Valid)
PUT    /api/companies/{id}  -> update a company
DELETE /api/companies/{id}  -> delete a company (204 No Content)
```

Controller methods stay thin: extract input, call exactly one Service method, convert Entity to ResponseDto, return. No business logic in the Controller layer, consistent with the layered architecture from Phase 2.

## 6. Postman Testing Performed

- `GET /api/companies` before any data existed -> 200 OK, `[]`
- `POST /api/companies` with valid data -> 201 Created, full company object with auto-generated `id` and `createdAt`
- `GET /api/companies` after creation -> 200 OK, array containing the created company
- `POST /api/companies` with blank `name` -> 400 Bad Request with message `["Company name is required"]` (after the MethodArgumentNotValidException fix)

All tests confirmed the full request path working end-to-end: Postman -> Controller -> Service -> Repository -> MySQL -> back through DTO conversion -> Postman.

## 7. Remaining Work for Phase 11

Controllers and DTOs for Job, Application, Interview, Offer, and Student/User (auth-related endpoints deferred to Phase 12) to be built in following sessions.