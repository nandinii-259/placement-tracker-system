# Placement Tracker System — Controllers (Part 4: Interview)

## 1. Objective

Build InterviewController, verify BR7 (interview only for SHORTLISTED applications), and confirm the cross-service status coordination built in Phase 10 works correctly through real HTTP calls.

## 2. DTOs

- **InterviewRequestDto**: `scheduledAt` (required, must be in the future via `@Future`), `mode` (required enum), `locationOrLink` (optional).
- **InterviewResponseDto**: flattens data from four levels of entity relationships -- `interview -> application -> student -> fullName` and `interview -> application -> job -> title` -- into simple fields, made possible entirely by the `@ManyToOne`/`@OneToOne` mappings from Phase 8.

## 3. InterviewController Endpoints

```
POST /api/interviews/application/{applicationId}  -> schedule an interview (201; enforces BR7)
GET  /api/interviews/application/{applicationId}   -> get the interview for an application
```

## 4. End-to-End Verification (Postman)

| Test | Result |
|---|---|
| Schedule interview for a REJECTED application | 400 Bad Request: "An interview can only be scheduled for a SHORTLISTED application." (BR7 confirmed) |
| Created a second job and application, moved it to SHORTLISTED, then scheduled an interview | 201 Created |
| Checked the application afterward (GET) | status automatically showed INTERVIEW_SCHEDULED, without any direct call to the status-update endpoint |

The final test confirms the cross-service design from Phase 10: `InterviewService.scheduleInterview()` internally calls `ApplicationService.updateStatus()`, so the status transition logic lives in exactly one place, reused rather than duplicated. This was verified end-to-end, not just by reading the code.

## 5. Phase 11 Progress

Completed: exception handling, CompanyController, JobController, ApplicationController, InterviewController.
Remaining: OfferController, StudentController.