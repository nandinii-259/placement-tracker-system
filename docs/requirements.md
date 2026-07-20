# Placement Tracker System — Requirements Document

## 1. Problem Statement

College placement processes are typically managed manually or via scattered spreadsheets and emails. Students lack a single place to see which companies are hiring, which jobs they are eligible for, and the status of their applications. Placement admins lack a structured way to manage companies, jobs, applications, interviews, and offers, and have no easy way to view placement statistics. The Placement Tracker System centralizes this process into one secure, role-based web application.

## 2. Project Objectives

- Provide students a single portal to discover jobs and track their placement journey end-to-end.
- Provide admins full control over the placement pipeline: companies, jobs, applications, interviews, offers.
- Enforce placement-process rules consistently and securely (not just via frontend restrictions).
- Provide basic analytics so admins can see placement outcomes at a glance.
- Serve as a portfolio-quality project demonstrating full-stack engineering: clean architecture, secure auth, relational database design, and REST API design.

## 3. Stakeholders

| Stakeholder | Interest |
|---|---|
| Student | Wants visibility into jobs and their own application/interview/offer status |
| Placement Admin | Wants to manage the entire pipeline and see outcomes |
| Developer | Wants a working, well-architected, explainable, interview-ready project |

Note: There is no separate "Company/Recruiter" login role in the MVP — companies are data records managed by admins. This is a deliberate scope decision.

## 4. User Roles

### STUDENT
- Register / Login
- View companies and jobs
- View job details, including eligibility criteria
- Apply to eligible jobs
- View their own application statuses
- View their own scheduled interviews
- View their own offers

### ADMIN
- Login (no self-registration for admins)
- View/manage students
- CRUD on companies
- CRUD on jobs
- View and manage all applications; update statuses
- Schedule and manage interviews
- Create and manage offers
- View placement analytics

## 5. Functional Requirements (MVP)

**Auth**
- FR1: A student can register with name, email, password, and required academic details.
- FR2: A registered user (student or admin) can log in and receive an authentication token.
- FR3: The system enforces role-based access — a STUDENT cannot call ADMIN-only endpoints.

**Student Data**
- FR4: A student can view and update their own profile (academic details relevant to eligibility).
- FR5: An admin can view all students and their profiles.

**Company**
- FR6: An admin can create, update, delete, and list companies.
- FR7: A student can view a list of companies and a company's details.

**Job**
- FR8: An admin can create, update, delete, and list jobs, each linked to a company.
- FR9: A job has eligibility criteria (e.g., minimum CGPA) and an application deadline.
- FR10: A student can view jobs, and the system indicates whether they are eligible.

**Application**
- FR11: A student can apply to a job if eligible, before the deadline, and if they have not already applied.
- FR12: A student can view the status of their own applications.
- FR13: An admin can view all applications (with filters) and update an application's status following valid transitions only.

**Interview**
- FR14: An admin can schedule an interview for an application that has reached the correct stage.
- FR15: A student can view interviews scheduled for their own applications.
- FR16: An admin can update interview outcome (pass/fail), which affects the application status.

**Offer**
- FR17: An admin can generate an offer once an application reaches the "selected" stage.
- FR18: A student can view their own offers.

**Analytics**
- FR19: An admin can view aggregate metrics: total students, total companies, total jobs, total applications, students placed, placement rate, offers per company.

## 6. Non-Functional Requirements (MVP)

| NFR | Requirement |
|---|---|
| Security | Passwords hashed (never stored/exposed in plaintext); JWT-based auth; role-based authorization enforced server-side |
| Data integrity | Foreign keys and constraints enforced at the database level, not just the application level |
| Usability | Simple, clear UI; meaningful error messages |
| Reliability | Core flows (apply, status update) must not leave data in an inconsistent state |
| Performance | Reasonable response times for a project of this scale (no premature optimization) |
| Testability | Business logic isolated in the service layer so it can be unit tested |
| Maintainability | Layered architecture, DTOs at API boundaries, no logic duplication |

## 7. Business Rules

- BR1: A student cannot apply to the same job more than once (enforced via UNIQUE constraint on (student_id, job_id) + service-layer check).
- BR2: A student cannot apply after the job's application deadline (service-layer validation).
- BR3: A student must meet the job's eligibility criteria (e.g., minimum CGPA) to apply (service-layer validation).
- BR4: Only ADMIN role can create/update/delete companies and jobs (authorization).
- BR5: A student can only view/access their own applications, interviews, and offers — never another student's (authorization + resource ownership check).
- BR6: Application status can only move along valid transitions — no skipping stages, no illegal reversals (service-layer state machine validation).
- BR7: An interview can only be scheduled for an application in SHORTLISTED status.
- BR8: An offer can only be generated for an application in SELECTED status.
- BR9: Admins are not self-registered through the public API — they are created/seeded separately, to prevent anyone from granting themselves admin rights.
- BR10: Once an application reaches SHORTLISTED or later, any transition to REJECTED requires a non-empty rejection reason. This reason is stored on the application and is visible to both the admin and the student who owns that application (subject to the same ownership rule as BR5).

## 8. Application Status Workflow

```
APPLIED -> UNDER_REVIEW -> SHORTLISTED -> INTERVIEW_SCHEDULED -> SELECTED -> OFFERED
                 |                |                 |
             REJECTED         REJECTED          REJECTED
       (no reason required) (reason required) (reason required)
```

Valid transitions:
- APPLIED -> UNDER_REVIEW
- UNDER_REVIEW -> SHORTLISTED
- UNDER_REVIEW -> REJECTED (no reason required)
- SHORTLISTED -> INTERVIEW_SCHEDULED
- SHORTLISTED -> REJECTED (reason required, per BR10)
- INTERVIEW_SCHEDULED -> SELECTED
- INTERVIEW_SCHEDULED -> REJECTED (reason required, per BR10)
- SELECTED -> OFFERED

## 9. Sample User Stories & Acceptance Criteria

**US-1:** As a student, I want to apply to a job I'm eligible for, so that I can be considered for placement.
- AC1: Apply action is blocked if I don't meet eligibility criteria.
- AC2: Apply fails with a clear error if the deadline has passed.
- AC3: Apply fails with a clear error if I've already applied.
- AC4: On success, a new application record is created with status APPLIED.

**US-2:** As an admin, I want to update an application's status, so that the placement pipeline reflects reality.
- AC1: Only valid transitions are allowed; invalid ones return a clear error.
- AC2: If rejecting from SHORTLISTED or INTERVIEW_SCHEDULED, a reason is required.
- AC3: Status change (and reason, if applicable) is visible to the relevant student immediately.

**US-3:** As a student, I want to see only my own applications, interviews, and offers.
- AC1: Attempting to access another student's application via API returns 403 Forbidden, not the data.

(This list will be expanded per-module during each module's design step.)

## 10. MVP Scope — In

Registration/login, role-based access, student profile, company CRUD, job CRUD, apply to job, application status tracking with valid transitions and rejection reasons, interview scheduling, offer generation, basic analytics, secure REST APIs, a simple functional frontend.

## 11. Out of Scope (Deferred — Future Enhancements)

Resume upload, advanced eligibility filtering beyond CGPA, email notifications, exportable reports, AI-based features, company/recruiter self-service login, advanced pagination/sorting. These require explicit approval after the core MVP is complete.

## 12. Assumptions

- One student has exactly one placement profile.
- A job belongs to exactly one company.
- Eligibility for MVP is based on a single numeric field: minimum CGPA.
- Admin accounts are created outside the public registration flow.

## 13. Constraints

- Tech stack is fixed: HTML/CSS/JS frontend, Spring Boot backend, MySQL database.
- Development pace is beginner-paced: ~2-4 hours per session, one primary objective per session.