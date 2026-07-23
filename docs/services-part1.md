# Placement Tracker System — Services (Part 1: Company & Job)

## 1. Objective

Build the Service layer, starting with two simpler services (`CompanyService`, `JobService`) to establish the pattern before tackling the more complex `ApplicationService` in a following session.

## 2. Location

```
src/main/java/com/placementtracker/placement_tracker_backend/service/
├── CompanyService.java
└── JobService.java
```

## 3. Core Concept: Dependency Injection

Services need access to Repositories (and sometimes other Services) to do their work. Rather than manually instantiating these dependencies, Spring creates and supplies them automatically — a pattern called Dependency Injection.

This project uses **constructor injection** (passing dependencies as constructor parameters) rather than field injection (`@Autowired` directly on a field), which is the current best-practice approach in Spring — it makes dependencies explicit and the class easier to test in isolation.

```java
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    ...
}
```

The `@Service` annotation marks a class as a Spring-managed bean containing business logic, placing it correctly within the Controller -> Service -> Repository layered architecture defined in Phase 2.

## 4. CompanyService

| Method | Purpose |
|---|---|
| getAllCompanies() | Returns all companies (FR7) |
| getCompanyById(id) | Fetches one company; throws if not found |
| createCompany(company) | Saves a new company (FR6) |
| updateCompany(id, updatedCompany) | Fetches the existing company, updates only its mutable fields, saves |
| deleteCompany(id) | Fetches (to validate existence), then deletes |

`getCompanyById` uses `.orElseThrow(...)` on the repository's `Optional<Company>` result, making the "not found" case an explicit, deliberate decision rather than an accidental null.

## 5. JobService

| Method | Purpose |
|---|---|
| getAllJobs() | Returns all jobs |
| getJobsByCompany(companyId) | Returns jobs for a specific company (uses JobRepository.findByCompanyId) |
| getJobById(id) | Fetches one job; throws if not found |
| createJob(companyId, job) | Validates the company exists (via CompanyService), attaches it, then saves (FR8) |
| updateJob(id, updatedJob) | Fetches existing job, updates mutable fields, saves |
| deleteJob(id) | Fetches (to validate existence), then deletes |

`JobService` depends on both `JobRepository` and `CompanyService` (a Service depending on another Service, not just a Repository), demonstrating that `createJob` cannot succeed unless the referenced company genuinely exists — an application-layer check that complements the database's foreign key constraint from Phase 5.

## 6. Verification

Application restarted successfully after adding both services, confirming Spring correctly resolved and injected the dependency chain (CompanyRepository -> CompanyService -> JobService, plus JobRepository -> JobService) with no wiring errors.

## 7. Remaining Work for Phase 10

`ApplicationService`, `InterviewService`, `OfferService`, `StudentService`, and `UserService` (covering BR1-BR10 and the full status workflow) will be built in a following session, kept separate due to their significantly greater complexity.