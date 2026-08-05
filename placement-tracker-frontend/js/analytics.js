requireLogin();

async function loadAnalytics() {
    showLoading("statsCards", "Loading analytics...");

    const response = await authFetch(`${API_BASE_URL}/analytics`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load analytics.";
        return;
    }

    const data = await response.json();
    renderStats(data);
    renderCompanyOffers(data.offersByCompany);
}

function renderStats(data) {
    const cardsContainer = document.getElementById("statsCards");

    cardsContainer.innerHTML = `
        <div class="card">
            <h3>${data.totalStudents}</h3>
            <p>Total Students</p>
        </div>
        <div class="card">
            <h3>${data.totalCompanies}</h3>
            <p>Companies</p>
        </div>
        <div class="card">
            <h3>${data.totalJobs}</h3>
            <p>Total Jobs</p>
        </div>
        <div class="card">
            <h3>${data.totalApplications}</h3>
            <p>Applications</p>
        </div>
        <div class="card">
            <h3>${data.studentsPlaced}</h3>
            <p>Students Placed</p>
        </div>
        <div class="card">
            <h3>${data.placementRate}%</h3>
            <p>Placement Rate</p>
        </div>
    `;
}

function renderCompanyOffers(offersByCompany) {
    const list = document.getElementById("companyOffersList");
    list.innerHTML = "";

    if (offersByCompany.length === 0) {
        list.innerHTML = "<p>No offers have been made yet.</p>";
        return;
    }

    offersByCompany.forEach(entry => {
        const card = document.createElement("div");
        card.className = "application-card";
        card.innerHTML = `
            <h3>${entry.companyName}</h3>
            <p>${entry.offerCount} offer${entry.offerCount === 1 ? '' : 's'} made</p>
        `;
        list.appendChild(card);
    });
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadAnalytics();