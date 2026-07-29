requireLogin();

async function loadCompanies() {
    const response = await authFetch(`${API_BASE_URL}/companies`);

    if (!response.ok) {
        document.getElementById("statusMessage").textContent = "Could not load companies.";
        return;
    }

    const companies = await response.json();
    const list = document.getElementById("companiesList");
    list.innerHTML = "";

    if (companies.length === 0) {
        list.innerHTML = "<p>No companies added yet.</p>";
        return;
    }

    companies.forEach(company => {
        const card = document.createElement("div");
        card.className = "application-card";

        card.innerHTML = `
            <h3>${company.name}</h3>
            <p>${company.description || "No description"}</p>
            <p>${company.website || ""}</p>
            <button class="delete-button" onclick="deleteCompany(${company.id})">Delete</button>
        `;

        list.appendChild(card);
    });
}

document.getElementById("companyForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const name = document.getElementById("name").value;
    const description = document.getElementById("description").value;
    const website = document.getElementById("website").value;

    const statusMessage = document.getElementById("statusMessage");
    statusMessage.textContent = "";

    try {
        const response = await authFetch(`${API_BASE_URL}/companies`, {
            method: "POST",
            body: JSON.stringify({ name, description, website })
        });

        const data = await response.json();

        if (!response.ok) {
            statusMessage.textContent = Array.isArray(data.message) ? data.message.join(", ") : data.message;
            return;
        }

        document.getElementById("companyForm").reset();
        loadCompanies();

    } catch (error) {
        statusMessage.textContent = "Could not connect to the server.";
    }
});

async function deleteCompany(id) {
    if (!confirm("Are you sure you want to delete this company?")) return;

    try {
        const response = await authFetch(`${API_BASE_URL}/companies/${id}`, {
            method: "DELETE"
        });

        if (response.ok) {
            loadCompanies();
        } else {
            document.getElementById("statusMessage").textContent = "Could not delete company.";
        }
    } catch (error) {
        document.getElementById("statusMessage").textContent = "Could not connect to the server.";
    }
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadCompanies();