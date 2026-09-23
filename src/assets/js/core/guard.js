/* =========================================
   EMS — Route & Role Guard
   Frontend Access Control
   ========================================= */


/**
 * Protect a page from unauthenticated access.
 *
 * If the user is not logged in, redirect to login.
 */
function requireAuthentication() {

    if (!isAuthenticated()) {

        window.location.href =
            "../auth/login.html";

        return false;
    }

    return true;
}


/**
 * Check whether the current user has
 * the required role.
 *
 * @param {string|string[]} allowedRoles
 * @returns {boolean}
 */
function requireRole(allowedRoles) {

    if (!isAuthenticated()) {

        window.location.href =
            "../auth/login.html";

        return false;
    }


    const currentRole = getCurrentRole();


    if (!currentRole) {

        window.location.href =
            "../auth/login.html";

        return false;
    }


    const roles = Array.isArray(allowedRoles)
        ? allowedRoles
        : [allowedRoles];


    if (!roles.includes(currentRole)) {

        redirectToAccessDenied();

        return false;
    }


    return true;
}


/**
 * Redirect the user to the
 * unauthorized/access-denied page.
 */
function redirectToAccessDenied() {

    window.location.href =
        "../auth/access-denied.html";
}


/**
 * Logout the current user.
 */
function logoutUser() {

    clearCurrentUser();

    window.location.href =
        "../auth/login.html";
}