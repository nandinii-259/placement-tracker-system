# Placement Tracker System — Database Design

## 1. Entities

Seven tables, each justified by specific functional requirements from `requirements.md`:

- **users** — login credentials and role, shared by both STUDENT and ADMIN accounts
- **students** — student-specific profile data (one-to-one with users)
- **companies** — companies participating in placements
- **jobs** — job postings, each belonging to one company
- **applications** — junction table connecting students and jobs, carrying the status workflow
- **interviews** — interview details for an application (one-to-one)
- **offers** — offer details for an application (one-to-one)

There is no separate `admins` table: an admin account is simply a `users` row with `role = ADMIN` and no matching `students` row. Admins have no data beyond what every account already needs (email, password, role), so a separate table would add structure without storing any new information.

There is no separate "registration" table: registration is a service-layer operation that creates a `users` row and a linked `students` row in one transaction. Tables store persistent state, not the actions that created it.

## 2. Table: users

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique account identifier |
| email | VARCHAR(150) | UNIQUE, NOT NULL | Login identifier (FR2) |
| password | VARCHAR(255) | NOT NULL | Hashed password, never plaintext |
| role | ENUM('ADMIN','STUDENT') | NOT NULL | Drives role-based access control (FR3) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Audit field |

## 3. Table: students

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique student profile identifier |
| user_id | BIGINT | FOREIGN KEY -> users(id), UNIQUE, NOT NULL | Links to exactly one login account (1:1) |
| full_name | VARCHAR(100) | NOT NULL | Profile info (FR4) |
| branch | VARCHAR(50) | NOT NULL | Academic branch |
| cgpa | DECIMAL(3,2) | NOT NULL | Used in eligibility checks (BR3). DECIMAL avoids floating-point rounding errors in comparisons |
| graduation_year | INT | NOT NULL | Placement-eligibility attribute |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Audit field |

Relationship: users 1:1 students (user_id is UNIQUE).

## 4. Table: companies

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique company identifier |
| name | VARCHAR(150) | NOT NULL | Company display name (FR6, FR7) |
| description | TEXT | NULL | Optional description |
| website | VARCHAR(255) | NULL | Optional website link |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Audit field |

## 5. Table: jobs

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique job identifier |
| company_id | BIGINT | FOREIGN KEY -> companies(id), NOT NULL | Every job belongs to exactly one company (FR8) |
| title | VARCHAR(150) | NOT NULL | Job title |
| description | TEXT | NULL | Job details |
| min_cgpa | DECIMAL(3,2) | NOT NULL | Eligibility threshold (BR3) |
| application_deadline | DATE | NOT NULL | Deadline check (BR2) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Audit field |

Relationship: companies 1:many jobs (one company can have many jobs; each job has exactly one company).

## 6. Table: applications

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique application identifier |
| student_id | BIGINT | FOREIGN KEY -> students(id), NOT NULL | Which student applied |
| job_id | BIGINT | FOREIGN KEY -> jobs(id), NOT NULL | Which job they applied to |
| status | ENUM('APPLIED','UNDER_REVIEW','SHORTLISTED','INTERVIEW_SCHEDULED','SELECTED','OFFERED','REJECTED') | NOT NULL, DEFAULT 'APPLIED' | Current stage in the status workflow |
| rejection_reason | VARCHAR(500) | NULL | Required (enforced in service layer) only when rejecting from SHORTLISTED or later (BR10) |
| applied_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the application was created |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last status change |

Additional constraint: UNIQUE(student_id, job_id) — enforces BR1 (no duplicate applications) at the database level.

Relationship: this is a junction table resolving the conceptual many-to-many between students and jobs into two one-to-many relationships (students 1:many applications, jobs 1:many applications).

Status field design decision: ENUM chosen over VARCHAR (prevents invalid/typo values) and over a separate lookup table (unnecessary JOIN overhead for a fixed, small set of statuses at this project's scale). A lookup table would be reconsidered if statuses needed to become configurable without a schema change in a larger production system.

## 7. Table: interviews

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique interview identifier |
| application_id | BIGINT | FOREIGN KEY -> applications(id), UNIQUE, NOT NULL | Which application this interview is for (1:1) |
| scheduled_at | DATETIME | NOT NULL | Exact date and time |
| mode | ENUM('ONLINE','OFFLINE') | NOT NULL | Interview mode |
| location_or_link | VARCHAR(255) | NULL | Venue or meeting link |
| outcome | ENUM('PENDING','PASSED','FAILED') | NOT NULL, DEFAULT 'PENDING' | Interview-specific outcome, separate from overall application status |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Audit field |

Relationship: applications 1:1 interviews (application_id is UNIQUE) — one interview per application in this MVP.

## 8. Table: offers

| Column | Type | Constraints | Purpose |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique offer identifier |
| application_id | BIGINT | FOREIGN KEY -> applications(id), UNIQUE, NOT NULL | Which application this offer is for (1:1) |
| position_title | VARCHAR(150) | NOT NULL | Role being offered |
| salary_ctc | DECIMAL(10,2) | NULL | Compensation figure; nullable since it may not be finalized immediately |
| offer_date | DATE | NOT NULL, DEFAULT (CURRENT_DATE) | When the offer was extended |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Audit field |

Relationship: applications 1:1 offers (application_id is UNIQUE).

## 9. Full Schema Overview

```
users (id, email, password, role, created_at)
   | 1:1 (user_id UNIQUE)
   v
students (id, user_id FK, full_name, branch, cgpa, graduation_year, created_at)
   | 1:many
   v
applications (id, student_id FK, job_id FK, status, rejection_reason, applied_at, updated_at)
   ^                                              | 1:1          | 1:1
   | 1:many                                       v              v
jobs (id, company_id FK, title, ...)      interviews (...)   offers (...)
   ^
   | 1:many
companies (id, name, description, website, created_at)
```

Relationship summary:
- users 1:1 students
- companies 1:many jobs
- students 1:many applications
- jobs 1:many applications
- applications 1:1 interviews
- applications 1:1 offers

## 10. Normalization Applied to This Schema

**First Normal Form (1NF):** Every column holds a single atomic value — no comma-separated lists or repeating groups in any column. Verified across all seven tables.

**Second Normal Form (2NF):** No table uses a composite primary key, so classic 2NF violations do not directly apply. However, the users/students split reflects the same underlying principle: columns like cgpa and branch only make sense for STUDENT rows, so they were separated into their own table rather than forced onto every row of a shared table.

**Third Normal Form (3NF):** No non-key column depends on another non-key column. Foreign keys (company_id, student_id, job_id, application_id) are used instead of duplicating data such as company name, student name, or job title into dependent tables.

**Anomalies avoided by this design:**
- Redundancy: student/company/job details are stored once and referenced via foreign keys, not copied into every related row.
- Update anomaly: e.g., updating a student's CGPA only requires updating one row in students, not every application row.
- Insertion anomaly: a company can be added independently of whether it has any jobs yet.
- Deletion anomaly: deleting an application does not delete the student's own profile data.

## 11. Business Rule to Database Mapping

| Business Rule | Enforcement |
|---|---|
| BR1 — no duplicate applications | UNIQUE(student_id, job_id) on applications |
| BR2 — deadline check | Service-layer comparison against jobs.application_deadline |
| BR3 — eligibility check | Service-layer comparison between students.cgpa and jobs.min_cgpa |
| BR6 — valid status transitions only | Service-layer state machine validation using applications.status |
| BR7 — interview only for SHORTLISTED | Service-layer check before inserting into interviews |
| BR8 — offer only for SELECTED | Service-layer check before inserting into offers |
| BR9 — no admin self-registration | Admin accounts created outside the public registration endpoint |
| BR10 — rejection reason required from SHORTLISTED+ | applications.rejection_reason (nullable column) + conditional service-layer validation |