requireLogin();

async function loadStats() {
    try {
        const companiesResponse = await authFetch(`${API_BASE_URL}/companies`);
        const jobsResponse = await authFetch(`${API_BASE_URL}/jobs`);

        if (companiesResponse.ok) {
            const companies = await companiesResponse.json();
            document.getElementById("totalCompanies").textContent = companies.length;
        }

        if (jobsResponse.ok) {
            const jobs = await jobsResponse.json();
            document.getElementById("totalJobs").textContent = jobs.length;
        }

    } catch (error) {
        document.getElementById("statusMessage").textContent = "Could not load dashboard data.";
    }
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadStats();