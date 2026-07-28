# Placement Tracker System — Authentication & Authorization (Part 5: Role-Based Access Control)

## 1. Objective

Replace the temporary `anyRequest().permitAll()` configuration with real, enforced role-based authorization rules, and verify them end-to-end using both an ADMIN and a STUDENT account.

## 2. Test Accounts

An admin account was created by first registering normally through `/api/auth/register` (to generate a real BCrypt hash), then promoting the role directly via SQL:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin2@placementtracker.com';
```
This mirrors the deliberate design decision from Phase 1 (BR9): admin accounts are never created through the public registration endpoint.

## 3. Final SecurityConfig Authorization Rules

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()

    .requestMatchers(HttpMethod.POST, "/api/companies").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasRole("ADMIN")

    .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasRole("ADMIN")

    .requestMatchers(HttpMethod.PATCH, "/api/applications/**").hasRole("ADMIN")
    .requestMatchers("/api/interviews/**").hasRole("ADMIN")
    .requestMatchers("/api/offers/**").hasRole("ADMIN")

    .requestMatchers(HttpMethod.GET, "/api/companies/**").authenticated()
    .requestMatchers(HttpMethod.GET, "/api/jobs/**").authenticated()
    .requestMatchers("/api/applications/**").authenticated()
    .requestMatchers("/api/students/**").authenticated()

    .anyRequest().authenticated()
)
```

- Creating, updating, deleting companies and jobs -> ADMIN only (implements BR4).
- Scheduling interviews and creating offers -> ADMIN only.
- Updating application status -> ADMIN only.
- Reading companies/jobs, and application/student endpoints in general -> any authenticated user (student or admin).
- Any unlisted endpoint defaults to requiring authentication, never fully open -- a deliberate safety net.

**Known limitation, tracked for future work**: true per-student ownership checks (BR5 -- "students can only access their own applications, interviews, and offers") are not yet implemented. Currently, any authenticated student can view any student's application data via ID, since the authorization rule only checks role, not resource ownership. Enforcing this requires comparing the authenticated user's identity (available via `SecurityContextHolder`) against the owning student ID inside the Service layer -- planned as a following step in this phase.

## 4. Debugging Note: Wrong Type Used for HTTP Method Matching

Initial code used `RequestMethod` (from `org.springframework.web.bind.annotation`, used for `@RequestMapping` annotations) instead of `HttpMethod` (from `org.springframework.http`, the type actually expected by `requestMatchers()`). This produced a compile error: "cannot be converted to HttpMethod." A partial find-and-replace initially missed some occurrences, still causing a build failure; resolved by replacing the entire file with a verified, complete, correct version rather than continuing to patch it piecemeal.

**Lesson**: two Spring classes with similar names and purposes but different, non-interchangeable types is a realistic and common source of confusion; verifying with a full file review after a bulk find-and-replace avoided a partial-fix loop.

## 5. End-to-End Verification (Postman)

| Test | Token Used | Result |
|---|---|---|
| POST /api/companies | None | 403 Forbidden |
| POST /api/companies | Valid STUDENT token | 403 Forbidden (authenticated, but wrong role) |
| POST /api/companies | Valid ADMIN token | 201 Created |

This three-way test is the strongest form of evidence for role-based access control: it confirms the system distinguishes between "not authenticated at all" and "authenticated but not authorized" -- both correctly resulting in 403, but for genuinely different reasons -- and correctly allows the action only for the appropriate role.

## 6. Next Steps

- Implement per-resource ownership checks (BR5) for student-facing endpoints
- Final full re-test of all Phase 11 endpoints under the new security rules