# Placement Tracker System — Frontend (Part 2: Student Dashboard & Jobs Page)

## 1. Objective

Build the Student Dashboard and Jobs browsing page, establishing the pattern for authenticated API requests from the frontend.

## 2. Shared Authentication Helper (auth.js)

Created `js/auth.js`, providing reusable functions used across every logged-in page:
- `getToken()` / `getEmail()` / `getRole()`: read stored identity from `localStorage`
- `requireLogin()`: redirects to `login.html` if no token is present -- a frontend UX convenience, not a security boundary (the backend's role/ownership checks from Phase 12 remain the actual enforcement)
- `logout()`: clears `localStorage` and returns to the login page
- `authFetch(url, options)`: wraps `fetch()`, automatically attaching the `Authorization: Bearer <token>` header to every call -- avoiding repeated header-setting code across every page

## 3. Backend Addition Required

Added `GET /api/students/by-email?email=...` to `StudentController`, using `@RequestParam` (reading a query parameter) rather than `@PathVariable`. This was necessary because after login, the frontend only knows the user's email (from the JWT), not their numeric student ID -- this endpoint bridges that gap, reusing `StudentService.getStudentByEmail()` already built for Phase 12's ownership checks.

## 4. Student Dashboard

- Displays the logged-in student's real name, branch, and CGPA, fetched via `/api/students/by-email`
- Displays live counts of applications, interviews scheduled, and offers received, derived by fetching the student's applications and filtering by status client-side
- Shared navbar pattern (Dashboard / Browse Jobs / My Applications / Logout) established here, reused on subsequent pages

## 5. Jobs Page

- Fetches all jobs via `/api/jobs` and dynamically builds one card per job using template literals and `innerHTML`
- Computes eligibility client-side (`studentCgpa >= job.minCgpa`) and deadline status, displaying an Eligible/Not Eligible badge and disabling the Apply button accordingly -- a UI convenience only; the backend's BR2/BR3 checks (Phase 10) remain the actual, unbypassable enforcement, consistent with the "frontend restrictions are never sufficient alone" principle from Phase 2
- Clicking "Apply" calls `POST /api/applications/student/{id}` via `authFetch`, displaying the backend's success or error message directly

## 6. Debugging Notes

**Stale backend instance**: after adding the `/by-email` endpoint, an initial test returned a `500 Internal Server Error` with no corresponding log entry in the running console -- despite `netstat` showing no process on port 8080. A clean stop-and-restart of the backend resolved the issue immediately. Lesson: when backend behavior seems inconsistent with the code being reviewed, a full restart is often the fastest reliable diagnostic step, especially after a sequence of edits and partial runs.

**CSS reuse across page types**: after changing the shared `body` rule to support the dashboard's top-down layout, the login/register pages lost their centered-box appearance, since all pages share one `style.css`. Resolved by introducing a `.centered-page` class applied only to login/register, rather than relying on a single global `body` style to serve visually different page types.

## 7. Testing Performed

- Verified the dashboard displays real, correct profile data and accurate stat counts
- Verified the Jobs page displays real jobs with correct eligibility badges
- Verified applying to a job succeeds and displays a confirmation message with the correct job title
- Verified BR1 is enforced from the UI: attempting to re-apply to an already-applied job correctly displays "You have already applied to this job." via the browser's Network tab, confirming the backend rule holds even when triggered through the frontend rather than Postman

## 8. Known Follow-Up (Not Blocking)

A planned UX enhancement -- turning the Apply button green with "Applied" text immediately after a successful application -- was implemented in code but not successfully verified in this session, since available fresh (not-yet-applied) jobs were limited during testing. The underlying application logic is confirmed working; only the visual confirmation of this specific enhancement remains to be verified in a future session.

## 9. Next Steps (Phase 13 Part 3)

- Build the My Applications page, showing a student's applications alongside any related interview and offer details