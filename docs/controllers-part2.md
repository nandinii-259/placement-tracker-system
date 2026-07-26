# Placement Tracker System — Controllers (Part 2: Job)

## 1. Objective

Build the second Controller, JobController, including handling a nested resource relationship (jobs belong to companies) in the URL structure.

## 2. JobRequestDto / JobResponseDto

New validation annotations introduced beyond Company's DTOs:
- `@NotNull` -- used instead of `@NotBlank` for non-String fields (BigDecimal, LocalDate), since "blank" does not apply to numbers or dates.
- `@DecimalMin(value = "0.0")` -- rejects a negative minimum CGPA.
- `@Future` -- rejects an application deadline that is already in the past, as an API-boundary safety check complementing the BR2 deadline check already enforced in the Service layer.

`JobResponseDto` flattens the related company into `companyId` and `companyName` fields rather than nesting the full `Company` object, keeping the API response predictable and avoiding exposure of the full entity graph.

## 3. JobController Endpoints

```
GET    /api/jobs                    -> list all jobs
GET    /api/jobs/{id}                -> get one job
GET    /api/jobs/company/{companyId} -> list jobs for a specific company
POST   /api/jobs/company/{companyId} -> create a job under a specific company (201)
PUT    /api/jobs/{id}                -> update a job
DELETE /api/jobs/{id}                -> delete a job (204)
```

The company relationship is expressed via nested URL paths (`/api/jobs/company/{companyId}`) rather than requiring the company ID inside the request body, since the company is a resource-defining relationship rather than an editable field of the job itself.

`toResponseDto()` reads `job.getCompany().getId()` and `job.getCompany().getName()` directly, made possible by the `@ManyToOne` relationship mapped in Phase 8 -- no manual lookup code required.

## 4. Debugging: Compile Error in Job Entity

On first run, the build failed with:
```
java: cannot find symbol
symbol:   method getCreatedAt()
location: variable job of type ... entity.Job
```

**Root cause:** Inspecting `Job.java` directly revealed two defects at the end of the file: `setApplicationDeadline()` had an empty body (never assigned the field), and `getCreatedAt()` was missing entirely from the file.

**Fix:** Corrected `setApplicationDeadline()` to properly assign `this.applicationDeadline = applicationDeadline;`, and added the missing `getCreatedAt()` getter.

**Lesson:** An empty setter is a particularly easy-to-miss bug -- it compiles fine and causes no immediate error, but silently discards the value it should be setting. Worth specifically checking setter bodies when debugging unexpected null values.

## 5. Postman Testing Performed

- `POST /api/jobs/company/1` with valid data -> 201 Created, full job object with `companyId`, `companyName`, and correctly precise `minCgpa` (BigDecimal displayed as `7.50`)
- `GET /api/jobs` -> 200 OK, array containing the created job with all fields correct

## 6. Phase 11 Progress

Completed so far: exception handling, CompanyController, JobController.
Remaining: ApplicationController, InterviewController, OfferController, StudentController (auth-related pieces deferred to Phase 12).