# Assignment 25 — Service unit tests

Requirement: “Hazırlanan sistemin service qovluğu 100 faiz cover edilməlidir unit test ilə.”

JUnit Jupiter and Mockito isolate all 14 service classes, using real service instances and mocked collaborators. No database, HTTP server, Docker, admin credentials or JWT secret are needed. The tests cover CRUD, validation, duplicate/not-found cases, relationships, authentication, token rotation/reuse, logout, sessions, account locking, cache lookup and transaction callback logic. Transaction annotations and actual database concurrency still require integration testing; these unit tests do not claim to verify Spring proxy behavior.

JaCoCo 0.8.14 supports Java 25. The report and verification use the same service package filter, including nested and anonymous classes. The verification fails for any missed line, branch or method (not rounded percentages). Production services are unchanged. This is an intentionally stricter interpretation than line coverage alone.

## Run

```bash
./gradlew clean test jacocoTestCoverageVerification
open build/reports/tests/test/index.html
open build/reports/jacoco/test/html/index.html
```

Inspect the service package in the coverage report. Do not claim 100% until the actual local run passes. The XML and CSV reports are under `build/reports/jacoco/test/`. If any tests or coverage fail, preserve the console output and CSV to diagnose the gap; do not lower the threshold or exclude service code.

## Evidence and submission

Screenshots are optional under the one-sentence assignment. Recommended: passing test summary and JaCoCo service package table. Store them under `screenshots/unit-tests/`. Do not commit `build/` or generated credentials.

Once the run passes:

```bash
git add build.gradle gradle/service-coverage.gradle src/test docs/ASSIGNMENT25.md screenshots/unit-tests
git commit -m "test: cover all service classes with JUnit and Mockito"
git push -u origin feature/unit-tests
```

Open a PR from `feature/unit-tests` into `feature/security-swagger` (or the instructor's required base if different). Suggested title: Assignment 25: Service layer unit tests and JaCoCo coverage.

## Scope

AccountService, AuthService, AuthorService, BookService, CourseService, DepartmentService, EmployeeService, IsolationWorkerService, PropagationDemoService, PropagationWorkerService, StudentService, TokenBlacklistService, TokenService, UserService.
