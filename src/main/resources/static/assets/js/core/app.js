/* =========================================
   EMS — Application Core
   Component Loader + Common UI Behavior
   ========================================= */

document.addEventListener("DOMContentLoaded", () => {
    loadComponents();
});


/* -----------------------------------------
   Load Reusable HTML Components
   ----------------------------------------- */

async function loadComponents() {

    const componentElements = document.querySelectorAll(
        "[data-component]"
    );

    for (const element of componentElements) {

        const componentPath = element.dataset.component;

        if (!componentPath) {
            continue;
        }

        try {

            const response = await fetch(componentPath);

            if (!response.ok) {
                throw new Error(
                    `Failed to load component: ${componentPath}`
                );
            }

            element.innerHTML = await response.text();

        } catch (error) {

            console.error(error);

            element.innerHTML = "";

        }
    }

    initializeSidebar();
    renderSidebarNavigation();
}


/* -----------------------------------------
   Mobile Sidebar
   ----------------------------------------- */

function initializeSidebar() {

    const sidebar = document.getElementById("app-sidebar");
    const menuButton = document.getElementById("mobile-menu-button");
    const overlay = document.querySelector(".app-overlay");

    if (!sidebar || !menuButton) {
        return;
    }

    menuButton.addEventListener("click", () => {

        const isOpen = sidebar.classList.toggle("is-open");

        menuButton.setAttribute(
            "aria-expanded",
            String(isOpen)
        );

        overlay?.classList.toggle(
            "is-visible",
            isOpen
        );

        document.body.classList.toggle(
            "sidebar-open",
            isOpen
        );
    });


    overlay?.addEventListener("click", closeSidebar);


    function closeSidebar() {

        sidebar.classList.remove("is-open");

        menuButton.setAttribute(
            "aria-expanded",
            "false"
        );

        overlay?.classList.remove("is-visible");

        document.body.classList.remove(
            "sidebar-open"
        );
    }
}