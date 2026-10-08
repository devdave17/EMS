/* =========================================
   EMS — Authentication Service
   ========================================= */

/**
 * Authenticate a user through the EMS backend.
 *
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{success: boolean, user?: object, message?: string}>}
 */
async function authenticateUser(email, password) {

    const normalizedEmail = String(email ?? "").trim();

    if (!normalizedEmail || !password) {
        return {
            success: false,
            message: "Please enter your email and password."
        };
    }

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: normalizedEmail,
                password
            })
        });

        let responseData = null;

        try {
            responseData = await response.json();
        } catch (error) {
            // A non-JSON response is handled below as an invalid response.
        }

        if (!response.ok) {
            return {
                success: false,
                message:
                    responseData?.message ||
                    (response.status === 401 || response.status === 403
                        ? "Invalid email or password."
                        : "Sign in failed. Please try again.")
            };
        }

        if (!responseData || typeof responseData !== "object" || Array.isArray(responseData)) {
            return {
                success: false,
                message: "The server returned an invalid sign-in response."
            };
        }

        return {
            success: true,
            user: responseData
        };

    } catch (error) {
        return {
            success: false,
            message: "Unable to connect to the server. Please try again."
        };
    }
}


/**
 * Get the dashboard path for a user's backend role.
 *
 * @param {string} role
 * @returns {string|null}
 */
function getDashboardPath(role) {

    const dashboardMap = {
        STUDENT: "../student/dashboard.html",
        TEACHER: "../teacher/dashboard.html",
        ADMIN: "../admin/dashboard.html"
    };

    return Object.prototype.hasOwnProperty.call(dashboardMap, role) ? dashboardMap[role] : null;
}


/**
 * Check whether a role is supported by EMS.
 *
 * @param {string} role
 * @returns {boolean}
 */
function isValidRole(role) {

    return [
        "STUDENT",
        "TEACHER",
        "ADMIN"
    ].includes(role);
}
