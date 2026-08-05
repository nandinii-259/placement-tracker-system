# Placement Tracker System — Phase 16: Analytics

## 1. Objective

Build a dedicated Analytics page backed by real backend aggregation logic, formalizing the metrics approved in FR19 (Phase 1): total students, companies, jobs, applications, students placed, placement rate, and offers per company.

## 2. AnalyticsService

```java
public AnalyticsResponseDto getAnalytics() {
    long totalStudents = studentRepository.count();
    ...
    List<Application> offeredApplications = applicationRepository.findByStatus(Application.Status.OFFERED);

    long studentsPlaced = offeredApplications.stream()
            .map(app -> app.getStudent().getId())
            .distinct()
            .count();

    double placementRate = totalStudents == 0 ? 0.0 : (studentsPlaced * 100.0 / totalStudents);

    Map<String, Long> groupedByCompany = offeredApplications.stream()
            .collect(Collectors.groupingBy(
                    app -> app.getJob().getCompany().getName(),
                    Collectors.counting()
            ));
    ...
}
```

- `Repository.count()` provides simple totals directly from Spring Data JPA, no custom query needed.
- `studentsPlaced` correctly counts each student once (via `.distinct()` on student ID) even if a student theoretically had multiple offers -- placement is measured per student, not per offer.
- `placementRate` is guarded against division by zero when no students exist yet.
- `Collectors.groupingBy(..., Collectors.counting())` is Java's idiomatic way to turn a flat list of offered applications into "offer count per company," used here instead of manual loop-based counting.

## 3. New DTOs

- **CompanyOfferCountDto**: `companyName`, `offerCount` -- a minimal pair DTO for the per-company breakdown.
- **AnalyticsResponseDto**: aggregates all metrics into a single response shape, including a list of `CompanyOfferCountDto`.

## 4. AnalyticsController & Security

```
GET /api/analytics -> aggregate placement statistics (ADMIN only)
```

Secured via `.requestMatchers(HttpMethod.GET, "/api/analytics").hasRole("ADMIN")` in `SecurityConfig`, consistent with the project's role-based access pattern -- analytics is administrative data, not appropriate for student-level access.

## 5. Frontend: analytics.html / analytics.js

- Reuses the existing `.dashboard-cards` / `.card` styling (mono-font numbers) for the six top-line metrics, and `.application-card` styling for the per-company offer breakdown -- no new CSS needed, demonstrating the value of the shared design system built in Phase 13.
- Uses the `showLoading()` helper established in Phase 14 while data is fetched.
- Analytics link added to the shared admin navbar across all four existing admin pages for consistent navigation.

## 6. Verification

Cross-checked the API response against the actual database state via Postman before building the frontend:
```json
{
  "totalStudents": 4, "totalCompanies": 1, "totalJobs": 3,
  "totalApplications": 5, "studentsPlaced": 2, "placementRate": 50.0,
  "offersByCompany": [{"companyName": "Google", "offerCount": 3}]
}
```
All figures independently verified as consistent with known test data accumulated across prior phases (50% = 2 of 4 students placed). The rendered frontend page was confirmed to display these exact figures correctly.

## 7. Phase 16 -- Complete

FR19's approved analytics scope is fully implemented, tested against real data, and secured appropriately. No metrics beyond the originally approved list were added, consistent with the project's MVP-first philosophy.