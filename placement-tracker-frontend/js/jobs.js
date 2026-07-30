requireLogin();

let studentId = null;
let studentCgpa = null;
let appliedJobIds = new Set();

async function loadStudentInfo() {
    const email = getEmail();

    const response = await authFetch(`${API_BASE_URL}/students/by-email?email=${encodeURIComponent(email)}`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load your profile.";
        return;
    }

    const student = await response.json();
    studentId = student.id;
    studentCgpa = student.cgpa;

    await loadAppliedJobs();
    loadJobs();
}

async function loadAppliedJobs() {
    const response = await authFetch(`${API_BASE_URL}/applications/student/${studentId}`);
    if (!response.ok) return;

    const applications = await response.json();
    appliedJobIds = new Set(applications.map(app => app.jobId));
}

async function loadJobs() {
    const response = await authFetch(`${API_BASE_URL}/jobs`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load jobs.";
        return;
    }

    const jobs = await response.json();
    const jobsList = document.getElementById("jobsList");
    jobsList.innerHTML = "";

    if (jobs.length === 0) {
        jobsList.innerHTML = "<p>No jobs available right now.</p>";
        return;
    }

    jobs.forEach(job => {
        const alreadyApplied = appliedJobIds.has(job.id);
        const isEligible = studentCgpa >= job.minCgpa;
        const deadlinePassed = new Date(job.applicationDeadline) < new Date();
        const canApply = isEligible && !deadlinePassed && !alreadyApplied;

        let buttonLabel = "Apply";
        let buttonClass = "";
        if (alreadyApplied) {
            buttonLabel = "Applied";
            buttonClass = "applied-button";
        } else if (deadlinePassed) {
            buttonLabel = "Deadline Passed";
        }

        const card = document.createElement("div");
        card.className = "job-card";

        card.innerHTML = `
            <div class="job-info">
                <h3>${job.title}</h3>
                <p>${job.companyName}</p>
                <p>Minimum CGPA: ${job.minCgpa} | Deadline: ${job.applicationDeadline}</p>
                <span class="eligibility-badge ${isEligible ? 'eligible' : 'not-eligible'}">
                    ${isEligible ? 'Eligible' : 'Not Eligible'}
                </span>
            </div>
            <div class="job-actions">
                <button class="${buttonClass}" ${canApply ? '' : 'disabled'} onclick="applyToJob(${job.id}, this)">
                    ${buttonLabel}
                </button>
            </div>
        `;

        jobsList.appendChild(card);
    });
}

async function applyToJob(jobId, button) {
    const statusMessage = document.getElementById("statusMessage");
    statusMessage.textContent = "";

    try {
        const response = await authFetch(`${API_BASE_URL}/applications/student/${studentId}`, {
            method: "POST",
            body: JSON.stringify({ jobId })
        });

        const data = await response.json();

        if (!response.ok) {
            statusMessage.textContent = data.message || "Could not apply to this job.";
            return;
        }

        statusMessage.style.color = "#3f7a5c";
        statusMessage.textContent = `Successfully applied to ${data.jobTitle}!`;

        button.textContent = "Applied";
        button.disabled = true;
        button.classList.add("applied-button");
        appliedJobIds.add(jobId);

    } catch (error) {
        statusMessage.textContent = "Could not connect to the server.";
    }
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadStudentInfo();