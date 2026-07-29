const API_BASE_URL = "http://localhost:8080/api";

function getToken() {
    return localStorage.getItem("token");
}

function getRole() {
    return localStorage.getItem("role");
}

function getEmail() {
    return localStorage.getItem("email");
}

function requireLogin() {
    if (!getToken()) {
        window.location.href = "login.html";
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    window.location.href = "login.html";
}

function authFetch(url, options = {}) {
    const token = getToken();

    const headers = {
        ...(options.headers || {}),
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };

    return fetch(url, { ...options, headers });
}