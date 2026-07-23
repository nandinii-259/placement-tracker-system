# Placement Tracker System — Repositories

## 1. Objective

Create the Repository layer: one Spring Data JPA interface per entity, providing database access without writing manual SQL, plus a small set of custom "derived query methods" needed by known business rules.

## 2. Location

```
src/main/java/com/placementtracker/placement_tracker_backend/repository/
├── UserRepository.java
├── StudentRepository.java
├── CompanyRepository.java
├── JobRepository.java
├── ApplicationRepository.java
├── InterviewRepository.java
└── OfferRepository.java
```

## 3. Core Concept: Derived Query Methods

Each repository is a Java **interface** extending `JpaRepository<EntityType, IdType>`. Extending `JpaRepository` automatically provides standard CRUD operations (`save`, `findById`, `findAll`, `deleteById`, etc.) without any implementation code.

Beyond that, Spring Data JPA can generate working queries purely from a method's name and signature — no SQL or implementation is written by hand. For example, `findByEmail(String email)` is parsed as "find by the `email` field" and becomes the equivalent of `SELECT * FROM users WHERE email = ?`. This is called a derived query method.

## 4. Repository Summary

| Repository | Custom Methods | Purpose |
|---|---|---|
| UserRepository | findByEmail, existsByEmail | Login lookup; duplicate-email check during registration (FR2) |
| StudentRepository | findByUserId | Find a student's profile from their logged-in user ID |
| CompanyRepository | (none) | Basic CRUD only; no custom lookup currently required |
| JobRepository | findByCompanyId | List all jobs posted by a specific company |
| ApplicationRepository | findByStudentId, findByJobId, findByStudentIdAndJobId, existsByStudentIdAndJobId | Student's own applications (FR12); admin's per-job applications (FR13); duplicate-application check supporting BR1 |
| InterviewRepository | findByApplicationId | Find the (at most one) interview linked to an application (FR15, BR7) |
| OfferRepository | findByApplicationId | Find the (at most one) offer linked to an application (FR18, BR8) |

## 5. Design Notes

- **Optional<T> vs List<T>:** Methods expected to return at most one result (e.g. `findByEmail`, `findByUserId`, `findByApplicationId`) return `Optional<T>`, forcing calling code to explicitly handle the "not found" case rather than risk a null-related error. Methods expected to return multiple results (e.g. `findByStudentId`, `findByCompanyId`) return `List<T>`.
- **existsByStudentIdAndJobId supports BR1** at the application layer: the Service layer (Phase 10) will call this before attempting to save a new application, providing a clean, specific error message. The database's `UNIQUE(student_id, job_id)` constraint (Phase 5) remains as the final safety net if this check is ever bypassed — consistent with the project's "multiple layers of protection" principle.
- **CompanyRepository intentionally has no custom methods.** No current requirement needs a custom company lookup beyond standard CRUD; methods are added only when an actual use case calls for them, not speculatively.

## 6. Verification

Ran the application after adding all 7 repositories. Startup log confirmed:
```
Finished Spring Data repository scanning in 149 ms. Found 7 JPA repository interfaces.
```
All derived query method names were successfully parsed against their corresponding entity fields, and the application started cleanly with no errors.