# Placement Tracker System — Controllers (Part 5: Offer)

## 1. Objective

Build OfferController, verify BR8 (offer only for SELECTED applications), and confirm the complete end-to-end placement workflow works correctly through real HTTP requests.

## 2. DTOs

- **OfferRequestDto**: `positionTitle` (required), `salaryCtc` (optional, no `@NotNull`, matching the Phase 3/8 design decision that this field may be unset if the exact figure isn't finalized yet).
- **OfferResponseDto**: flattens student name, company name, position, salary, and offer date into a flat structure.

## 3. OfferController Endpoints

```
POST /api/offers/application/{applicationId}  -> create an offer (201; enforces BR8)
GET  /api/offers/application/{applicationId}   -> get the offer for an application
```

## 4. End-to-End Verification (Postman)

| Test | Result |
|---|---|
| Create offer while application is INTERVIEW_SCHEDULED | 400 Bad Request (BR8 confirmed) |
| Moved application to SELECTED, then created offer | 201 Created |
| Checked application status afterward | automatically advanced to OFFERED (cross-service coordination confirmed again) |

## 5. Full Workflow Verified End-to-End

Across ApplicationController, InterviewController, and OfferController testing, the complete status workflow was exercised and confirmed correct in a single continuous test:

```
APPLIED -> UNDER_REVIEW -> SHORTLISTED -> INTERVIEW_SCHEDULED -> SELECTED -> OFFERED
```

Every transition succeeded when valid and was rejected when invalid, matching the state machine defined in ApplicationService (Phase 10) exactly. All of BR1, BR6, BR7, BR8, and BR10 were independently verified via real HTTP requests, not just code inspection -- this is the strongest form of evidence that the system behaves as designed.

## 6. Phase 11 Progress

Completed: exception handling, CompanyController, JobController, ApplicationController, InterviewController, OfferController -- all core business modules fully tested.
Remaining: StudentController (profile view/update; registration deferred to Phase 12).