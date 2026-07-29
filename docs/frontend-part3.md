# Placement Tracker System — Frontend (Part 3: My Applications Page)

## 1. Objective

Build the My Applications page, showing a student's applications with their current status, and conditionally displaying related interview and offer details.

## 2. my-applications.html

Follows the same navbar and layout pattern established in Part 2, with a dedicated container for dynamically rendered application cards.

## 3. Status Badge Styling

Each application status gets a distinct CSS class and color (`.status-APPLIED`, `.status-UNDER_REVIEW`, `.status-SHORTLISTED`, `.status-INTERVIEW_SCHEDULED`, `.status-SELECTED`, `.status-OFFERED`, `.status-REJECTED`), giving an immediate visual read of where each application stands in the workflow defined back in Phase 1 and implemented in Phase 10.

## 4. my-applications.js

- Loads the student's ID (via `/api/students/by-email`), then fetches their applications (`/api/applications/student/{id}`)
- For each application, conditionally fetches additional detail:
  - If status is REJECTED and a reason exists, displays it in a highlighted note (surfacing BR10's rejection reason directly to the student, closing the loop described back in Phase 1's business rule design)
  - If status suggests an interview may exist (INTERVIEW_SCHEDULED, SELECTED, OFFERED), fetches and displays interview details
  - If status is OFFERED, fetches and displays offer details
- Uses a `for...of` loop rather than `.forEach()`, since the loop body needs to `await` additional API calls per application -- `.forEach()` does not support `await` inside its callback
- `fetchInterview`/`fetchOffer` helper functions return `null` on failure (e.g. a 404 when no interview/offer exists yet) rather than throwing, so a missing related resource does not break rendering of the rest of the page

## 5. Verification

Loaded the page as Priya Sharma: both of her current applications (Software Engineer Intern, Data Analyst Intern) rendered correctly with job title, company, applied date, and an "APPLIED" status badge. No interview/offer sections appeared, correctly reflecting that these specific applications have not yet been progressed past the initial APPLIED status in the current dataset -- confirming the page displays genuine, accurate data rather than placeholder content.

## 6. Phase 13 Progress

Completed: Part 1 (Login, Register), Part 2 (Student Dashboard, Jobs), Part 3 (My Applications).
Remaining: Part 4 (Admin Dashboard, Companies management), Part 5 (Manage Applications, Analytics).