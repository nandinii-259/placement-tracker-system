# Placement Tracker System — Services (Part 2: Business Logic Core)

## 1. Objective

Complete the Service layer by implementing the core business rules (BR1-BR10) and the application status workflow, building on the pattern established in Part 1 (CompanyService, JobService).

## 2. Services Added

```
src/main/java/com/placementtracker/placement_tracker_backend/service/
├── StudentService.java
├── ApplicationService.java
├── InterviewService.java
├── OfferService.java
└── UserService.java
```

## 3. ApplicationService — The State Machine

The application status workflow finalized in Phase 1 is implemented as a Java `Map`:

```java
private static final Map<Application.Status, Set<Application.Status>> VALID_TRANSITIONS = Map.of(
    Application.Status.APPLIED, Set.of(Application.Status.UNDER_REVIEW),
    Application.Status.UNDER_REVIEW, Set.of(Application.Status.SHORTLISTED, Application.Status.REJECTED),
    Application.Status.SHORTLISTED, Set.of(Application.Status.INTERVIEW_SCHEDULED, Application.Status.REJECTED),
    Application.Status.INTERVIEW_SCHEDULED, Set.of(Application.Status.SELECTED, Application.Status.REJECTED),
    Application.Status.SELECTED, Set.of(Application.Status.OFFERED)
);
```

Each key is a current status; its value is the set of statuses it may transition to. `OFFERED` is absent as a key, making it a true terminal state. This single structure is a direct, generic implementation of BR6 ("valid transitions only") — it is checked once, in `updateStatus()`, rather than hardcoded per status pair.

## 4. Business Rules Implemented

| Business Rule | Method | Implementation |
|---|---|---|
| BR1 - no duplicate applications | applyToJob() | Checks `applicationRepository.existsByStudentIdAndJobId()` before creating |
| BR2 - deadline check | applyToJob() | Compares `job.getApplicationDeadline()` against `LocalDate.now()` |
| BR3 - eligibility check | applyToJob() | Compares `student.getCgpa()` against `job.getMinCgpa()` using `BigDecimal.compareTo()` |
| BR6 - valid status transitions | updateStatus() | Looks up current status in `VALID_TRANSITIONS`; rejects if the requested status is not in the allowed set |
| BR7 - interview only for SHORTLISTED | InterviewService.scheduleInterview() | Checks `application.getStatus() == SHORTLISTED` before allowing |
| BR8 - offer only for SELECTED | OfferService.createOffer() | Checks `application.getStatus() == SELECTED` before allowing |
| BR10 - rejection reason required | updateStatus() | If transitioning to REJECTED from SHORTLISTED or INTERVIEW_SCHEDULED, throws if `rejectionReason` is null or blank |

## 5. Cross-Service Coordination

`InterviewService.scheduleInterview()` and `OfferService.createOffer()` both call `applicationService.updateStatus(...)` after successfully saving, automatically advancing the application to `INTERVIEW_SCHEDULED` or `OFFERED` respectively. This reuses the single state-machine implementation in `ApplicationService` rather than duplicating transition logic across services -- each service owns its own entity's business rules, but the shared status workflow lives in exactly one place.

## 6. UserService - Deliberately Minimal

`UserService` currently only supports lookups (`getUserByEmail`, `emailExists`, `getUserById`). Registration (`createUser`) is deliberately deferred to Phase 12, since it requires password hashing via Spring Security. Implementing it now would mean either storing plaintext passwords or building the logic twice; both are avoided by sequencing it correctly.

## 7. Verification

Application restarted successfully after adding all 5 new services. Startup log confirmed correct Dependency Injection across the full chain (ApplicationRepository + StudentService + JobService -> ApplicationService; InterviewRepository + ApplicationService -> InterviewService; OfferRepository + ApplicationService -> OfferService; UserRepository -> UserService), with no wiring or compilation errors.

## 8. Phase 10 Status: Complete

All planned services for the current MVP scope are implemented: CompanyService, JobService, StudentService, ApplicationService, InterviewService, OfferService, UserService (partial, pending Phase 12).