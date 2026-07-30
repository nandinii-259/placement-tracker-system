requireLogin();

let studentId = null;

async function loadStudentInfo() {
    const email = getEmail();

    const response = await authFetch(`${API_BASE_URL}/students/by-email?email=${encodeURIComponent(email)}`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load your profile.";
        return;
    }

    const student = await response.json();
    studentId = student.id;

    loadApplications();
}

async function loadApplications() {
    const response = await authFetch(`${API_BASE_URL}/applications/student/${studentId}`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load applications.";
        return;
    }

    const applications = await response.json();
    const list = document.getElementById("applicationsList");
    list.innerHTML = "";

    if (applications.length === 0) {
        list.innerHTML = "<p>You haven't applied to any jobs yet.</p>";
        return;
    }

    for (const app of applications) {
        const card = document.createElement("div");
        card.className = "application-card";

        let extraInfo = "";

        if (app.status === "REJECTED" && app.rejectionReason) {
            extraInfo = `<div class="rejection-note"><strong>Rejection reason:</strong> ${app.rejectionReason}</div>`;
        }

        if (["INTERVIEW_SCHEDULED", "SELECTED", "OFFERED"].includes(app.status)) {
            const interview = await fetchInterview(app.id);
            if (interview) {
                extraInfo += `<div class="interview-note">
                    <strong>Interview:</strong> ${new Date(interview.scheduledAt).toLocaleString()}
                    (${interview.mode}) ${interview.locationOrLink ? '- ' + interview.locationOrLink : ''}
                </div>`;
            }
        }

       if (app.status === "OFFERED") {
           const offer = await fetchOffer(app.id);
           if (offer) {
            extraInfo += `<div class="offer-note">
            <strong>Offer:</strong> ${offer.positionTitle}
            ${offer.salaryCtc ? ' - CTC: ' + offer.salaryCtc : ''}
            <br><a href="offer-letter.html?applicationId=${app.id}">View Offer Letter →</a>
        </div>`;
    }
}

        card.innerHTML = `
            <h3>${app.jobTitle}</h3>
            <p>${app.companyName}</p>
            <p>Applied on: ${new Date(app.appliedAt).toLocaleDateString()}</p>
            <span class="status-badge status-${app.status}">${app.status.replace(/_/g, ' ')}</span>
            ${extraInfo}
        `;

        list.appendChild(card);
    }
}

async function fetchInterview(applicationId) {
    try {
        const response = await authFetch(`${API_BASE_URL}/interviews/application/${applicationId}`);
        if (!response.ok) return null;
        return await response.json();
    } catch (error) {
        return null;
    }
}

async function fetchOffer(applicationId) {
    try {
        const response = await authFetch(`${API_BASE_URL}/offers/application/${applicationId}`);
        if (!response.ok) return null;
        return await response.json();
    } catch (error) {
        return null;
    }
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadStudentInfo();