# Placement Tracker System — Authentication & Authorization (Part 1: Setup)

## 1. Objective

Add Spring Security to the project and observe its default behavior, before building custom authentication (password hashing, registration, JWT, role-based access).

## 2. Authentication vs Authorization

- **Authentication**: "Who are you?" -- verifying identity, done once at login.
- **Authorization**: "What are you allowed to do?" -- checked on every subsequent request, based on the authenticated identity's role.

## 3. Why Passwords Are Hashed, Not Stored Directly

Storing plaintext passwords means a single database breach exposes every user's real password immediately, with severe consequences given common password reuse across sites. Hashing is a one-way transformation: the original password can never be recovered from the stored hash. At login, the entered password is hashed the same way and compared against the stored hash -- if they match, the password was correct; the real password is never stored or compared directly.

**BCrypt** was chosen (built into Spring Security) specifically because it is deliberately slow, making brute-force attacks impractical -- unlike general-purpose hashes like MD5/SHA, which are fast and therefore unsuitable for passwords.

## 4. Dependency Added

`spring-boot-starter-security` was added to `pom.xml` now, in Phase 12 -- deliberately deferred since Phase 6, since adding it earlier would have locked down every endpoint before any of them existed or could be tested (Phases 9-11 required unauthenticated access for iterative development and testing).

## 5. Immediate Effect Observed

Simply adding the dependency, before writing any custom security configuration, caused Spring Security's auto-configuration to activate default protection:

- Console output on startup:
  ```
  Using generated security password: 00c410ec-c67a-402f-8c48-dca7cd8f5cc9
  ```
- All previously working, tested endpoints (e.g. `GET /api/companies`) now return `401 Unauthorized` when called without credentials.

This demonstrates that Spring Security's presence alone changes application behavior -- it defaults to "deny everything" as a safety net, requiring explicit configuration to open up any endpoint deliberately, rather than defaulting to open access.

## 6. Next Steps

- Implement password hashing via `BCryptPasswordEncoder`
- Complete `UserService` with real registration logic
- Build a custom `SecurityConfig` to replace the default lockdown with our own rules
- Implement JWT-based authentication
- Apply role-based access control to existing endpoints