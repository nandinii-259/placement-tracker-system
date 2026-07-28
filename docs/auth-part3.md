# Placement Tracker System — Authentication & Authorization (Part 3: Login & JWT Generation)

## 1. Objective

Implement JWT token generation and a working login endpoint, verified end-to-end in Postman.

## 2. JWT Library

Added the `jjwt` library (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`, version 0.12.6) to `pom.xml`, since JWT support is not built into Spring Boot itself.

## 3. JwtUtil

Located at `config/JwtUtil.java`, this component is responsible for creating and reading tokens:

- **generateToken(email, role)**: builds a signed JWT containing the user's email (as the subject), their role, an issued-at timestamp, and a 24-hour expiration, signed with a secret key.
- **extractEmail / extractRole**: read values back out of a token without needing a database lookup -- the token itself carries the proof of identity, consistent with the STATELESS session policy set in Part 2.
- **isTokenValid / isTokenExpired**: basic safety checks used later by the request-validation filter.

**Known simplification**: the secret signing key is currently hardcoded directly in the class. This is acceptable for local development and learning purposes, but in a production system this value would be externalized to an environment variable or secrets manager, never committed to source control. Documented here as a deliberate, known limitation rather than an oversight.

## 4. UserService.login()

```java
public String login(String email, String rawPassword) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessRuleException("Invalid email or password."));

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        throw new BusinessRuleException("Invalid email or password.");
    }

    return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
}
```

**Security detail**: an identical error message is used whether the email does not exist or the password is wrong. Using two different messages would let an attacker determine which emails are registered in the system (a technique known as user enumeration) -- using one generic message for both cases avoids leaking that information.

`passwordEncoder.matches(rawPassword, storedHash)` re-hashes the newly entered password using the same BCrypt algorithm and compares the resulting hashes -- the original password is never reversed or compared directly.

## 5. LoginRequestDto / LoginResponseDto

- **LoginRequestDto**: `email`, `password`, both required.
- **LoginResponseDto**: `token`, `email`, `role` -- everything the frontend needs after a successful login: the token for future authenticated requests, plus basic identity info to display immediately without an extra request.

## 6. AuthController

```
POST /api/auth/login -> authenticates a user and returns a JWT (200 OK) or a generic error (400)
```

## 7. Debugging Note

While pasting the login method into `AuthController`, it was accidentally nested inside the closing brace of the `register` method, producing invalid Java syntax. Identified by carefully reading the brace structure rather than guessing, and fixed by replacing the file with a correctly structured version where each method is a sibling within the class, not nested inside another method.

## 8. Verification (Postman)

- Valid login (correct email + password) -> 200 OK, returned a well-formed JWT (three dot-separated Base64 segments: header, payload, signature) plus correct email and role.
- Invalid login (correct email, wrong password) -> 400 Bad Request, "Invalid email or password."

## 9. Next Steps

- Build a JWT validation filter to check tokens on incoming requests
- Replace `anyRequest().permitAll()` in SecurityConfig with real role-based rules
- Re-secure all Phase 11 endpoints appropriately (e.g. ADMIN-only writes on Company/Job, student-owns-resource checks on Application/Interview/Offer)