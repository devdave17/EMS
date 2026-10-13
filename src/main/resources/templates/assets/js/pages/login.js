/* =========================================
   EMS — Login Page Controller
   ========================================= */

document.addEventListener("DOMContentLoaded", () => {
    initializeLoginPage();
});


function initializeLoginPage() {

    const loginForm = document.getElementById("login-form");

    if (!loginForm) {
        return;
    }

    loginForm.addEventListener("submit", handleLoginSubmit);

    initializeForgotPassword();
}


async function handleLoginSubmit(event) {

    event.preventDefault();

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const submitButton = document.getElementById("login-submit");

    if (!emailInput || !passwordInput || !submitButton) {
        return;
    }

    clearLoginError();

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    if (!email || !password) {
        showLoginError("Please enter your email and password.");
        return;
    }

    setLoginLoading(true);

    const result = await authenticateUser(email, password);

    if (!result.success) {
        showLoginError(result.message);
        setLoginLoading(false);
        return;
    }

    const user = result.user;
    const dashboardPath = getDashboardPath(user?.role);

    if (!dashboardPath) {
        showLoginError("Your account role is not supported.");
        setLoginLoading(false);
        return;
    }

    const sessionSaved = setCurrentUser(user);

    if (!sessionSaved) {
        showLoginError(
            "Unable to create your session. Please try again."
        );
        setLoginLoading(false);
        return;
    }

    window.location.href = dashboardPath;
}


function setLoginLoading(isLoading) {

    const submitButton =
        document.getElementById("login-submit");

    if (!submitButton) {
        return;
    }

    submitButton.disabled = isLoading;

    submitButton.classList.toggle(
        "is-loading",
        isLoading
    );

    if (isLoading) {
        submitButton.dataset.originalText =
            submitButton.textContent;

        submitButton.textContent =
            "Signing in...";
    } else {
        submitButton.textContent =
            submitButton.dataset.originalText ||
            "Sign In";
    }
}


function showLoginError(message) {

    let errorElement =
        document.getElementById("login-error");

    if (!errorElement) {
        errorElement =
            document.createElement("div");

        errorElement.id = "login-error";
        errorElement.className = "alert alert--danger";
        errorElement.setAttribute("role", "alert");

        const loginHeader =
            document.querySelector(".login-header");

        if (!loginHeader) {
            return;
        }

        loginHeader.insertAdjacentElement(
            "afterend",
            errorElement
        );
    }

    errorElement.innerHTML =
        '<span class="alert__icon" aria-hidden="true">!</span>' +
        '<div class="alert__content">' +
            '<div class="alert__title">Sign in failed</div>' +
            '<div class="alert__message">' +
                escapeHtml(message) +
            '</div>' +
        '</div>';
}


function clearLoginError() {

    const errorElement =
        document.getElementById("login-error");

    if (errorElement) {
        errorElement.remove();
    }
}


function initializeForgotPassword() {

    const link =
        document.getElementById("forgot-password-link");

    if (!link) {
        return;
    }

    link.addEventListener("click", (event) => {
        event.preventDefault();

        showLoginError(
            "Password recovery is not available yet. Please contact your EMS administrator."
        );
    });
}


function escapeHtml(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
