# Placement Tracker System — Spring Boot Project Setup

## 1. Tooling Used

- **IntelliJ IDEA Community Edition** (2025.1.4.1) — free edition, used for all backend development
- **Spring Initializr** (start.spring.io) — used to generate the initial project skeleton, since IntelliJ Community Edition does not include the built-in Spring Boot project wizard (that is an Ultimate Edition-only feature). Spring Initializr produces an identical result; it is what the Ultimate wizard calls internally.

## 2. Project Configuration

| Setting | Value | Why |
|---|---|---|
| Build tool | Maven | Simpler XML-based configuration, most common in tutorials and industry, easier to get help with as a beginner |
| Language | Java | Per project tech stack |
| Spring Boot version | 4.1.0 (stable, non-SNAPSHOT) | SNAPSHOT versions are unfinished/unstable; a stable release was chosen deliberately |
| Group | com.placementtracker | Project namespace |
| Artifact | placement-tracker-backend | Project/folder name |
| Packaging | Jar | Standard packaging for a Spring Boot web application |
| Java version | 17 | Stable, widely-supported LTS version |

## 3. Dependencies Added

| Dependency | Purpose |
|---|---|
| Spring Web | Enables the application to receive HTTP requests and send responses; foundation for the Controller layer |
| Spring Data JPA | Enables Java code to talk to a relational database using Java objects instead of raw SQL; foundation for the Repository layer |
| MySQL Driver | The specific "translator" that allows Spring Data JPA to communicate with MySQL specifically |
| Validation | Provides reusable annotations to validate incoming data on DTOs (e.g., not-blank, not-negative) |

**Deliberately NOT added yet: Spring Security.** Adding it this early would lock down every endpoint by default, including ones not yet built, making it impossible to test anything. Spring Security will be added in Phase 12, when authentication and authorization are actually implemented.

## 4. Project Structure Generated

```
placement-tracker-backend/
├── .idea/                  (IntelliJ project metadata)
├── .mvn/                   (Maven wrapper files)
├── src/
│   ├── main/
│   │   ├── java/com/placementtracker/placement_tracker_backend/
│   │   │   └── PlacementTrackerBackendApplication.java   (entry point)
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties   (currently empty — filled in Phase 7)
│   └── test/
├── pom.xml                 (Maven configuration listing dependencies)
└── mvnw / mvnw.cmd          (Maven wrapper scripts)
```

## 5. The Application Entry Point

```java
package com.placementtracker.placement_tracker_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlacementTrackerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementTrackerBackendApplication.class, args);
    }

}
```

- `@SpringBootApplication` marks this class as the application's main configuration point, enables Spring Boot's auto-configuration, and tells Spring Boot to automatically scan this package (and sub-packages) for future Controllers, Services, and Repositories.
- `main(String[] args)` is the standard Java entry point — the method that runs first when the program starts.
- `SpringApplication.run(...)` boots the internal web server and wires together the application.

## 6. First Run — Result and Diagnosis

The application was run for the first time and **failed to start**, with the following error:

```
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
Reason: Failed to determine a suitable driver class
```

**Root cause:** The Spring Data JPA and MySQL Driver dependencies cause Spring Boot to expect database connection details immediately on startup. Since `application.properties` had not yet been filled in with a database URL, username, and password, Spring Boot had no way to establish a connection and failed fast.

**This is expected at this exact stage** — it is not a bug in the project setup. Database connection configuration is a deliberate, separate phase (Phase 7) rather than something bundled into initial project creation. This error will be resolved as the first task of Phase 7.

## 7. Verification Performed

- Confirmed project structure matches Spring Initializr's standard Maven layout
- Confirmed all 4 dependencies downloaded successfully (visible under "External Libraries" in IntelliJ)
- Confirmed the entry point file compiles and is recognized as runnable (green run icon present)
- Ran the application and correctly diagnosed the resulting startup failure as a missing database configuration, not a project setup error