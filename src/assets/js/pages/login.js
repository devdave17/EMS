/* =========================================
   EMS — Login Page
   Mock Authentication Controller
   ========================================= */

document.addEventListener("DOMContentLoaded", () => {
    initializeLoginPage();
});


/* -----------------------------------------
   Initialize Login
   ----------------------------------------- */

function initializeLoginPage() {

    const loginForm = document.getElementById("login-form");

    if (!loginForm) {
        return;
    }

    loginForm.addEventListener(
        "submit",
        handleLoginSubmit
    );


    initializeForgotPassword();
}


/* -----------------------------------------
   Login Submit
   ----------------------------------------- */

function handleLoginSubmit(event) {

    event.preventDefault();


    const loginForm =
        document.getElementById("login-form");

    const userIdInput =
        document.getElementById("user-id");

    const passwordInput =
        document.getElementById("password");

    const submitButton =
        document.getElementById("login-submit");


    if (
        !loginForm ||
        !userIdInput ||
        !passwordInput ||
        !submitButton
    ) {
        return;
    }


    clearLoginError();


    const userId =
        userIdInput.value.trim();

    const password =
        passwordInput.value;


    if (!userId || !password) {

        showLoginError(
            "Please enter your User ID and password."
        );

        return;
    }


    setLoginLoading(true);


    /*
     * Small delay for prototype UX.
     *
     * Later this will be replaced by
     * an actual API request to Spring Boot.
     */

    setTimeout(() => {

        const result =
            authenticateUser(
                userId,
                password
            );


        if (!result.success) {

            showLoginError(
                result.message
            );

            setLoginLoading(false);

            return;
        }


        const user =
            result.user;


        const sessionSaved =
            setCurrentUser(user);


        if (!sessionSaved) {

            showLoginError(
                "Unable to create your session. Please try again."
            );

            setLoginLoading(false);

            return;
        }


        const dashboardPath =
            getDashboardPath(user.role);


        if (!dashboardPath) {

            clearCurrentUser();

            showLoginError(
                "Your account role is not supported."
            );

            setLoginLoading(false);

            return;
        }


        window.location.href =
            dashboardPath;

    }, 500);
}


/* -----------------------------------------
   Login Loading State
   ----------------------------------------- */

function setLoginLoading(isLoading) {

    const submitButton =
        document.getElementById("login-submit");

    if (!submitButton) {
        return;
    }


    submitButton.disabled =
        isLoading;


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


/* -----------------------------------------
   Login Error
   ----------------------------------------- */

function showLoginError(message) {

    let errorElement =
        document.getElementById("login-error");


    if (!errorElement) {

        errorElement =
            document.createElement("div");

        errorElement.id =
            "login-error";

        errorElement.className =
            "alert alert--danger";

        errorElement.setAttribute(
            "role",
            "alert"
        );


        const loginHeader =
            document.querySelector(".login-header");


        if (loginHeader) {

            loginHeader.insertAdjacentElement(
                "afterend",
                errorElement
            );

        } else {

            return;
        }
    }


    errorElement.innerHTML = `
        <span
            class="alert__icon"
            aria-hidden="true"
        >
            !
        </span>

        <div class="alert__content">

            <div class="alert__title">
                Sign in failed
            </div>

            <div class="alert__message">
                ${escapeHtml(message)}
            </div>

        </div>
    `;
}


/* -----------------------------------------
   Clear Login Error
   ----------------------------------------- */

function clearLoginError() {

    const errorElement =
        document.getElementById("login-error");

    if (errorElement) {
        errorElement.remove();
    }
}


/* -----------------------------------------
   Forgot Password
   ----------------------------------------- */

function initializeForgotPassword() {

    const forgotPasswordLink =
        document.getElementById(
            "forgot-password-link"
        );

    if (!forgotPasswordLink) {
        return;
    }


    forgotPasswordLink.addEventListener(
        "click",
        (event) => {

            event.preventDefault();

            showLoginError(
                "Password recovery will be available when the backend authentication service is integrated."
            );
        }
    );
}


/* -----------------------------------------
   Basic HTML Escape
   ----------------------------------------- */

function escapeHtml(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
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


function handleLoginSubmit(event) {

    event.preventDefault();

    const userIdInput = document.getElementById("user-id");
    const passwordInput = document.getElementById("password");
    const submitButton = document.getElementById("login-submit");

    if (!userIdInput || !passwordInput || !submitButton) {
        return;
    }

    clearLoginError();

    const userId = userIdInput.value.trim();
    const password = passwordInput.value;

    if (!userId || !password) {
        showLoginError(
            "Please enter your User ID and password."
        );
        return;
    }

    setLoginLoading(true);

    setTimeout(() => {

        const result = authenticateUser(userId, password);

        if (!result.success) {

            showLoginError(result.message);
            setLoginLoading(false);
            return;
        }

        const sessionSaved = setCurrentUser(result.user);

        if (!sessionSaved) {

            showLoginError(
                "Unable to create your session. Please try again."
            );

            setLoginLoading(false);
            return;
        }

        const dashboardPath =
            getDashboardPath(result.user.role);

        if (!dashboardPath) {

            clearCurrentUser();

            showLoginError(
                "Your account role is not supported."
            );

            setLoginLoading(false);
            return;
        }

        window.location.href = dashboardPath;

    }, 500);
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

    errorElement.innerHTML = `
        <span class="alert__icon" aria-hidden="true">!</span>

        <div class="alert__content">

            <div class="alert__title">
                Sign in failed
            </div>

            <div class="alert__message">
                ${escapeHtml(message)}
            </div>

        </div>
    `;
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
            "Password recovery will be available when the backend authentication service is integrated."
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