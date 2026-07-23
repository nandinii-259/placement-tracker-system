# Placement Tracker System — JPA Entities

## 1. Objective

Create JPA Entity classes mapping our 7 database tables (designed in Phase 3, created in Phase 5) into Java, so the application can read and write data using plain Java objects instead of raw SQL.

## 2. Location

```
src/main/java/com/placementtracker/placement_tracker_backend/entity/
├── User.java
├── Student.java
├── Company.java
├── Job.java
├── Application.java
├── Interview.java
└── Offer.java
```

## 3. Core Annotations Used

| Annotation | Purpose |
|---|---|
| `@Entity` | Marks a class as mapped to a database table |
| `@Table(name = "...")` | Specifies the exact table name, since Java class names (singular, PascalCase) differ from table names (plural, snake_case) |
| `@Id` | Marks the primary key field |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Delegates ID generation to the database's AUTO_INCREMENT, rather than generating it in Java |
| `@Column(...)` | Maps a field to a specific column, with attributes for nullability, uniqueness, length, and precision/scale matching the SQL schema exactly |
| `@Enumerated(EnumType.STRING)` | Stores Java enums as their literal text value (e.g. "ADMIN") rather than a numeric index, matching SQL ENUM columns |
| `@OneToOne` / `@ManyToOne` | Map relationships between entities, matching the cardinality designed in Phase 3 and diagrammed in Phase 4 |
| `@JoinColumn(name = "...")` | Specifies the actual foreign key column name backing a relationship |
| `@PrePersist` / `@PreUpdate` | Lifecycle hooks that run automatically before a row is first saved / before it is updated, used to auto-populate timestamp fields |

## 4. Relationship Mapping Summary

| Entity Field | Annotation | Matches Schema Relationship |
|---|---|---|
| Student.user | @OneToOne, unique join column | users 1:1 students |
| Job.company | @ManyToOne | companies 1:many jobs |
| Application.student | @ManyToOne | students 1:many applications |
| Application.job | @ManyToOne | jobs 1:many applications |
| Interview.application | @OneToOne, unique join column | applications 1:1 interviews |
| Offer.application | @OneToOne, unique join column | applications 1:1 offers |

The `@ManyToOne` direction was implemented without a corresponding `@OneToMany` on the "one" side (e.g. no `List<Job> jobs` field on `Company`), since no current use case requires navigating that direction directly from the parent entity. This avoids unnecessary complexity; it can be added later if a genuine need arises.

## 5. Design Decisions Carried Over from the Database Schema

- **BigDecimal used for cgpa, minCgpa, and salaryCtc** — matches SQL's DECIMAL type, avoiding floating-point rounding errors in values that are directly compared (eligibility checks).
- **LocalDate used for applicationDeadline and offerDate; LocalDateTime used for scheduledAt, createdAt, appliedAt, updatedAt** — Java types chosen to match each SQL column's actual semantics (a calendar day vs. a specific date and time).
- **Application.rejectionReason left nullable in the entity, with no validation logic here** — the Entity layer only describes what is possible to store. The conditional rule that a reason is required when rejecting from SHORTLISTED or later (BR10) belongs in the Service layer (Phase 10), consistent with the project's layered architecture.
- **Application.status defaults to Status.APPLIED in Java**, mirroring the SQL `DEFAULT 'APPLIED'`.
- **@UniqueConstraint on Application(student_id, job_id)** enforces BR1 (no duplicate applications) at the entity level, matching the database's UNIQUE constraint.

## 6. Verification

With `spring.jpa.hibernate.ddl-auto=validate` active (set in Phase 7), the application was run after writing all 7 entities. Hibernate validated every entity's mapping (column names, types, constraints, relationships) against the real tables in `placement_tracker_db` and the application started successfully with no validation errors — confirming full consistency between the Phase 3 design, the Phase 5 SQL schema, and the Phase 8 Java entities.