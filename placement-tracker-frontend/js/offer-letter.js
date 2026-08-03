requireLogin();

function getQueryParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}

async function loadOfferLetter() {
    const applicationId = getQueryParam("applicationId");
    const letterContent = document.getElementById("letterContent");

    if (!applicationId) {
        letterContent.innerHTML = "<p>No application specified.</p>";
        return;
    }

    try {
        const offerResponse = await authFetch(`${API_BASE_URL}/offers/application/${applicationId}`);
        const appResponse = await authFetch(`${API_BASE_URL}/applications/${applicationId}`);

        if (!offerResponse.ok || !appResponse.ok) {
            letterContent.innerHTML = "<p>Could not load offer letter.</p>";
            return;
        }

        const offer = await offerResponse.json();
        const application = await appResponse.json();

        letterContent.innerHTML = `
            <div class="letter-seal">Placement Cell</div>
            <h2>Letter of Offer</h2>
            <p class="letter-date">${new Date(offer.offerDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</p>

            <p class="letter-body">Dear <strong>${application.studentName}</strong>,</p>

            <p class="letter-body">
                We are pleased to offer you the position of <strong>${offer.positionTitle}</strong>
                at <strong>${application.companyName}</strong>, following your successful application
                and interview process through the college placement program.
            </p>

            ${offer.salaryCtc ? `<p class="letter-body">Your annual compensation (CTC) will be <strong>${formatCurrency(offer.salaryCtc)}</strong>.</p>` : ''}
            <p class="letter-body">
                We look forward to welcoming you to the team. Congratulations on this achievement.
            </p>

            <div class="letter-signature">
                <p>Sincerely,</p>
                <p class="letter-signature-line">Placement Cell</p>
            </div>
        `;

    } catch (error) {
        letterContent.innerHTML = "<p>Could not connect to the server.</p>";
    }
}

document.getElementById("logoutLink")?.addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadOfferLetter();