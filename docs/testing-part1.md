# Placement Tracker System — Phase 15: Testing (Part 1: ApplicationService Unit Tests)

## 1. Objective

Write real, automated unit tests for `ApplicationService`, the class carrying the most business-rule logic in the project (BR1, BR2, BR3, BR6, BR10), using JUnit 5 and Mockito.

## 2. Unit Testing Approach

Unit tests isolate a single class by replacing its dependencies with mocks -- controllable fakes that never touch a real database. This project uses:

- `@ExtendWith(MockitoExtension.class)`: activates Mockito's annotation processing for the test class.
- `@Mock`: creates fake versions of `ApplicationRepository`, `StudentService`, and `JobService`.
- `@InjectMocks`: creates a real `ApplicationService`, automatically injecting the three mocks into its constructor -- the same Dependency Injection concept from Phase 10, applied for testing.
- `@BeforeEach`: resets fresh test data (`student`, `job`) before every test method, preventing tests from affecting each other.
- `when(...).thenReturn(...)`: programs a mock's behavior for a specific call.
- `assertThrows(...)` / `assertEquals(...)`: verify both that an exception was thrown and that it carries the correct message -- confirming the code fails for the *right* reason, not just any reason.
- `verify(mock, never()).save(any())`: confirms no data was persisted when a business rule correctly blocks an operation.

## 3. Tests Written (8 total, in ApplicationServiceTest.java)

**applyToJob() -- BR1, BR2, BR3:**
- `applyToJob_shouldThrowException_whenAlreadyApplied` -- BR1
- `applyToJob_shouldThrowException_whenDeadlinePassed` -- BR2
- `applyToJob_shouldThrowException_whenCgpaTooLow` -- BR3
- `applyToJob_shouldSucceed_whenAllConditionsAreMet` -- confirms the valid path also works correctly, not just the rejection paths

**updateStatus() -- BR6, BR10:**
- `updateStatus_shouldSucceed_forValidTransition` -- BR6, valid path
- `updateStatus_shouldThrowException_forInvalidTransition` -- BR6, invalid path
- `updateStatus_shouldThrowException_whenRejectingFromShortlistedWithoutReason` -- BR10
- `updateStatus_shouldSucceed_whenRejectingFromShortlistedWithReason` -- BR10, valid path with reason correctly persisted

## 4. Debugging Note

One test initially failed:
```
Expected: class java.lang.IllegalArgumentException
Actual:   class com.placementtracker.placement_tracker_backend.exception.BusinessRuleException
```

Root cause: the test was written expecting the pre-Phase-12 generic exception type. Back in Phase 12, `ApplicationService` was deliberately refactored to throw the project's own specific `BusinessRuleException` instead of generic Java exceptions -- the test simply hadn't been written with that refactor in mind. The application code was behaving correctly (correct exception, correct message); only the test's expectation was wrong.

**Lesson**: a failing test is not automatically evidence of a code bug -- reading the actual vs. expected values in the failure message is essential before assuming which side (test or code) is incorrect. In this case, the stack trace's `Caused by` message confirmed the real application logic fired exactly as intended.

## 5. Verification

All 8 tests pass on a clean run (`4 tests passed` initially, then `8 tests passed` after the second batch was added and the one test fix applied), confirming BR1, BR2, BR3, BR6, and BR10 all behave correctly under automated, repeatable verification -- distinct from and complementary to the manual Postman/browser testing performed throughout Phases 11-13.

## 6. Next Steps

- Unit tests for other services (JobService, CompanyService, OfferService, InterviewService) as time allows
- Consider a small number of integration tests (real Spring context + in-memory or test database) as a following step, testing the full Controller -> Service -> Repository chain rather than isolated units