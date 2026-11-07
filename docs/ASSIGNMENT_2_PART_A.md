# Assignment 2 — Part A installation and verification

This is an **overlay**, not a new project. Copy the contents of this folder into the existing `school-api` project on branch `feature/liquibase-cache-tx`.

## Install

```bash
git switch main
git pull origin main
git switch -c feature/liquibase-cache-tx
```

Copy overlay files into your existing project, preserving the directory hierarchy. The `build.gradle`, `application.yaml`, and `Course.java` files intentionally replace the corresponding Assignment 1 files; review your local changes before overwriting.

**Destructive, dev-only reset** (deletes the project's PostgreSQL volume):

```bash
docker compose down -v
docker compose up -d
./gradlew bootRun
```

Gradle uses Java 25 and PostgreSQL `localhost:5433` in the existing project. `ddl-auto` is now `validate`, not `update`.

## Inspect databasechangelog

```sql
SELECT id, author, filename, contexts, tag, exectype
FROM databasechangelog
ORDER BY orderexecuted;
```

The `100-*` changesets should have `contexts=dev`. `005-1` should have `tag=v1.0`. The development seeds provide 3 departments, 10 employees, 5 courses, 10 students, 5 authors, and 8 books; Demo Course 1 has capacity 1 for the isolation demos.

## Gradle plugin commands

With the app stopped, and Docker PostgreSQL running:

```bash
./gradlew status
./gradlew rollback -PliquibaseTag=v1.0
# Verify capacity column and audit_logs table are gone, then:
./gradlew update
```

If your plugin/Liquibase version requires the `tag` value in its activity rather than as a command-line property, add `tag 'v1.0'` to the activity just for the rollback command, and remove it afterward. Always run rollback when the application is not running; otherwise application startup can immediately reapply migrations.

## Checksum verification screenshot

1. Run the app successfully and stop it.
2. Temporarily modify an *already executed* changeset, e.g. change `VARCHAR(50)` to `VARCHAR(51)` in `001-create-users-passports.yaml`.
3. Start the app. Capture Liquibase's checksum validation failure.
4. Restore the original changelog from Git before proceeding. Never commit the modified executed changeset.

## Hibernate validate verification screenshot

1. Run the app successfully and stop it.
2. Temporarily change `Course.title` to `@Column(name = "does_not_exist", nullable = false, length = 100)`.
3. Start the app and capture the validation failure.
4. Restore the entity mapping.

## Rollback verification

Before rollback, run:

```sql
SELECT column_name FROM information_schema.columns
WHERE table_name='courses' AND column_name='capacity';
SELECT to_regclass('public.audit_logs');
```

After `rollback v1.0`, both should be absent. After `update`, both should be present again.

## README concepts

`databasechangeloglock` serializes Liquibase migration execution so that two application instances cannot modify the schema concurrently. Liquibase acquires the lock during an update and releases it after migrations finish.

`ddl-auto: update` is dangerous in team development because Hibernate applies implicit schema mutations without reviewed, ordered, reversible migration history. Liquibase stores an explicit sequence of changesets, checksums, contexts, tags and rollback instructions, making database evolution auditable and repeatable.

## Important

This package covers **Part A** only. Part B cache annotations/`ehcache.xml`, Part C services, Part D concurrency controllers/scripts, the enhanced Postman collection, and all required empirical screenshots remain to be implemented and verified on a running machine. The source files were validated as YAML but the Java app was not built or executed here because this runtime lacks the project's Java 25 toolchain, Gradle and Docker.
