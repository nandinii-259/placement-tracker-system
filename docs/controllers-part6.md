# Placement Tracker System — Controllers (Part 6: Student) — Phase 11 Complete

## 1. Objective

Build StudentController to support FR4 (view/update own profile), completing all planned Controllers for Phase 11.

## 2. DTOs

- **StudentResponseDto**: includes `email` (navigated from the related `User` entity via `student.getUser().getEmail()`) but deliberately never includes `password` -- the concrete, real implementation of the DTO security principle discussed at the start of this phase.
- **StudentUpdateRequestDto**: allows updating `fullName`, `branch`, and `cgpa` only. `graduationYear` and `email` are deliberately excluded from self-service updates, treated as more fixed/administrative fields for this MVP.

## 3. Service Layer Addition

Added `updateStudentProfile()` to `StudentService`, following the same fetch-then-update pattern used in `CompanyService`/`JobService` since Phase 10.

## 4. StudentController Endpoints

```
GET /api/students/{id}  -> get a student's profile
PUT /api/students/{id}  -> update a student's profile (fullName, branch, cgpa)
```

## 5. Testing Performed

- `GET /api/students/1` -> 200 OK, confirmed response includes email but never password
- `PUT /api/students/1` with updated fullName/branch/cgpa -> 200 OK, values correctly updated

## 6. Phase 11 — Complete

All 6 planned controllers built and tested:

| Controller | Endpoints | Status |
|---|---|---|
| CompanyController | Full CRUD | Tested |
| JobController | Full CRUD, company-nested | Tested |
| ApplicationController | Apply, view, status update | Tested (BR1, BR6, BR10 verified) |
| InterviewController | Schedule, view | Tested (BR7 verified) |
| OfferController | Create, view | Tested (BR8 verified) |
| StudentController | View, update profile | Tested |

Combined with `GlobalExceptionHandler`, every endpoint returns consistent, correctly-coded JSON responses (200/201/204 for success, 400/404/500 for errors), and the complete placement workflow (APPLIED through OFFERED) has been independently verified end-to-end via Postman.

Registration and login endpoints are deliberately deferred to Phase 12, since they require Spring Security and password hashing, which do not yet exist.