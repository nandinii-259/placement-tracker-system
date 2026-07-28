# Placement Tracker System — Authentication & Authorization (Part 4: JWT Validation Filter)

## 1. Objective

Build and verify a filter that validates JWT tokens on incoming requests, confirming the complete authentication chain works end-to-end before applying real access restrictions.

## 2. JwtAuthFilter

Located at `config/JwtAuthFilter.java`, extending `OncePerRequestFilter` -- a Spring Security base class guaranteeing this filter runs exactly once per incoming request, before it reaches any Controller.

Behavior:
- Reads the `Authorization` header; if missing or not prefixed with `"Bearer "`, the request continues unauthenticated (to be rejected later only if the endpoint actually requires authentication).
- Extracts and validates the token via `JwtUtil`.
- On success, populates Spring Security's `SecurityContextHolder` with an authenticated principal (the user's email) and their authority, formatted as `ROLE_<ROLE>` (e.g. `ROLE_STUDENT`) -- following Spring Security's required naming convention for role-based checks.
- Wrapped in try/catch: an invalid or expired token is silently ignored rather than crashing the request, leaving it unauthenticated so Spring Security can reject it downstream if needed.

## 3. SecurityConfig Update

`JwtAuthFilter` is registered via:
```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```
ensuring it runs before Spring Security's own built-in authentication filter, early enough in the request pipeline to inform all later authorization decisions.

`anyRequest().permitAll()` was deliberately kept in place during this step -- the goal here was to confirm the filter recognizes valid tokens correctly before restricting any endpoint, an incremental verification approach rather than changing security rules and access logic at the same time.

## 4. Verification Method

A temporary `/api/auth/whoami` endpoint was added, reporting the current Spring Security authentication state directly. This provided a direct, observable way to confirm the filter's behavior rather than inferring it indirectly from a Controller's business logic.

- Called with no Authorization header -> "Not authenticated"
- Called with a valid Bearer token (obtained by logging in via `/api/auth/login`) -> "Authenticated as: priya@example.com, authorities: [ROLE_STUDENT]"

This confirmed the complete chain: login -> password verification -> JWT generation -> token sent on a new request -> filter validation -> Spring Security recognizing the authenticated identity and role.

The temporary endpoint was removed after verification, since it existed only as a diagnostic tool, not a permanent part of the API.

## 5. Next Steps

- Replace `anyRequest().permitAll()` with real role-based authorization rules
- Apply `hasRole("ADMIN")` / `hasRole("STUDENT")` restrictions to existing endpoints per the business rules defined in Phase 1 (BR4, BR5)
- Re-test all Phase 11 endpoints with real authentication enforced