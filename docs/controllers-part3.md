# Placement Tracker System — Controllers (Part 3: Application)

## 1. Objective

Build ApplicationController, exposing the core business rule engine (BR1, BR2, BR3, BR6, BR10) and the status workflow to real HTTP requests, and verify every rule end-to-end via Postman.

## 2. DTOs

- **ApplicationRequestDto**: minimal -- only `jobId`. The applying student's ID is passed as a path variable for now (`/student/{studentId}`), since no authentication exists yet to derive it from a logged-in session. This will be revisited in Phase 12.
- **StatusUpdateRequestDto**: `status` (required) and `rejectionReason` (optional, conditionally required per BR10, validated in the Service layer).
- **ApplicationResponseDto**: flattens three related entities (student, job, company) into simple fields (`studentName`, `jobTitle`, `companyName`) rather than nesting full entity objects.

## 3. ApplicationController Endpoints

```
POST  /api/applications/student/{studentId}  -> apply to a job (201; enforces BR1, BR2, BR3)
GET   /api/applications/{id}                  -> get one application
GET   /api/applications/student/{studentId}   -> list a student's applications
GET   /api/applications/job/{jobId}            -> list applications for a job
PATCH /api/applications/{id}/status            -> update status (enforces BR6, BR10)
```

`PATCH` (not `PUT`) was used for the status endpoint, since it represents a partial update of one aspect of the resource (its status), not a full replacement -- correct REST semantics.

The Controller performs no business-rule checks itself; it calls `applicationService.applyToJob()` / `updateStatus()` directly and lets any `BusinessRuleException` propagate to `GlobalExceptionHandler`, which converts it into a proper 400 response automatically.

## 4. Test Data Setup

Since no registration endpoint exists yet (deferred to Phase 12), a test student was inserted directly via MySQL Workbench:
```sql
INSERT INTO users (email, password, role) VALUES ('teststudent@example.com', 'placeholder', 'STUDENT');
INSERT INTO students (user_id, full_name, branch, cgpa, graduation_year)
VALUES (LAST_INSERT_ID(), 'Test Student', 'CSE', 8.50, 2026);
```

## 5. End-to-End Business Rule Verification (Postman)

| Test | Request | Result |
|---|---|---|
| Apply to a job | POST /api/applications/student/1, jobId 1 | 201 Created, status APPLIED |
| BR1 -- duplicate application | Same request repeated | 400 Bad Request, "You have already applied to this job." |
| Valid transition | PATCH status -> UNDER_REVIEW | 200 OK |
| BR6 -- invalid transition | PATCH status -> OFFERED (skipping steps) | 400 Bad Request |
| Valid transition | PATCH status -> SHORTLISTED | 200 OK |
| BR10 -- rejection without reason | PATCH status -> REJECTED, no reason | 400 Bad Request |
| BR10 -- rejection with reason | PATCH status -> REJECTED, with reason | 200 OK, rejectionReason populated |

All business rules defined in Phase 1 and implemented in Phase 10 (ApplicationService) were independently confirmed working through real HTTP requests, not just unit-level reasoning -- the full stack (Controller -> Service -> Repository -> MySQL -> GlobalExceptionHandler -> JSON response) is verified end-to-end.

## 6. Phase 11 Progress

Completed: exception handling, CompanyController, JobController, ApplicationController (fully tested).
Remaining: InterviewController, OfferController, StudentController.