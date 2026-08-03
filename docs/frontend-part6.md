# Placement Tracker System — Frontend (Part 6: Visual Redesign, Bug Fixes, Offer Letter & Response)

## 1. Objective

Redesign the visual identity of the frontend, fix three real bugs discovered through hands-on testing, and add two new features: a formatted offer letter view and the ability for students to accept or reject an offer.

## 2. Visual Redesign

Replaced the generic default styling with a deliberate "Official Record" identity, fitting the subject matter (a placement tracker deals in formal milestones — applications, admissions, offers):

- **Palette**: deep navy ink (`#14213d`) for structure/text, cool paper background (`#f3f5f8`), muted gold (`#b8912f`) reserved for the single most significant moment (an offer), brick red for rejections, sage green for positive/eligible states.
- **Type**: Newsreader (serif) for headings, IBM Plex Sans for body/UI, IBM Plex Mono for stat numbers — reinforcing a "ledger/record-keeping" feel for data.
- **Signature element**: application status badges form a color ramp that deepens as an application progresses through the workflow (structure encodes information), culminating in a rotated, gold-ringed "stamp" treatment uniquely applied to the OFFERED status.

Since every page shares one `style.css`, this required no HTML or JS changes anywhere in the project.

## 3. Bug Fixes

**Delete failures for companies/jobs with dependents**: attempting to delete a company with existing jobs, or a job with existing applications, previously caused an unhandled database error (raw 500). Fixed by adding explicit dependency checks in `CompanyService.deleteCompany()` and `JobService.deleteJob()`, throwing a clear `BusinessRuleException` instead of allowing the database's foreign key constraint to fail ungracefully -- consistent with the "specific exceptions over raw errors" pattern established in Phase 11.

**Apply button not reflecting prior applications**: the Jobs page always rendered every job with a fresh "Apply" button, even for jobs the student had already applied to in an earlier session -- only updating the button after a *successful* click within that session. Fixed by fetching the student's existing applications first (`loadAppliedJobs()`) and checking membership in that set when rendering each job card, so already-applied jobs correctly show a disabled "Applied" button immediately on page load.

**Students unable to view their own interview/offer data**: `GET /api/interviews/**` and `/api/offers/**` were both restricted to `hasRole("ADMIN")` in `SecurityConfig`, blocking students from viewing their own interview and offer details -- directly conflicting with FR15/FR18 from the original requirements. Root cause was traced via the browser's Network tab (403 responses) after confirming via Postman that the underlying data existed correctly. Fixed by splitting these rules by HTTP method: `POST` remains ADMIN-only (scheduling/creating), `GET` now only requires `authenticated()`.

## 4. New Feature: Offer Letter View

- **offer-letter.html / offer-letter.js**: a formatted, letter-styled read-only view of an existing offer (student name, position, company, formatted CTC, offer date), reached via a link from the My Applications page (`offer-letter.html?applicationId={id}`) and read from `window.location.search` via `URLSearchParams`.
- Includes a Print/Save-as-PDF button and dedicated `@media print` CSS rules hiding navigation and adjusting layout for clean printing.
- Explicitly does not create or infer any data -- it is a presentation layer over the same offer record the admin already created via Manage Applications; if the admin left `salaryCtc` blank, the letter simply omits that line.
- Added `formatCurrency()` to the shared `auth.js` helper, formatting raw numbers into Indian-locale currency strings (e.g. `600000` -> `₹6,00,000 per annum`), reused across the offer letter, the My Applications offer note, and available for future use.

## 5. New Feature: Offer Accept/Reject

New business rule (elicited explicitly rather than assumed): if a student accepts one offer, other pending offers are left untouched -- the student manually responds to each independently, with no automatic cascade.

- Added `offer_status` (ENUM PENDING/ACCEPTED/REJECTED, default PENDING) to the `offers` table and `Offer` entity.
- `OfferService.respondToOffer()` enforces that an offer can only be responded to once -- rejects attempts to change an already-accepted or already-rejected offer with a clear error.
- `OfferController` exposes `PATCH /api/offers/application/{id}/status`, with an ownership check (a student may only respond to their own offer; admins are exempted), mirroring the ownership pattern established for applications in Phase 12.
- Frontend: My Applications now shows Accept/Reject buttons for any PENDING offer, and a simple confirmation message once responded to, reloading the list after a successful response.

## 6. Debugging Note: Function Nested Inside a Loop

While integrating the Accept/Reject buttons, `respondToOffer()` was accidentally pasted inside the `for` loop body of `loadApplications()`, inside a conditional block -- structurally similar to the `AuthController` nesting bug from Phase 12. This caused `ReferenceError: respondToOffer is not defined` at runtime (the code parsed without a build error, since JavaScript is more permissive here than Java, but the function never became a callable top-level reference). Diagnosed via the browser console's exact error message and fixed by moving the function to the top level of the file, as a sibling to the other functions.

**Lesson repeated from Phase 12**: when pasting a new function into an existing file, always verify it sits at the correct nesting level (top-level vs. inside another function/loop) before assuming a "not defined" error is a typo rather than a structural placement mistake.

## 7. Known Follow-Up (Not Blocking)

An optional decline-reason field for offer rejection (mirroring BR10's rejection-reason pattern for applications) was designed at the database, service, and controller layers but deliberately deferred before frontend wiring was completed, at the user's request, to keep this session's scope manageable. The current Accept/Reject feature is fully functional and consistent end-to-end without it.