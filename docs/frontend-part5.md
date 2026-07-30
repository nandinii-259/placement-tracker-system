# Placement Tracker System — Frontend (Part 5: Manage Jobs & Manage Applications) — Phase 13 Complete

## 1. Objective

Build the final two admin pages -- Manage Jobs and Manage Applications -- completing the entire frontend and the full placement workflow from posting a job through generating an offer.

## 2. Manage Jobs Page

- Company dropdown populated dynamically from `GET /api/companies`, used to select which company a new job belongs to
- Post New Job form maps directly to `POST /api/jobs/company/{companyId}`
- Lists all jobs with a Delete action per job (`DELETE /api/jobs/{id}`)

## 3. Manage Applications Page

Since no "get all applications" endpoint exists (by design, from Phase 11 -- applications are queried by student or by job), this page filters by Job ID, using `GET /api/applications/job/{jobId}`.

**State machine mirrored in the frontend**: `VALID_TRANSITIONS` is duplicated in JavaScript, matching the exact logic in `ApplicationService` (Phase 10), used only to decide which action buttons to display for each application's current status. This is a UI convenience for a better admin experience -- the backend independently re-validates every transition via its own `VALID_TRANSITIONS` map regardless of what the frontend displays, so an invalid request (even one bypassing the UI entirely) is still correctly rejected.

Actions supported per status:
- Simple transitions (e.g. APPLIED -> UNDER_REVIEW) trigger a direct `PATCH /api/applications/{id}/status` call
- Rejecting (from SHORTLISTED or INTERVIEW_SCHEDULED) uses a `prompt()` dialog to collect a rejection reason, enforcing that a non-empty reason is provided client-side before sending -- mirroring BR10, with the backend remaining the authoritative check
- Scheduling an interview and creating an offer each open a small inline form injected directly into the application's card (rather than a separate page or modal), collecting the needed fields and calling `POST /api/interviews/application/{id}` or `POST /api/offers/application/{id}` respectively

## 4. Verification Performed

Full end-to-end workflow test through the UI:
- Posted a new job via Manage Jobs, confirmed it appeared and could be deleted
- Filtered applications by job ID, confirmed real applications displayed with correct status and appropriate action buttons
- Advanced an application through UNDER_REVIEW -> SHORTLISTED
- Scheduled an interview via the inline form, confirmed the application automatically advanced to INTERVIEW_SCHEDULED (the cross-service coordination built in Phase 10, now observable end-to-end through the UI)
- Advanced to SELECTED, created an offer via the inline form, confirmed the application automatically advanced to OFFERED

This confirms the complete placement pipeline -- from job posting through final offer -- works correctly through the actual user interface, not just via Postman.

## 5. Phase 13 — Complete

| Part | Pages | Status |
|---|---|---|
| 1 | Login, Register | Done, tested |
| 2 | Student Dashboard, Jobs | Done, tested |
| 3 | My Applications | Done, tested |
| 4 | Admin Dashboard, Companies | Done, tested |
| 5 | Manage Jobs, Manage Applications | Done, tested |

All 9 planned pages are built, connected to the real backend via `authFetch`, and verified through direct use in the browser. The application now has a complete, functional user interface covering the entire placement workflow for both student and admin roles, backed by the fully secured and tested Spring Boot API built in Phases 6-12.

## 6. Known Follow-Ups (documented, not blocking)

- The "Apply" button color-change-to-green enhancement on the Jobs page (Part 2) was implemented but not fully re-verified due to limited fresh test data in that session.
- A dedicated Analytics page was not built separately; basic aggregate stats (company/job counts) are shown on the Admin Dashboard instead, considered sufficient for MVP scope. A more detailed analytics view (placement rate, offers per company, etc.) remains a possible future enhancement, consistent with FR19 from the original requirements.