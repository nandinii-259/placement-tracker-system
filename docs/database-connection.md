# Placement Tracker System — Database Connection

## 1. Objective

Connect the Spring Boot backend to the real MySQL database (`placement_tracker_db`) created in Phase 5, while keeping database credentials out of the public GitHub repository.

## 2. Configuration Files

Two properties files are used:

### `src/main/resources/application.properties` (committed to Git — no secrets)

```properties
spring.application.name=placement-tracker-backend
spring.config.import=optional:application-local.properties

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### `src/main/resources/application-local.properties` (NOT committed — listed in .gitignore)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/placement_tracker_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 3. Why the Configuration Is Split Into Two Files

Database credentials (username, password, connection URL) are sensitive information. Since this repository is public on GitHub, committing real credentials would expose them to anyone. The configuration is split so that:

- `application.properties` contains only safe, shareable behavior settings and is committed normally.
- `application-local.properties` contains the actual connection details and is excluded from Git via `.gitignore` (already configured in Phase 1).
- `spring.config.import=optional:application-local.properties` tells Spring Boot to load the second file if it exists, and silently skip it if it doesn't (the `optional:` prefix). This means each developer (or anyone cloning the repo) creates their own local file with their own credentials, and the application still runs correctly without ever exposing anyone's real password in the shared codebase.

## 4. Explanation of Each Property

| Property | Meaning |
|---|---|
| `spring.datasource.url` | The full address of the database: protocol (`jdbc:mysql://`), host and port (`localhost:3306`), and database name (`placement_tracker_db`) |
| `spring.datasource.username` / `password` | Credentials used to authenticate with MySQL |
| `spring.datasource.driver-class-name` | Tells Spring Boot which driver class to use to communicate with MySQL specifically |
| `spring.jpa.hibernate.ddl-auto=validate` | Deliberately set to `validate` rather than `update` or `create` — JPA will check that the Java entity mappings match the existing hand-designed schema, but will never automatically create, alter, or drop tables. This protects the schema designed and built in Phases 3 and 5 from being silently modified by application code. |
| `spring.jpa.show-sql=true` | Prints the actual SQL statements Hibernate generates to the console — useful for learning and debugging |
| `spring.jpa.properties.hibernate.dialect` | Tells Hibernate to generate SQL using MySQL's specific syntax dialect |

## 5. Challenge Encountered and Resolved

On first connection attempt, the application failed with:
```
Access denied for user 'root'@'localhost' (using password: NO)
```

**Cause:** The MySQL root account's actual password was unknown (it had been auto-saved by MySQL Workbench and never noted down). An initial attempt with a blank password also failed, confirming a real password was set.

**Resolution:** The root password was reset via MySQL Workbench (Server -> Users and Privileges -> root -> set new password), and the new password was used in `application-local.properties`. The application then connected successfully.

## 6. Verification Performed

Application log confirmed successful connection:
```
HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@...
HikariPool-1 - Start completed.
Database JDBC URL [jdbc:mysql://localhost:3306/placement_tracker_db]
Started PlacementTrackerBackendApplication in ~13 seconds
```

Confirmed via `git status` that `application-local.properties` does not appear as a tracked or staged file, verifying `.gitignore` correctly excludes it from version control.