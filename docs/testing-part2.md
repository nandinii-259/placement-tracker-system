# Placement Tracker System — Phase 15: Testing (Part 2: Additional Unit Tests & Scoping Decision)

## 1. Objective

Add unit test coverage for JobService's delete-dependency logic, and attempt an automated integration test before making a deliberate scoping decision about the remaining testing effort.

## 2. JobServiceTest

Two tests added, directly verifying the delete-dependency bug fix made earlier in Phase 13:
- `deleteJob_shouldThrowException_whenApplicationsExist` -- confirms a job with existing applications is correctly blocked from deletion
- `deleteJob_shouldSucceed_whenNoApplicationsExist` -- confirms a job with no dependents deletes successfully

Both pass, using the same Mockito mocking pattern established in Part 1.

## 3. Integration Testing Attempt and Scoping Decision

An integration test (`CompanyControllerIntegrationTest`) was attempted using two different standard approaches:

1. **MockMvc with `@AutoConfigureMockMvc`**: failed to compile -- `org.springframework.boot.test.autoconfigure.web.servlet` could not be resolved, despite `spring-security-test` and `spring-boot-starter-test` both being present in `pom.xml` and confirmed present via IntelliJ's class search.
2. **TestRestTemplate with `@SpringBootTest(webEnvironment = RANDOM_PORT)`**: also failed to compile -- `org.springframework.boot.test.web.client` could not be resolved.

**Root cause**: this project uses Spring Boot `4.0.8-SNAPSHOT` (a pre-release, actively-changing build), which has a notably more granular, fragmented dependency layout than stable Spring Boot releases -- already observed earlier in the project (e.g. `spring-boot-starter-webmvc-test` and `spring-boot-starter-data-jpa-test` as separate artifacts, rather than one combined `spring-boot-starter-test`). Two different standard Spring test packages, both normally bundled by default in stable releases, were unavailable in this snapshot's dependency tree even after adding their usual parent starters and invalidating IDE caches.

**Decision**: rather than continue chasing missing test artifacts in an unstable snapshot's dependency structure -- a genuine environment limitation, not a project design flaw -- automated integration testing (real Spring context + real HTTP requests) was deliberately descoped for this project. This is a pragmatic engineering trade-off: the time cost of further debugging a snapshot-specific dependency gap outweighed the incremental value, given that:

- Unit tests already provide strong, automated, repeatable coverage of the core business logic (BR1, BR2, BR3, BR6, BR10) via Part 1.
- The full request path (Controller -> Service -> Repository -> Database -> Security -> Response) has already been extensively and repeatedly verified manually via Postman (Phases 11-12) and the real browser-based frontend (Phase 13) -- including explicit three-way role verification (no token / wrong role / correct role) and ownership checks, which is genuinely thorough evidence even though it is manual rather than automated.

## 4. Honest Summary of Testing Coverage

| Testing Type | Coverage | Status |
|---|---|---|
| Unit tests (JUnit + Mockito) | ApplicationService (8 tests), JobService (2 tests) | Automated, passing, committed to the repo |
| Manual API testing (Postman) | Every endpoint, every business rule, every role/ownership combination | Extensive, performed throughout Phases 11-12 |
| Manual UI testing (browser) | Every page, every user workflow, end-to-end | Extensive, performed throughout Phase 13 |
| Automated integration tests | Not achieved | Descoped due to a snapshot-version dependency limitation, documented rather than silently omitted |

This is recorded honestly rather than overstated: the project has strong overall test coverage in practice, achieved through a combination of automated unit tests and extensive, repeated manual verification -- not full automated integration test coverage, which was genuinely blocked by tooling constraints outside the project's own design.

## 5. Phase 15 -- Complete

Given the environment constraint encountered, Phase 15 is considered complete with the coverage achieved: 10 passing automated unit tests covering the project's core business rules, on top of the manual testing evidence already documented across Phases 11-13.