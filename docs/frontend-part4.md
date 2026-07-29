# Placement Tracker System — Frontend (Part 4: Admin Dashboard & Companies Management)

## 1. Objective

Build the Admin Dashboard and Companies management page, establishing the admin-facing frontend pattern.

## 2. Admin Dashboard

- Shared navbar pattern extended for admin pages (Dashboard / Companies / Jobs / Applications / Logout), distinct from the student navbar
- Displays live counts of total companies and total jobs, fetched via `authFetch` from `/api/companies` and `/api/jobs`

## 3. Companies Management Page

- Displays all existing companies in a card list
- Includes an "Add New Company" form (`POST /api/companies`), clearing and reloading the list on success
- Includes a Delete button per company (`DELETE /api/companies/{id}`), guarded by a native browser `confirm()` dialog before proceeding -- a simple safety check against accidental destructive actions
- Handles both single-string and array error message shapes from the backend, consistent with the pattern established in the registration page

## 4. Design Note: Frontend Role Checks Not Duplicated

Admin pages call `requireLogin()` (checking that *someone* is logged in) but do not additionally check `getRole() === "ADMIN"` in JavaScript. This is a deliberate choice, not an oversight: the backend's `hasRole("ADMIN")` rules (Phase 12) are the actual, unbypassable enforcement. A student who somehow navigated to an admin page would simply receive `403 Forbidden` responses from every API call and see empty/broken data -- correctly denied access at the only layer that matters for real security.

## 5. Debugging Note

The Admin Dashboard initially displayed `0` for both companies and jobs, despite real data existing in the database. Diagnosed via the browser's Network tab: `admin-dashboard.js` returned a `404 Not Found`, meaning the script never loaded at all, so no API calls were ever made -- not a backend or logic bug, but a missing/misnamed file. Resolved by verifying the file's exact name and location matched the `<script src="js/admin-dashboard.js">` reference in the HTML.

**Lesson**: when a page's data never populates and no related network requests appear at all (not even failed ones), check whether the script file itself loaded successfully before investigating the script's logic -- a 404 on the script means none of its code ever ran.

## 6. Verification Performed

- Confirmed dashboard displays accurate company and job counts matching the real database
- Confirmed existing companies (created in earlier Postman testing) correctly appear in the Companies list
- Added a new company through the UI and confirmed it appears in the list immediately
- Deleted a company through the UI, confirmed the confirmation dialog appears, and confirmed the company is removed from the list after confirming

## 7. Phase 13 Progress

Completed: Part 1 (Login, Register), Part 2 (Student Dashboard, Jobs), Part 3 (My Applications), Part 4 (Admin Dashboard, Companies).
Remaining: Part 5 (Manage Jobs, Manage Applications, Analytics) -- final part of Phase 13.