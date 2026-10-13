/* =========================================
   EMS — Application State
   Frontend Session Management
   ========================================= */

const EMS_STORAGE_KEY = "ems_current_user";


/**
 * Store the authenticated user in localStorage.
 *
 * @param {object} user
 */
function setCurrentUser(user) {

    if (!user || typeof user !== "object") {
        return false;
    }

    try {

        localStorage.setItem(
            EMS_STORAGE_KEY,
            JSON.stringify(user)
        );

        return true;

    } catch (error) {

        console.error(
            "Unable to save EMS session:",
            error
        );

        return false;
    }
}


/**
 * Get the currently authenticated user.
 *
 * @returns {object|null}
 */
function getCurrentUser() {

    try {

        const storedUser =
            localStorage.getItem(EMS_STORAGE_KEY);

        if (!storedUser) {
            return null;
        }

        return JSON.parse(storedUser);

    } catch (error) {

        console.error(
            "Unable to read EMS session:",
            error
        );

        return null;
    }
}


/**
 * Check whether a user is currently authenticated.
 *
 * @returns {boolean}
 */
function isAuthenticated() {
    return getCurrentUser() !== null;
}


/**
 * Get the currently authenticated user's role.
 *
 * @returns {string|null}
 */
function getCurrentRole() {

    const user = getCurrentUser();

    return user?.role || null;
}


/**
 * Clear the current authentication session.
 */
function clearCurrentUser() {

    try {

        localStorage.removeItem(
            EMS_STORAGE_KEY
        );

        return true;

    } catch (error) {

        console.error(
            "Unable to clear EMS session:",
            error
        );

        return false;
    }
}