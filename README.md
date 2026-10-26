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
