requireLogin();

let currentStudentId = null;

async function loadStudentProfile() {
    const email = getEmail();

    try {
        const response = await authFetch(`${API_BASE_URL}/students/by-email?email=${encodeURIComponent(email)}`);

        if (!response.ok) {
            console.error("Could not load student profile.");
            return;
        }

        const student = await response.json();
        currentStudentId = student.id;

        document.getElementById("studentName").textContent = student.fullName;
        document.getElementById("studentBranch").textContent = student.branch;
        document.getElementById("studentCgpa").textContent = student.cgpa;

        loadApplicationStats(currentStudentId);

    } catch (error) {
        console.error("Error loading profile:", error);
    }
}

async function loadApplicationStats(studentId) {
    try {
        const response = await authFetch(`${API_BASE_URL}/applications/student/${studentId}`);

        if (!response.ok) return;

        const applications = await response.json();

        document.getElementById("totalApplications").textContent = applications.length;

        const interviewCount = applications.filter(
            app => ["INTERVIEW_SCHEDULED", "SELECTED", "OFFERED"].includes(app.status)
        ).length;
        document.getElementById("totalInterviews").textContent = interviewCount;

        const offerCount = applications.filter(app => app.status === "OFFERED").length;
        document.getElementById("totalOffers").textContent = offerCount;

    } catch (error) {
        console.error("Error loading applications:", error);
    }
}

document.getElementById("logoutLink").addEventListener("click", function (event) {
    event.preventDefault();
    logout();
});

loadStudentProfile();