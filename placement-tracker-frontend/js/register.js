const API_BASE_URL = "http://localhost:8080/api";

document.getElementById("registerForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const fullName = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const branch = document.getElementById("branch").value;
    const cgpa = parseFloat(document.getElementById("cgpa").value);
    const graduationYear = parseInt(document.getElementById("graduationYear").value);

    const errorMessage = document.getElementById("errorMessage");
    const successMessage = document.getElementById("successMessage");

    errorMessage.textContent = "";
    successMessage.textContent = "";

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ fullName, email, password, branch, cgpa, graduationYear })
        });

        const data = await response.json();

        if (!response.ok) {
            if (Array.isArray(data.message)) {
                errorMessage.textContent = data.message.join(", ");
            } else {
                errorMessage.textContent = data.message || "Registration failed. Please try again.";
            }
            return;
        }

        successMessage.textContent = "Registration successful! Redirecting to login...";

        setTimeout(() => {
            window.location.href = "login.html";
        }, 2000);

    } catch (error) {
        errorMessage.textContent = "Could not connect to the server. Please try again.";
    }
});