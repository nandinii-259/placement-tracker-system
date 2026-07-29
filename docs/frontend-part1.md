# Placement Tracker System — Frontend (Part 1: Login & Registration)

## 1. Objective

Set up the frontend project structure, configure CORS on the backend, and build fully working, tested Login and Registration pages -- the two entry points into the application before a user is authenticated.

## 2. Folder Structure

```
placement-tracker-frontend/
├── css/
│   └── style.css
├── js/
│   ├── login.js
│   └── register.js
├── login.html
└── register.html
```

HTML files live directly in the frontend root rather than in a separate `html/` subfolder, keeping relative links to `css/` and `js/` simple across the project's modest page count.

## 3. CORS Configuration

Since the frontend (served via VS Code Live Server at `http://127.0.0.1:5500`) and backend (`http://localhost:8080`) run on different origins, browsers block cross-origin JavaScript requests unless the server explicitly permits it.

Added to `SecurityConfig.java`:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```
Allowed origins are deliberately narrow (the specific Live Server address), not a wildcard, as a genuine security practice -- only the actual frontend built for this project is trusted to call the API with credentials.

## 4. Login Page

- **login.html**: form with email/password inputs, an error-message placeholder, and a link to registration.
- **login.js**:
  - Prevents default form-submit page reload
  - Sends `POST /api/auth/login` via `fetch()`, identical in shape to requests already verified in Postman
  - Displays the backend's error message directly on failure (reusing the consistent error shape from `GlobalExceptionHandler`)
  - On success, stores `token`, `email`, `role` in `localStorage`, then redirects to `admin-dashboard.html` or `student-dashboard.html` based on role

## 5. Registration Page

- **register.html**: form covering all fields required by `RegisterRequestDto` (full name, email, password, branch, CGPA, graduation year). Uses matching HTML-level validation where practical (`minlength="8"` on password, `min`/`max`/`step` on CGPA) mirroring backend validation rules -- providing immediate feedback while the backend remains the authoritative, unbypassable check.
- **register.js**:
  - Converts numeric field values from strings to actual numbers (`parseFloat`, `parseInt`) before sending, matching the backend's expected types
  - Sends `POST /api/auth/register` via `fetch()`
  - Handles two different error response shapes from the backend: a single string message (e.g. from `BusinessRuleException`) versus an array of messages (from `MethodArgumentNotValidException`), displaying either correctly
  - On success, shows a confirmation message and redirects to `login.html` after a short delay (`setTimeout`), giving the user time to read the message before navigating away

## 6. Verification Performed

- Login page: tested both incorrect credentials (error displayed correctly, no page reload) and correct credentials (confirmed via the expected "page not found" for the not-yet-built dashboard, proving the login and redirect logic executed correctly)
- Confirmed `token`, `email`, `role` correctly saved in `localStorage` via browser DevTools after successful login
- Registration page: registered a genuinely new account through the UI, confirmed a success message and automatic redirect to the login page
- Confirmed the newly registered account could then log in successfully, closing the full registration -> login loop

This confirms the complete authentication frontend works end-to-end against the real backend, covering both new-user registration and returning-user login.

## 7. Next Steps (Phase 13 Part 2)

- Build the Student Dashboard
- Build the Jobs browsing page
- Establish a shared pattern for attaching the stored JWT `Authorization` header on authenticated requests from these and future pages