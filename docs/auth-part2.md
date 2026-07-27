# Placement Tracker System — Authentication & Authorization (Part 2: Registration & Password Hashing)

## 1. Objective

Implement real user registration with proper BCrypt password hashing, and temporarily open the registration endpoint through a minimal custom SecurityConfig.

## 2. SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
```

- `@EnableWebSecurity` replaces Spring's automatic default lockdown with this custom configuration.
- CSRF protection disabled: relevant to cookie-based server-rendered sites, not applicable to a stateless JSON REST API, and would interfere with API calls.
- `SessionCreationPolicy.STATELESS`: no server-side sessions are created; every request must carry its own proof of identity (a JWT, added in a following part), matching the REST API architecture decided in Phase 2.
- `anyRequest().permitAll()` is a deliberate, temporary state -- all endpoints are currently open so registration and login can be built and tested before role-based restrictions are added.

## 3. UserService.registerStudent()

- Checks `existsByEmail` first, throwing `BusinessRuleException` if the email is already registered (real implementation of the uniqueness rule referenced in FR2).
- Hashes the password via `passwordEncoder.encode(rawPassword)` -- the raw password is never persisted.
- Creates and saves the `User` row first (role STUDENT), then creates and saves the linked `Student` row using the generated user ID -- implementing the two-linked-inserts registration pattern described conceptually back in Phase 3.

## 4. RegisterRequestDto

New validation annotations: `@Email` (validates email format) and `@Size(min = 8)` (minimum password length).

## 5. AuthController

```
POST /api/auth/register -> registers a new student account (201 Created)
```

Reuses `StudentResponseDto` from Phase 11 for the response, since it already represents exactly the desired output shape and never includes the password field.

## 6. Verification

- Registered a real account via Postman: 201 Created, correct response shape, no password in response.
- Queried the database directly: confirmed the stored `password` column contains a BCrypt hash (`$2a$10$...`), not the plaintext password -- proving hashing is genuinely applied, not just assumed from reading the code.

## 7. Next Steps

- Implement login endpoint
- Implement JWT token generation and validation
- Replace `anyRequest().permitAll()` with real role-based access rules
- Apply authentication/authorization to all existing Phase 11 endpoints