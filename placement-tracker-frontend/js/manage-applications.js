requireLogin();

const VALID_TRANSITIONS = {
    APPLIED: ["UNDER_REVIEW"],
    UNDER_REVIEW: ["SHORTLISTED", "REJECTED"],
    SHORTLISTED: ["INTERVIEW_SCHEDULED", "REJECTED"],
    INTERVIEW_SCHEDULED: ["SELECTED", "REJECTED"],
    SELECTED: ["OFFERED"]
};

async function loadApplicationsByJob(jobId) {
    const statusMessage = document.getElementById("statusMessage");
    statusMessage.textContent = "";

    const response = await authFetch(`${API_BASE_URL}/applications/job/${jobId}`);

    if (!response.ok) {
        statusMessage.textContent = "Could not load applications for this job.";
        return;
    }

    const applications = await response.json();
    const list = document.getElementById("applicationsList");
    list.innerHTML = "";

    if (applications.length === 0) {
        list.innerHTML = "<p>No applications found for this job.</p>";
        return;
    }

    applications.forEach(app => {
        const card = document.createElement("div");
        card.className = "application-card";

        const nextStatuses = VALID_TRANSITIONS[app.status] || [];

        let actionsHtml = "";

        nextStatuses.forEach(nextStatus => {
            if (nextStatus === "REJECTED") {
                actionsHtml += `<button onclick="rejectApplication(${app.id})">Reject</button> `;
            } else if (nextStatus === "INTERVIEW_SCHEDULED") {
                actionsHtml += `<button onclick="showScheduleForm(${app.id})">Schedule Interview</button> `;
            } else if (nextStatus === "OFFERED") {
                actionsHtml += `<button onclick="showOfferForm(${app.id})">Create Offer</button> `;
            } else {
                actionsHtml += `<button onclick="updateStatus(${app.id}, '${nextStatus}')">Move to ${nextStatus.replace(/_/g, ' ')}</button> `;
            }
        });

        card.innerHTML = `
            <h3>${app.studentName}</h3>
            <p>${app.jobTitle} — ${app.companyName}</p>
            <span class="status-badge status-${app.status}">${app.status.replace(/_/g, ' ')}</span>
            <div style="margin-top:12px;">${actionsHtml}</div>
            <div id="extra-form-${app.id}"></div>
        `;

        list.appendChild(card);
    });
}

async function updateStatus(applicationId, newStatus, rejectionReason) {
    const statusMessage = document.getElementById("statusMessage");
    statusMessage.textContent = "";

    try {
        const response = await authFetch(`${API_BASE_URL}/applications/${applicationId}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status: newStatus, rejectionReason: rejectionReason || null })
        });

        const data = await response.json();

        if (!response.ok) {
            statusMessage.textContent = data.message || "Could not update status.";
            return;
        }

        const jobId = document.getElementById("jobFilter").value;
        loadApplicationsByJob(jobId);

    } catch (error) {
        statusMessage.textContent = "Could not connect to the server.";
    }
}

function rejectApplication(applicationId) {
    const reason = prompt("Enter a rejection reason:");
    if (reason === null) return;
    if (reason.trim() === "") {
        alert("A rejection reason is required.");
        return;
    }
    updateStatus(applicationId, "REJECTED", reason);
}

function showScheduleForm(applicationId) {
    const container = document.getElementById(`extra-form-${applicationId}`);
    container.innerHTML = `
        <div style="margin-top:10px;">
            <input type="datetime-local" id="scheduledAt-${applicationId}">
            <select id="mode-${applicationId}">
                <option value="ONLINE">Online</option>
                <option value="OFFLINE">Offline</option>
            </select>
            <input type="text" id="location-${applicationId}" placeholder="Location or link">
            <button onclick="submitInterview(${applicationId})">Confirm Schedule</button>
        </div>
    `;
}

async function submitInterview(applicationId) {
    const scheduledAt = document.getElementById(`scheduledAt-${applicationId}`).value;
    const mode = document.getElementById(`mode-${applicationId}`).value;
    const locationOrLink = document.getElementById(`location-${applicationId}`).value;

    const statusMessage = document.getElementById("statusMessage");

    try {
        const response = await authFetch(`${API_BASE_URL}/interviews/application/${applicationId}`, {
            method: "POST",
            body: JSON.stringify({ scheduledAt, mode, locationOrLink })
        });

        const data = await response.json();

        if (!response.ok) {
            statusMessage.textContent = data.message || "Could not schedule interview.";
            return;
        }

        const jobId = document.getElementById("jobFilter").value;
        loadApplicationsByJob(jobId);

    } catch (error) {
        statusMessage.textContent = "Could not connect to the server.";
    }
}

function showOfferForm(applicationId) {
    const container = document.getElementById(`extra-form-${applicationId}`);
    container.innerHTML = `
        <div class="inline-form">
            <label>Position Title</label>
            <input type="text" id="position-${applicationId}" placeholder="e.g. Data Analyst">

            <label>Annual CTC (₹)</label>
            <input type="number" id="salary-${applicationId}" placeholder="e.g. 600000">

            <button onclick="submitOffer(${applicationId})">Confirm Offer</button>
        </div>
    `;
}

async function submitOffer(applicationId) {
    const positionTitle = document.getElementById(`position-${applicationId}`).value;
    const salaryCtc = parseFloat(document.getElementById(`salary-${applicationId}`).value) || null;

    const statusMessage = document.getElementById("statusMessage");

    try {
        const response = await authFetch(`${API_BASE_URL}/offers/application/${applicationId}`, {
            method: "POST",
            body: JSON.stringify({ positionTitle, salaryCtc })
        });

        const data = await response.json();

        if (!response.ok) {
            statusMessage.textContent = data.message || "Could not create offer.";
            return;
        }

        const jobId = document.getElementById("jobFilter").value;
        loadApplicationsByJob(jobId);

    } catch (error) {
        statusMessage.textContent = "Could not connect to the server.";
    }
}

document.getElementById("filterButton").addEventListener("click", function () {
    const jobId = document.getElementById("jobFilter").value;
    if (jobId) loadApplicationsByJob(jobId);
});

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});