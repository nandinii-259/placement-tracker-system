# Placement Tracker System — ER Diagram

This diagram is written in Mermaid syntax. GitHub renders Mermaid diagrams natively when viewing this file directly in the repository.

```mermaid
erDiagram
  USERS ||--o| STUDENTS : "one user has zero or one student profile"
  COMPANIES ||--o{ JOBS : "one company posts zero or many jobs"
  STUDENTS ||--o{ APPLICATIONS : "one student submits zero or many applications"
  JOBS ||--o{ APPLICATIONS : "one job receives zero or many applications"
  APPLICATIONS ||--o| INTERVIEWS : "one application gets zero or one interview"
  APPLICATIONS ||--o| OFFERS : "one application gets zero or one offer"

  USERS {
    id PK
    role "ADMIN or STUDENT"
  }
  STUDENTS {
    id PK
    user_id FK
    cgpa "used for eligibility"
  }
  COMPANIES {
    id PK
    name
  }
  JOBS {
    id PK
    company_id FK
    min_cgpa "eligibility rule"
  }
  APPLICATIONS {
    id PK
    student_id FK
    job_id FK
    status "workflow stage"
  }
  INTERVIEWS {
    id PK
    application_id FK
  }
  OFFERS {
    id PK
    application_id FK
  }
```

Note: this diagram shows only the most important columns of each table (primary keys, foreign keys, and the field most relevant to that table's role) for readability. The complete column list, with all data types and constraints, is documented in `docs/database-design.md`.

## How to Read the Relationship Labels

Each line is written in plain English instead of only using crow's-foot symbols, so it can be read directly. For example: "one job receives zero or many applications" means every application must point to exactly one real job (mandatory), while a job itself might have no applications yet, or many (optional-many).

## Relationship Summary

| Relationship | Cardinality | Notes |
|---|---|---|
| users - students | 1 : 0..1 | Every student profile belongs to exactly one user; not every user has a student profile (admins do not) |
| companies - jobs | 1 : 0..* | A company can exist with zero jobs; every job belongs to exactly one company |
| students - applications | 1 : 0..* | A student can have zero or many applications |
| jobs - applications | 1 : 0..* | A job can receive zero or many applications |
| applications - interviews | 1 : 0..1 | An application has at most one interview in this MVP |
| applications - offers | 1 : 0..1 | An application has at most one offer |

## Validation

This diagram was checked line-by-line against `docs/database-design.md` (Phase 3) and matches exactly — no schema changes were made during this phase.