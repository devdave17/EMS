/* =========================================
   EMS — Authentication Service
   Frontend Mock Authentication
   ========================================= */


/**
 * Authenticate a user using the mock user list.
 *
 * NOTE:
 * This is only for the frontend prototype.
 * Real authentication will be handled by the
 * Spring Boot backend later.
 *
 * @param {string} userId
 * @param {string} password
 * @returns {object}
 */
function authenticateUser(userId, password) {

    const normalizedUserId = userId.trim();

    if (!normalizedUserId || !password) {
        return {
            success: false,
            message: "Please enter your User ID and password."
        };
    }

    const user = MOCK_USERS.find(
        (item) =>
            item.userId === normalizedUserId &&
            item.password === password
    );

    if (!user) {
        return {
            success: false,
            message: "Invalid User ID or password."
        };
    }

    /*
     * Do not keep the password in the authenticated
     * session object.
     */

    const authenticatedUser = {
        id: user.id,
        userId: user.userId,
        name: user.name,
        role: user.role,
        roleLabel: user.roleLabel,
        email: user.email
    };

    return {
        success: true,
        user: authenticatedUser
    };
}


/**
 * Get the dashboard path for a user's role.
 *
 * @param {string} role
 * @returns {string|null}
 */
function getDashboardPath(role) {

    const dashboardMap = {
        STUDENT: "../student/dashboard.html",
        TEACHER: "../teacher/dashboard.html",
        ADMIN: "../admin/dashboard.html",
        AUTHORITY: "../authority/dashboard.html"
    };

    return dashboardMap[role] || null;
}


/**
 * Check whether a role is supported by EMS.
 *
 * @param {string} role
 * @returns {boolean}
 */
function isValidRole(role) {

    const validRoles = [
        "STUDENT",
        "TEACHER",
        "ADMIN",
        "AUTHORITY"
    ];

    return validRoles.includes(role);
}