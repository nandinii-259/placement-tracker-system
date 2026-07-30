requireLogin();

async function loadCompaniesDropdown() {
    const response = await authFetch(`${API_BASE_URL}/companies`);
    if (!response.ok) return;

    const companies = await response.json();
    const select = document.getElementById("companySelect");
    select.innerHTML = "";

    companies.forEach(company => {
        const option = document.createElement("option");
        option.value = company.id;
        option.textContent = company.name;
        select.appendChild(option);
    });
}

async function loadJobs() {
    const response = await authFetch(`${API_BASE_URL}/jobs`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load jobs.";
        return;
    }

    const jobs = await response.json();
    const list = document.getElementById("jobsList");
    list.innerHTML = "";

    if (jobs.length === 0) {
        list.innerHTML = "<p>No jobs posted yet.</p>";
        return;
    }

    jobs.forEach(job => {
        const card = document.createElement("div");
        card.className = "application-card";

        card.innerHTML = `
            <h3>${job.title}</h3>
            <p>${job.companyName}</p>
            <p>Min CGPA: ${job.minCgpa} | Deadline: ${job.applicationDeadline}</p>
            <button class="delete-button" onclick="deleteJob(${job.id})">Delete</button>
        `;

        list.appendChild(card);
    });
}

document.getElementById("jobForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const companyId = document.getElementById("companySelect").value;
    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const minCgpa = parseFloat(document.getElementById("minCgpa").value);
    const applicationDeadline = document.getElementById("deadline").value;

    const statusMessage = document.getElementById("statusMessage");
    statusMessage.textContent = "";

    try {
        const response = await authFetch(`${API_BASE_URL}/jobs/company/${companyId}`, {
            method: "POST",
            body: JSON.stringify({ title, description, minCgpa, applicationDeadline })
        });

        const data = await response.json();

        if (!response.ok) {
            statusMessage.textContent = Array.isArray(data.message) ? data.message.join(", ") : data.message;
            return;
        }

        document.getElementById("jobForm").reset();
        loadJobs();

    } catch (error) {
        statusMessage.textContent = "Could not connect to the server.";
    }
});

async function deleteJob(id) {
    if (!confirm("Are you sure you want to delete this job?")) return;

    try {
        const response = await authFetch(`${API_BASE_URL}/jobs/${id}`, { method: "DELETE" });

        if (response.ok) {
            loadJobs();
        } else {
            document.getElementById("statusMessage").textContent = "Could not delete job.";
        }
    } catch (error) {
        document.getElementById("statusMessage").textContent = "Could not connect to the server.";
    }
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadCompaniesDropdown();
loadJobs();