/* =========================================
   EMS — Student Exams Page
   ========================================= */


/* -----------------------------------------
   Page Initialization
   ----------------------------------------- */

document.addEventListener("DOMContentLoaded", () => {
    initializeExamsPage();
});


function initializeExamsPage() {

    const searchInput =
        document.getElementById("exam-search");

    const statusFilter =
        document.getElementById("exam-status");


    if (!searchInput || !statusFilter) {
        return;
    }


    searchInput.addEventListener(
        "input",
        renderFilteredExams
    );


    statusFilter.addEventListener(
        "change",
        renderFilteredExams
    );


    renderFilteredExams();
}


/* -----------------------------------------
   Filter + Render
   ----------------------------------------- */

function renderFilteredExams() {

    const searchInput =
        document.getElementById("exam-search");

    const statusFilter =
        document.getElementById("exam-status");


    if (!searchInput || !statusFilter) {
        return;
    }


    const searchTerm =
        searchInput.value
            .trim()
            .toLowerCase();


    const selectedStatus =
        statusFilter.value;


    const filteredExams =
        MOCK_EXAMS.filter((exam) => {

            const matchesSearch =
                !searchTerm ||
                exam.title
                    .toLowerCase()
                    .includes(searchTerm) ||
                exam.code
                    .toLowerCase()
                    .includes(searchTerm) ||
                exam.semester
                    .toLowerCase()
                    .includes(searchTerm);


            const matchesStatus =
                selectedStatus === "ALL" ||
                exam.status === selectedStatus;


            return (
                matchesSearch &&
                matchesStatus
            );
        });


    renderExamSections(filteredExams);
}


/* -----------------------------------------
   Render Sections
   ----------------------------------------- */

function renderExamSections(exams) {

    const upcomingContainer =
        document.getElementById(
            "upcoming-exams-container"
        );


    const completedContainer =
        document.getElementById(
            "completed-exams-container"
        );


    const upcomingCount =
        document.getElementById(
            "upcoming-exams-count"
        );


    const completedCount =
        document.getElementById(
            "completed-exams-count"
        );


    if (
        !upcomingContainer ||
        !completedContainer
    ) {
        return;
    }


    const upcomingExams =
        exams.filter(
            (exam) => exam.status === "UPCOMING"
        );


    const completedExams =
        exams.filter(
            (exam) => exam.status === "COMPLETED"
        );


    upcomingContainer.innerHTML =
        upcomingExams.length
            ? upcomingExams
                .map(createExamCard)
                .join("")
            : createEmptyState(
                "No upcoming exams",
                "There are no upcoming examinations matching your search."
            );


    completedContainer.innerHTML =
        completedExams.length
            ? completedExams
                .map(createExamCard)
                .join("")
            : createEmptyState(
                "No completed exams",
                "No completed examinations match your current filter."
            );


    if (upcomingCount) {
        upcomingCount.textContent =
            `${upcomingExams.length} ${
                upcomingExams.length === 1
                    ? "examination"
                    : "examinations"
            }`;
    }


    if (completedCount) {
        completedCount.textContent =
            `${completedExams.length} ${
                completedExams.length === 1
                    ? "examination"
                    : "examinations"
            }`;
    }
}


/* -----------------------------------------
   Create Exam Card
   ----------------------------------------- */

function createExamCard(exam) {

    const isCompleted =
        exam.status === "COMPLETED";


    const statusClass =
        isCompleted
            ? "badge--success"
            : "badge--info";


    const statusLabel =
        isCompleted
            ? "Completed"
            : "Upcoming";


    const actionLabel =
        isCompleted
            ? "View Result"
            : "View Details";


    const actionHref =
        isCompleted
            ? "result-details.html?examId=" +
              encodeURIComponent(exam.id)
            : "exam-details.html?examId=" +
              encodeURIComponent(exam.id);


    const proctoringText =
        exam.proctored
            ? "Proctored examination"
            : "Standard examination";


    return `
        <article
            class="exam-card ${
                isCompleted
                    ? "exam-card--completed"
                    : ""
            }"
        >

            <div class="exam-card__body">

                <div class="exam-card__top">

                    <div class="exam-card__title-group">

                        <h3 class="exam-card__title">
                            ${escapeExamText(exam.title)}
                        </h3>

                        <div class="exam-card__code">
                            ${escapeExamText(exam.code)}
                        </div>

                    </div>

                    <span class="badge ${statusClass}">
                        ${statusLabel}
                    </span>

                </div>


                <p class="exam-card__subject">
                    ${escapeExamText(exam.semester)}
                </p>


                <div class="exam-card__details">

                    <div class="exam-card__detail">

                        <div class="exam-card__detail-label">
                            Date
                        </div>

                        <div class="exam-card__detail-value">
                            ${formatExamDate(exam.date)}
                        </div>

                    </div>


                    <div class="exam-card__detail">

                        <div class="exam-card__detail-label">
                            Time
                        </div>

                        <div class="exam-card__detail-value">
                            ${formatExamTime(exam.startTime)}
                        </div>

                    </div>


                    <div class="exam-card__detail">

                        <div class="exam-card__detail-label">
                            Duration
                        </div>

                        <div class="exam-card__detail-value">
                            ${exam.durationMinutes} Minutes
                        </div>

                    </div>


                    <div class="exam-card__detail">

                        <div class="exam-card__detail-label">
                            Questions
                        </div>

                        <div class="exam-card__detail-value">
                            ${exam.totalQuestions}
                        </div>

                    </div>

                </div>


                <div class="exam-card__proctoring">

                    <span
                        class="exam-card__proctoring-icon"
                        aria-hidden="true"
                    >
                        ${exam.proctored ? "P" : "✓"}
                    </span>

                    <span>
                        ${proctoringText}
                    </span>

                </div>


                <div class="exam-card__footer">

                    <span class="exam-card__footer-info">
                        ${
                            isCompleted
                                ? "Result available"
                                : formatAttemptStatus(
                                    exam.attemptStatus
                                )
                        }
                    </span>


                    <a
                        href="${actionHref}"
                        class="btn ${
                            isCompleted
                                ? "btn--secondary"
                                : "btn--primary"
                        }"
                    >
                        ${actionLabel}
                    </a>

                </div>

            </div>

        </article>
    `;
}


/* -----------------------------------------
   Empty State
   ----------------------------------------- */

function createEmptyState(title, message) {

    return `
        <div class="exam-empty-state">

            <div
                class="exam-empty-state__icon"
                aria-hidden="true"
            >
                E
            </div>

            <h3 class="exam-empty-state__title">
                ${escapeExamText(title)}
            </h3>

            <p class="exam-empty-state__message">
                ${escapeExamText(message)}
            </p>

        </div>
    `;
}


/* -----------------------------------------
   Format Attempt Status
   ----------------------------------------- */

function formatAttemptStatus(status) {

    const statusMap = {
        NOT_STARTED: "Not started",
        IN_PROGRESS: "In progress",
        SUBMITTED: "Submitted"
    };


    return statusMap[status] || "Not started";
}


/* -----------------------------------------
   Format Date
   ----------------------------------------- */

function formatExamDate(dateString) {

    const date =
        new Date(
            `${dateString}T00:00:00`
        );


    if (Number.isNaN(date.getTime())) {
        return dateString;
    }


    return new Intl.DateTimeFormat(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    ).format(date);
}


/* -----------------------------------------
   Format Time
   ----------------------------------------- */

function formatExamTime(timeString) {

    const [hours, minutes] =
        timeString.split(":").map(Number);


    if (
        Number.isNaN(hours) ||
        Number.isNaN(minutes)
    ) {
        return timeString;
    }


    const date =
        new Date();

    date.setHours(
        hours,
        minutes,
        0,
        0
    );


    return new Intl.DateTimeFormat(
        "en-IN",
        {
            hour: "numeric",
            minute: "2-digit"
        }
    ).format(date);
}


/* -----------------------------------------
   Basic HTML Escape
   ----------------------------------------- */

function escapeExamText(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}