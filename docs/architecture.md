# Placement Tracker System – System Architecture

## 1. Overview

The Placement Tracker System follows a **Client-Server Architecture**.

The application is divided into three main parts:

### Frontend (Browser)

The frontend is the part that users interact with. It is built using:

- HTML
- CSS
- JavaScript

It displays pages such as:

- Login
- Register
- Dashboard
- Jobs
- Profile
- Companies

The frontend **cannot access the database directly**. Whenever it needs data, it sends an HTTP request to the backend.

---

### Backend (Spring Boot)

The backend is the brain of the application.

It is responsible for:

- Receiving requests from the frontend
- Validating requests
- Applying business rules
- Communicating with the database
- Sending responses back to the frontend

Only the backend can access the database.

---

### Database (MySQL)

MySQL stores all permanent data used by the system, including:

- Students
- Companies
- Jobs
- Applications
- Interviews
- Offers

---

## 2. Request Flow

Whenever a user performs an action, the request follows this path:

```text
Browser (Frontend)
        |
        | HTTP Request (JSON)
        v
Controller Layer
        |
        v
Service Layer
        |
        v
Repository Layer
        |
        | SQL
        v
MySQL Database
```

The response then travels back through the same layers in reverse order.

---

# 3. Backend Architecture

The Spring Boot backend follows a **Layered Architecture**.

Each layer has a single responsibility.

## Controller Layer

The Controller is the entry point of the backend.

Responsibilities:

- Receives HTTP requests
- Accepts request data
- Calls the appropriate service
- Returns HTTP responses

The Controller **does not contain business logic**.

---

## Service Layer

The Service layer contains all business logic.

Responsibilities:

- Validate business rules
- Process application logic
- Coordinate multiple repositories
- Decide whether an operation is allowed

Examples:

- Check if a student is eligible
- Verify application deadlines
- Prevent duplicate applications
- Validate profile completion

---

## Repository Layer

The Repository layer communicates with the database.

Responsibilities:

- Retrieve data
- Save data
- Update data
- Delete data

It contains **database operations only** and no business logic.

---

## Entity Layer

Entities represent database tables.

Each Entity is a Java class that maps to one table in MySQL.

Example:

- Student → students table
- Company → companies table
- Job → jobs table

---

## DTO Layer

DTO (Data Transfer Object) defines the data exchanged between the frontend and backend.

Instead of exposing complete database entities, DTOs allow the application to send only the required information.

Example:

The Student table contains a password, but the password should never be returned to the frontend.

---

## Config Layer

This layer stores application configuration.

Examples:

- Database configuration
- Spring Security configuration
- JWT configuration
- CORS configuration

---

## Exception Layer

The Exception layer handles application errors in a consistent way.

Instead of returning confusing server errors, the application returns meaningful messages such as:

- Student already applied
- Company not found
- Invalid credentials
- Unauthorized access

---

# 4. Why Layered Architecture?

The application is divided into layers to keep the code clean and organized.

Benefits:

- Easier to understand
- Easier to maintain
- Easier to test
- Easier to add new features
- Better separation of responsibilities

This structure is widely used in real-world Spring Boot applications.

---

# 5. Why REST API?

The frontend and backend communicate using REST APIs.

The frontend sends HTTP requests, and the backend returns JSON responses.

This approach allows the same backend to support:

- Web applications
- Mobile applications
- Desktop applications

without changing the backend logic.

---

# 6. Why MySQL?

The Placement Tracker System manages highly related data.

Examples:

- Students apply for Jobs.
- Jobs belong to Companies.
- Applications belong to Students.

A relational database like MySQL is well suited for these relationships.

It also provides:

- Foreign Keys
- Unique Constraints
- Data Integrity
- Reliable transactions

---

# 7. Example Request Flow – Student Applies for a Job

When a student clicks the **Apply** button:

### Step 1

The frontend sends:

```http
POST /api/applications
```

along with:

- Job ID
- Student authentication token

---

### Step 2

The Controller receives the request and forwards it to the Service layer.

---

### Step 3

The Service validates the request.

It checks:

- Is the student logged in?
- Is the profile complete?
- Is the student eligible?
- Has the student already applied?
- Has the application deadline passed?

If all validations pass, the Service creates a new application.

---

### Step 4

The Repository saves the application into the database.

---

### Step 5

The Database stores the record.

A UNIQUE constraint also prevents duplicate applications, providing an additional layer of protection.

---

### Step 6

The success response travels back through:

```text
Database
    ↑
Repository
    ↑
Service
    ↑
Controller
    ↑
Browser
```

The student sees:

> Application Submitted Successfully

---

# 8. Security Overview

The application uses two levels of security.

## Authentication

Authentication answers:

**Who are you?**

Users log in using their credentials.

After successful login, the backend issues a JWT token.

The frontend sends this token with every future request.

---

## Authorization

Authorization answers:

**What are you allowed to do?**

Examples:

Student:

- View jobs
- Apply for jobs
- Update profile

Admin:

- Manage companies
- Create jobs
- Schedule interviews
- Manage applications

The backend verifies permissions on every request.

Even if someone bypasses the frontend using Postman or browser developer tools, unauthorized requests are rejected.

---

# 9. Project Structure

## Backend

```text
placement-tracker-backend/
│
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── config/
└── exception/
```

## Frontend

```text
placement-tracker-frontend/
│
├── login.html
├── register.html
├── dashboard.html
├── jobs.html
├── applications.html
├── css/
└── js/
```

---

# 10. Common Mistakes to Avoid

- Putting business logic inside the Controller.
- Skipping the Service layer for simple features.
- Trusting only the frontend for security.
- Exposing Entity classes directly through APIs.
- Confusing Authentication with Authorization.

---

# Summary

The Placement Tracker System follows a clean layered architecture where:

- **Frontend** provides the user interface.
- **Controller** receives requests.
- **Service** contains business logic.
- **Repository** communicates with the database.
- **MySQL** stores application data.

This architecture keeps the project modular, secure, maintainable, and aligned with industry best practices.