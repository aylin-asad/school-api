# School API — Spring Boot 4 + Spring Data JPA

Praktiki tapşırıq: `az.iktex.organization.structure.testders` paketində REST API.

## Texnologiyalar

- Java **25**, Gradle **9.8.2**, Spring Boot **4.0.9**
- PostgreSQL **16** (Docker Compose)
- Spring Data JPA (Hibernate), Lombok, MapStruct **1.6.3**
- `spring.jpa.open-in-view=false`, `ddl-auto=update`, `show-sql=true`
- **9 entity, 9 repository, 8 mapper, 8 request/8 əsas response record, 7 service, 7 controller**

## 1. Lokal mühit (macOS)

Lazımdır: Java 25, Gradle 9.x (ən az 8.14 də işləyir), Docker Desktop.

```bash
java -version
gradle --version
docker --version
```

> `java -version` Java 25 deyilsə, Java 25 quraşdırın və `JAVA_HOME` düzgün seçin.
> Əvvəlki layihədəki Java 21 ilə bu tapşırığın Java 25 tələbi eyni deyil.

## 2. PostgreSQL-i başladın

```bash
cd school-api
docker compose up -d
docker ps
```

DB: `localhost:5432`, database `school_db`, user `postgres`, password `postgres`.
Port doludursa, Docker/yerli PostgreSQL konfliktini həll edin.

## 3. Gradle wrapper və server

Bu arxivdə Gradle Wrapper **JAR** yoxdur, çünki bu mühitdə rəsmi binary faylı endirmək mümkün olmayıb. İlk dəfə lokal quraşdırılmış Gradle ilə yaradın:

```bash
gradle wrapper --gradle-version 9.8.2
./gradlew clean build
./gradlew bootRun
```

Əgər `gradle --version` köhnə **8.11.1** göstərirsə, yeni Spring Boot 4 plugin-i ilə `gradle wrapper` əmri uğursuz ola bilər. O halda əvvəl Gradle 9.8.2-ni birbaşa yükləyin:

```bash
curl -L -o gradle-9.8.2-bin.zip https://services.gradle.org/distributions/gradle-9.8.2-bin.zip
unzip gradle-9.8.2-bin.zip
./gradle-9.8.2/bin/gradle wrapper --gradle-version 9.8.2
./gradlew clean build
```

`gradle-9.8.2/` qovluğu yaradılırsa, onu repository-yə əlavə etməyin. Aşağıdakı komanda ilə silə bilərsiniz: `rm -rf gradle-9.8.2 gradle-9.8.2-bin.zip`.

`./gradlew` fallback launcher mövcuddur: JAR yaradılmayıbsa, quraşdırılmış `gradle` komandasını işlədir. `gradle wrapper` icra etdikdə standart Gradle wrapper faylları yaradılacaq. GitHub-a **rəsmi** `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar` və `.properties` əlavə edin.

Application: **http://localhost:8080/test-demo**

## 4. Postman

Import edin: `postman/school-api.postman_collection.json`. `baseUrl` dəyişəni öncədən:

`http://localhost:8080/test-demo/api`

Create request-lər response ID-lərini `userId`, `studentId`, `courseId`, və s. dəyişənlərə avtomatik yazır. **Kolleksiyanı yuxarıdan aşağı başdan sona işlətməyin**: `DELETE` request-lər test data-sını silir. Əvvəl create, sonra relationship əməliyyatları, sonra yalnız istədiyiniz DELETE ssenarisi.

## 5. Bütün endpoint-lər

### User + Passport

| Method | Path |
|---|---|
| POST | `/api/users` |
| GET | `/api/users/{id}` |
| GET | `/api/users/by-email?email=...` |
| GET | `/api/users?gender=MALE` |
| GET | `/api/users/without-passport` |
| PUT | `/api/users/{id}` |
| DELETE | `/api/users/{id}` |
| POST | `/api/users/{id}/passport` |
| GET | `/api/users/{id}/passport` |
| DELETE | `/api/users/{id}/passport` |

### Department + Employee

| Method | Path |
|---|---|
| POST | `/api/departments` |
| GET | `/api/departments/{id}` |
| GET | `/api/departments` |
| GET | `/api/departments/without-employees` |
| PUT / DELETE | `/api/departments/{id}` |
| POST | `/api/employees` |
| GET | `/api/employees/{id}` |
| GET | `/api/employees/department/{departmentId}` |
| GET | `/api/employees/salary-range?min=500&max=2000` |
| GET | `/api/employees/department/{id}/average-salary` |
| PUT / DELETE | `/api/employees/{id}` |

### Student + Course

| Method | Path |
|---|---|
| POST | `/api/students` |
| GET | `/api/students/{id}` |
| GET | `/api/students` |
| GET | `/api/students/without-courses` |
| PUT / DELETE | `/api/students/{id}` |
| POST / DELETE | `/api/students/{studentId}/courses/{courseId}` |
| POST | `/api/courses` |
| GET | `/api/courses`, `/api/courses/{id}` |
| GET | `/api/courses/student/{studentId}` |
| GET | `/api/courses/{id}/students` |
| GET | `/api/courses/{id}/students/count` |
| PUT / DELETE | `/api/courses/{id}` |

### Author + Book

| Method | Path |
|---|---|
| POST | `/api/authors` |
| GET | `/api/authors/{id}` |
| GET | `/api/authors?country=Azerbaijan` |
| GET | `/api/authors/without-books` |
| GET | `/api/authors/{id}/books` |
| PUT / DELETE | `/api/authors/{id}` |
| POST | `/api/books` |
| GET | `/api/books/{id}`, `/api/books?title=java` |
| GET | `/api/books/cheaper-than?price=20` |
| PUT / DELETE | `/api/books/{id}` |
| POST / DELETE | `/api/books/{bookId}/authors/{authorId}` |

## 6. Qiymətləndirmə ekran görüntüləri — hansı ardıcıllıqla?

1. `POST /api/users`, `POST /api/users/{id}/passport`, sonra `GET /api/users/{id}` — `passport` response daxilində gəlsin. Konsoldakı JOIN FETCH SQL-i də ayrıca çək.
2. pgAdmin/DBeaver:

   ```sql
   SELECT id, name, surname, gender_id FROM users;
   ```

   `MALE=1`, `FEMALE=2` olmalıdır.
3. `DELETE /api/users/{id}/passport`, sonra DB-də:

   ```sql
   SELECT * FROM passports WHERE user_id = <user_id>;
   ```

   Boş nəticə — `orphanRemoval`.
4. `POST /api/departments`, `POST /api/employees` (departmentId verin), `DELETE /api/departments/{id}` — `409 Conflict` və xəta mesajı.
5. İki ayrı kurs + tələbə yaradın. `POST /api/students/{studentId}/courses/{courseId}` və ikinci kursa enroll. Sonra tələbəni `DELETE` edin; `GET /api/courses` hələ iki kursu qaytarmalıdır.
6. Kitab və 2 müəllif yaradın, hər ikisini bağlayın, ilk müəllifi yenidən bağlayın — `409 Conflict`.
7. `GET /api/users/{id}`, `GET /api/departments/{id}`, `GET /api/students/{id}`, `GET /api/books/{id}` üçün `Hibernate: select ... join ...` görünməlidir. **Bir konkret detail sorğunun SQL-inin tək SELECT** etdiyini göstərin (əlavə unrelated SQL-lər ssenarini korlamasın).

## 7. Arxitektura / vacib qaydalar

- Controller yalnız request / response `record` istifadə edir; heç vaxt entity qaytarmır.
- `@Transactional(readOnly=true)` class səviyyəsində, dəyişən metodlarda `@Transactional`.
- `PUT` metodlarında `save()` yoxdur — JPA dirty checking.
- Yoxlamalar: duplicate user/email/passport, department/employee, enrollment, book-author association, salary bounds.
- 400 invalid request; 404 missing item; 409 conflicting business rule.
- `@ManyToMany` kurslarda REMOVE cascade **yoxdur** — tələbə silinməsi kursu silə bilməz.
- `Course` inverse tərəfdir; kurs silməzdən əvvəl `Student.courses` əlaqələri qırılır.
- Department `cascade=ALL` olsa da, işçisi varsa service `DELETE`-ə icazə vermir.
- AuthorBook ayrıca entity-dir; Author və Book əks tərəflərə cascade REMOVE etmir.

## 8. GitHub

```bash
git init
git add .
git commit -m "Implement school REST API with JPA relationships"
git branch -M main
git remote add origin https://github.com/<username>/school-api.git
git push -u origin main
```

Repo yaratdıqdan sonra `<username>` hissəsini öz GitHub istifadəçi adınızla əvəz edin. `build/`, `.gradle/`, `.idea/` `.gitignore`-a daxildir.

## 9. Yoxlanmanın vəziyyəti

**Qeyd:** Bu arxiv Java/Gradle/DB ilə end-to-end build olunmayıb (hazırkı çalışma mühitində Java 21 var, Gradle və Docker yoxdur). Ona görə `./gradlew clean build` və Postman smoke test-i öz kompüterinizdə mütləq işə salın. Kod statik yoxlamadan keçirilib.
# School API — Practical Assignment 2

Spring Boot REST API built on the existing `school-api` project. This assignment demonstrates **Liquibase database migrations**, **Hibernate second-level and query caching**, **Spring transaction propagation**, and **PostgreSQL transaction isolation**.

## Technology and environment

- Java 25; Spring Boot 4.0.9; Gradle; Spring Data JPA / Hibernate; PostgreSQL 16
- Liquibase for versioned database schema changes
- Hibernate JCache with Ehcache 3 for caching
- Local PostgreSQL: `localhost:5433`, database `school_db`
- Local application: `http://localhost:8080/test-demo`
- Development profile uses the Liquibase `dev` context for seed data

> The commands below reflect the local development setup used for the demonstrations. The transaction and isolation endpoints are educational diagnostics, not production APIs.

## Start the project

1. Start the existing PostgreSQL Docker container or Docker Compose services:

   ```bash
   docker start school-postgres
   ```

   Alternatively, if using the project's Docker Compose configuration:

   ```bash
   docker compose up -d
   ```

2. Confirm that `src/main/resources/application.yaml` points to the local PostgreSQL instance and that Liquibase is enabled. Hibernate must validate the database schema rather than create it:

   ```yaml
   spring:
     jpa:
       hibernate:
         ddl-auto: validate
     liquibase:
       enabled: true
       change-log: classpath:db/changelog/db.changelog-master.yaml
       contexts: dev
   ```

3. Build and run from the repository root:

   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

The REST API is served under `/test-demo/api`.

## Part A — Liquibase (30 points)

Liquibase changesets live under `src/main/resources/db/changelog/`; the master file is `db.changelog-master.yaml`. They create and manage the application's tables, including users/passports, departments/employees, students/courses, authors/books/author-books, and `audit_logs`.

**Demonstrated behaviors:**

- Database objects are created by Liquibase; Hibernate uses `ddl-auto: validate`.
- Executed changesets are recorded in PostgreSQL's `databasechangelog` table; the development seed data is controlled by context `dev`.
- Tag `v1.0` provides a rollback target. Later migrations add `courses.capacity` and `audit_logs`.
- Editing a previously executed changeset produces a checksum validation error; restoring the original changeset resolves the error.
- Deliberately mismatching an entity mapping causes Hibernate schema validation to fail, demonstrating that it does not silently alter the database.
- Rollback to `v1.0` removes later objects, and `update` restores them.

Useful checks:

```bash
./gradlew status
./gradlew update
```

The Gradle Liquibase rollback activity is configured to use the tag `v1.0`:

```bash
./gradlew rollback -PliquibaseTag=v1.0
```

**Important:** Do not run rollback on a shared or production database. Stop the application before running rollback/update. Do not modify executed changesets. The demo seed scripts are development-only; because identity sequences can advance after a rollback, verify seeded foreign-key IDs before assuming that a repeated data restore is safe.

**Why Liquibase instead of `ddl-auto: update`?** Liquibase provides ordered, reviewable changes, checksums, history, contexts, tags, and rollback support. By contrast, implicit Hibernate updates are difficult to audit and coordinate across a team. The `databasechangeloglock` table prevents concurrent migration processes from changing the schema simultaneously.

## Part B — Hibernate L2, collection and query caching (25 points)

Caching is enabled through Hibernate's JCache integration with Ehcache, configured by `src/main/resources/ehcache.xml` and `application.yaml`.

| Cached data | Region | Strategy |
|---|---|---|
| `Department` | `departments` | `READ_WRITE` |
| `Course` | `courses` | `READ_WRITE` |
| `Author` | `authors` | `READ_WRITE` |
| `Book` | `books` | `NONSTRICT_READ_WRITE` |
| `AuthorBook` | `author-books` | `READ_WRITE` |
| `Author.authorBooks` | `author-authorBooks` | `READ_WRITE` |
| `Book.authorBooks` | `book-authorBooks` | `READ_WRITE` |
| Cached author-country query results | `default-query-results-region` | Query cache |

The cache configuration includes `hibernate.cache.auto_evict_collection_cache=true` so changes to bidirectional links do not leave previously cached inverse collections stale. Caching behavior is observable through `CacheController`.

**Diagnostic endpoints:**

```text
GET    /test-demo/api/cache/stats
DELETE /test-demo/api/cache
DELETE /test-demo/api/cache/stats
DELETE /test-demo/api/cache/{region}
```

**Demonstrated behaviors:**

1. Two separate requests to `GET /test-demo/api/courses/6` produce one cache miss/put and then one L2 hit.
2. Repeating `GET /test-demo/api/authors?country=Azerbaijan` produces a query-cache hit; inserting an author invalidates the cached query result.
3. Evicting `/api/cache/courses` causes the next course lookup to miss and repopulate the region.
4. Repeating `GET /test-demo/api/books/9` produces hits in both `books` and `book-authorBooks`.
5. Updating Book 9's price returns fresh data instead of an obsolete cached value.
6. Repeating `GET /test-demo/api/authors/6/books` demonstrates cached `Author.authorBooks`, including an empty collection.
7. Linking and unlinking books updates the author-book relationship and invalidates the relevant cached collection.

Example:

```bash
curl -s http://localhost:8080/test-demo/api/cache/stats | python3 -m json.tool
```

**Note:** IDs 6 and 9 are examples from the development database used for verification. Identity values may differ after migrations, rollback, or reseeding.

## Part C — Transaction propagation (20 points)

The demonstration uses separate Spring service beans to ensure `@Transactional` proxy interception. `PropagationDemoService` opens the outer transaction, and `PropagationWorkerService` performs audit inserts to the existing `audit_logs` table.

| Propagation | Demonstration | Expected outcome |
|---|---|---|
| `REQUIRED` | Audit insert joins an outer transaction that deliberately throws an exception | Insert is rolled back |
| `REQUIRES_NEW` | Audit insert runs in a separate transaction before the outer exception | Insert remains committed |
| `MANDATORY` | Invoke worker outside and inside a transaction | Outside fails; inside succeeds |

```bash
curl -s -X POST http://localhost:8080/test-demo/api/tx-demo/rollback | python3 -m json.tool
curl -s http://localhost:8080/test-demo/api/tx-demo/audit | python3 -m json.tool
curl -s -X POST http://localhost:8080/test-demo/api/tx-demo/mandatory/outside | python3 -m json.tool
curl -s -X POST http://localhost:8080/test-demo/api/tx-demo/mandatory/inside | python3 -m json.tool
curl -s http://localhost:8080/test-demo/api/tx-demo/audit | python3 -m json.tool
```

**Observed in the local demonstration:** The `REQUIRES_NEW` row survived the intentional rollback, no `REQUIRED` row remained, the outside `MANDATORY` call reported an expected failure, and the inside `MANDATORY` call committed its audit row.

## Part D — PostgreSQL transaction isolation (20 points)

The isolation demonstration uses `IsolationDemoController`, `IsolationWorkerService`, independent threads, transaction annotations, and coordinated barriers. A separate Liquibase changeset (`008-isolation-demo.yaml`) creates the two demonstration tables: `isolation_accounts` and `isolation_doctors`.

Run the scenarios **sequentially**; each scenario resets its own demonstration rows:

```bash
curl -s http://localhost:8080/test-demo/api/isolation/d1 | python3 -m json.tool
curl -s http://localhost:8080/test-demo/api/isolation/d2 | python3 -m json.tool
curl -s http://localhost:8080/test-demo/api/isolation/d3 | python3 -m json.tool
```

| Test | Comparison | Expected result |
|---|---|---|
| **D1: Non-repeatable read** | `READ_COMMITTED` vs. `REPEATABLE_READ` | The first sees balance `100 -> 150`; the second sees `100 -> 100` |
| **D2: Lost update** | Unsafe read-modify-write vs. atomic SQL update | Unsafe balance `110`; atomic fix `120` |
| **D3: Write skew** | `REPEATABLE_READ` vs. `SERIALIZABLE` | Repeatable Read can leave 0 doctors on call; Serializable preserves at least 1 and aborts a conflicting transaction (PostgreSQL SQLSTATE `40001`) |

**Validation:** Confirm that each JSON response contains `"passed": true`. A screenshot alone is evidence of running a test, not proof that it passed. The SERIALIZABLE failure is an *expected controlled result*, not an application startup failure.

## Evidence / screenshots

Screenshots are stored in `screenshots/` and document verification of the implementation. Filenames used during the assignment:

- **Part A:** `A01_Liquibase_Startup.png` through `A07_Restore_After_Rollback.png`.
- **Part B:** `B01_Course_L2_Cache_Hit.png` through `B09_Author_Book_Unlink_Cache_Invalidation.png`.
- **Part C:** `C01_Required_Rollback.png`, `C02_RequiresNew_Commit.png`, `C03_Mandatory_Propagation.png`.
- **Part D:** `D01_Transaction_Isolation_All_Tests.png` (combined output for D1, D2, D3). If a single image does not show all three results legibly, provide separate screenshots.

Only include screenshots that were actually captured and verified. Avoid committing passwords, production data, or large build artifacts.

## Project layout (relevant files)

```text
src/main/java/az/iktex/organization/structure/testders/
  controller/
    CacheController.java
    PropagationDemoController.java
    IsolationDemoController.java
  service/
    PropagationDemoService.java
    PropagationWorkerService.java
    IsolationWorkerService.java
  entity/
    AuditLog.java
src/main/resources/
  application.yaml
  ehcache.xml
  db/changelog/
    db.changelog-master.yaml
    changes/
      001-... through 008-isolation-demo.yaml
      100-seed-data.yaml
scripts/
  enable-part-d.sh
screenshots/
  ...
```

## Final verification

```bash
./gradlew clean build
./gradlew status
```

Confirm that application startup succeeds, the Liquibase changelog is up to date, Part B cache counters show hits and invalidation, Part C audit behavior matches the expectations above, and D1/D2/D3 each return `passed: true`. Then commit the code, migration, README and screenshots to the assignment branch.

## Practical assignment 3: Spring Security and stateful JWT

The new security layer adds independent login `Account` records, numeric roles, BCrypt(12), access and refresh JWTs, persisted token metadata, database revocation, refresh rotation and reuse detection, session administration, password changes, role-based authorization, localized security errors, and Swagger bearer authentication. Existing User/Passport entities and previous migrations remain unchanged.

### Local run (development database)

Requirements: JDK 25 (existing project toolchain), Gradle wrapper, Docker Compose, Python 3, OpenSSL. From the project root:

```bash
bash scripts/run-security.sh
```

The first run prompts for the initial administrator password and generates a Base64 32-byte JWT secret. The values are stored only in a private, Git-ignored `.env` file. If `admin` already exists, use its current password; the seeder never resets an existing administrator password. Keep the same secret between app restarts. To set the variables manually, export `JWT_SECRET` and `ADMIN_PASSWORD` in your shell before starting the application (do not commit the values).

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
read -s ADMIN_PASSWORD; export ADMIN_PASSWORD
./gradlew bootRun
```

API root: `http://localhost:8080/test-demo/api`. Swagger: `http://localhost:8080/test-demo/swagger-ui.html`. The `/test-demo` context path is retained from the previous assignment.

### Verification and screenshot evidence

Leave the application running and open a second terminal in the project root:

```bash
bash scripts/verify-security.sh
```

The script exercises checklist items 1–11 and 14–16 (item 15 checks OpenAPI JSON; interactive Swagger is manual), as well as the account-lockout bonus. It uses randomly named disposable accounts and does not delete existing records. The SQL for the two mandatory screenshots is saved to `screenshots/security/logout-evidence.sql`; the corresponding database output is saved to `screenshots/security/logout-evidence.txt`. Run these SQL queries in IntelliJ's database console or use:

```bash
docker exec -i school-postgres psql -X -U postgres -d school_db < screenshots/security/logout-evidence.sql
```

Capture genuine screenshots of the two result tables showing revoked access and refresh rows and blacklist reason `LOGOUT`. Suggested names: `F01_Auth_Tokens_Logout.png` and `F02_Token_Blacklist_Logout.png`. Attach them to the `feature/token-blacklist` PR. Capture the Swagger Authorize UI separately if desired; mask bearer tokens in screenshots. Decode tokens locally rather than sending active credentials to external sites.

For tests 12–13 only, stop the app and restart with temporary environment overrides:

```bash
APP_JWT_ACCESS_TOKEN_TTL=1m APP_SECURITY_TOKEN_CLEANUP_CRON='0 * * * * *' bash scripts/run-security.sh
# In a separate terminal:
bash scripts/verify-security.sh --expiry
```

This verifies expiration and scheduled deletion of expired rows. Restore defaults by restarting without those overrides. Standard TTLs are 15 minutes and 7 days; cleanup runs hourly.

### Security design decisions

- Authentication is HTTP-session stateless. JWT credentials are sent explicitly in the Authorization header, not browser-managed cookies, so CSRF is disabled. Roles and enabled status are retrieved from the database.
- A token's raw value is never persisted; only its UUID (`jti`), session ID (`sid`), type and timestamps are stored. Blacklist inserts use PostgreSQL `ON CONFLICT DO NOTHING` to handle repeated revocation without aborting the transaction. Account-level pessimistic locks serialize refresh, password change and account revocation operations. Reuse detection commits revocations via `noRollbackFor=UnauthorizedException.class`, even when the request returns 401.
- Session revocation is available to authenticated owners, not just administrators; the existing domain DELETE methods retain an additional ADMIN method guard.
- The Caffeine bonus caches only positive blacklist results for one minute and updates the cache after a successful commit. Negative results are never cached: a cached negative on one application instance could allow a token revoked by another instance to remain usable. Cache misses always consult the database.
- The account lockout bonus persists failed attempts and `locked_until`, returns ERR-2709 on the fifth failed attempt, and resets the count on successful login after the lockout window. This is an account-based educational lockout; production systems would also require rate limits and stronger abuse controls.
- `login_at` preserves the original session start across rotation. Existing demo/cache endpoints are ADMIN-only to prevent authenticated ordinary users from manipulating demo or cache state.
- Logging masks arbitrary controller/service arguments and does not print exception messages from security operations. Existing trace IDs and three-language message bundles are preserved.

### Branch submission

Required stacked branches: `feature/security-base` → `feature/security-errors` → `feature/jwt-filter` → `feature/token-blacklist` → `feature/auth-endpoints` → `feature/authorization` → `feature/security-swagger`. The final branch contains all features and both bonuses. The blacklist screenshot is generated using the final application (logout endpoint becomes available in the following branch) and attached to its dedicated PR. Each PR should target its preceding branch until that branch has been merged. The provided source has not been compiled or run in the preparation environment: Gradle wrapper distribution download was blocked by unavailable network access. Verify locally before submission.
