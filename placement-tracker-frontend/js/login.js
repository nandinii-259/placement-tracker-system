const API_BASE_URL = "http://localhost:8080/api";

document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMessage = document.getElementById("errorMessage");

    errorMessage.textContent = "";

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            errorMessage.textContent = data.message || "Login failed. Please try again.";
            return;
        }

        localStorage.setItem("token", data.token);
        localStorage.setItem("email", data.email);
        localStorage.setItem("role", data.role);

        if (data.role === "ADMIN") {
            window.location.href = "admin-dashboard.html";
        } else {
            window.location.href = "student-dashboard.html";
        }

    } catch (error) {
        errorMessage.textContent = "Could not connect to the server. Please try again.";
    }
});