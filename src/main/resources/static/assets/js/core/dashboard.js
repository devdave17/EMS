/* =========================================
   EMS — Student Dashboard
   ========================================= */

const currentUser = getCurrentUser();

console.log("Dashboard JS loaded");
console.log("Current user:", currentUser);

const welcomeTitle = document.getElementById("student-welcome-title");

if (welcomeTitle && currentUser?.name) {
    welcomeTitle.textContent = `Welcome back, ${currentUser.name}`;
}