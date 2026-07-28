# Placement Tracker System — Authentication & Authorization (Part 6: Resource Ownership / BR5) — Phase 12 Complete

## 1. Objective

Implement and verify BR5 -- students can only access their own applications, interviews, and offers -- completing Phase 12.

## 2. Implementation

Added `findByUserEmail` to `StudentRepository` (a derived query reaching through the `Student -> User` relationship, translated by Spring Data JPA into a SQL JOIN), and `getStudentByEmail` to `StudentService`.

In `ApplicationController`, added two helper methods:

```java
private void verifyStudentOwnership(Long requestedStudentId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String loggedInEmail = auth.getName();
    Student loggedInStudent = studentService.getStudentByEmail(loggedInEmail);

    if (!loggedInStudent.getId().equals(requestedStudentId)) {
        throw new BusinessRuleException("You are not allowed to access another student's data.");
    }
}

private boolean isAdmin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}
```

`SecurityContextHolder.getContext().getAuthentication()` retrieves the identity that `JwtAuthFilter` (Part 4) populated earlier in the request. The logged-in user's email is used to look up their real `Student` record, and its ID is compared against the student ID being requested in the URL.

Applied to:
- `applyToJob`: a student can only apply as themselves.
- `getApplicationById` / `getApplicationsByStudent`: ownership is enforced for students, but skipped for admins via `isAdmin()`, since admins are permitted to view any student's data.
- `getApplicationsByJob` / `updateStatus`: no additional check needed, since these are already restricted to ADMIN at the SecurityConfig level.

## 3. Debugging Note

An initial test unexpectedly returned `200 OK` for a cross-student access attempt that should have been blocked. Root cause: the running application instance had not been restarted after the `ApplicationController` changes were saved, so it was still executing the old code. Resolved by fully stopping and restarting the application before retesting.

**Lesson**: after any code change, especially to security-relevant logic, restart and confirm a fresh startup timestamp before trusting test results -- a stale running instance can silently mask whether new code is actually in effect.

## 4. Verification (Postman)

Test data (verified via SQL): student id 1 (teststudent@example.com), student id 4 (priya@example.com).

| Test | Result |
|---|---|
| Priya requests GET /api/applications/student/1 (not her own) | 400 Bad Request, "You are not allowed to access another student's data." |
| Priya requests GET /api/applications/student/4 (her own) | 200 OK |

Both directions of BR5 confirmed: access to one's own data succeeds, access to another student's data is blocked.

## 5. Phase 12 — Complete

| Part | Deliverable | Status |
|---|---|---|
| 1 | Spring Security added, default lockdown observed | Done |
| 2 | Password hashing (BCrypt), registration | Done, verified in database |
| 3 | JWT generation, login endpoint | Done, verified via decoded token |
| 4 | JWT validation filter | Done, verified via diagnostic endpoint |
| 5 | Role-based access control (BR4) | Done, verified three-way (no token / wrong role / correct role) |
| 6 | Resource ownership checks (BR5) | Done, verified both directions |

The application now has genuine, tested authentication and authorization: hashed passwords, stateless JWT-based identity, role-based endpoint restrictions, and per-resource ownership enforcement for students.

## 6. Known Simplifications (documented, not hidden)

- JWT secret key is hardcoded in `JwtUtil` rather than externalized to an environment variable -- acceptable for local development, flagged as a production concern.
- Ownership checks were implemented directly in `ApplicationController` rather than as a reusable, centralized mechanism (e.g. a custom Spring Security expression) -- acceptable for the current MVP scope with one primary ownership relationship, but would be worth revisiting if more resource types needed similar checks.