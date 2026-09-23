/* =========================================
   EMS — Sidebar Controller
   Role-Based Navigation
   ========================================= */


/* -----------------------------------------
   Navigation Definitions
   ----------------------------------------- */

const NAVIGATION_BY_ROLE = {

    STUDENT: [

        {
            section: "Overview",
            items: [
                {
                    label: "Dashboard",
                    href: "dashboard.html",
                    icon: "dashboard"
                }
            ]
        },

        {
            section: "Examination",
            items: [
                {
                    label: "My Exams",
                    href: "exams.html",
                    icon: "exam"
                },
                {
                    label: "Results",
                    href: "results.html",
                    icon: "results"
                }
            ]
        }

    ],

    TEACHER: [],

    ADMIN: [],

    AUTHORITY: []
};


/* -----------------------------------------
   Icons
   ----------------------------------------- */

const NAVIGATION_ICONS = {

    dashboard: `
        <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
        >
            <rect x="3" y="3" width="7" height="7" rx="1"></rect>
            <rect x="14" y="3" width="7" height="7" rx="1"></rect>
            <rect x="3" y="14" width="7" height="7" rx="1"></rect>
            <rect x="14" y="14" width="7" height="7" rx="1"></rect>
        </svg>
    `,

    exam: `
        <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
        >
            <path d="M6 3h12"></path>
            <path d="M6 21h12"></path>
            <path d="M8 3v4a4 4 0 0 0 8 0V3"></path>
            <path d="M8 21v-4a4 4 0 0 1 8 0v4"></path>
            <path d="M10 12h4"></path>
        </svg>
    `,

    results: `
        <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
        >
            <path d="M4 19V5"></path>
            <path d="M4 19h16"></path>
            <path d="M7 15l3-3 3 2 5-6"></path>
        </svg>
    `
};


/* -----------------------------------------
   Render Sidebar Navigation
   ----------------------------------------- */

function renderSidebarNavigation() {

    const sidebarNav =
        document.getElementById("sidebar-nav");

    if (!sidebarNav) {
        return;
    }


    const currentUser =
        getCurrentUser();

    if (!currentUser) {
        sidebarNav.innerHTML = "";
        return;
    }


    const role =
        currentUser.role;

    const navigation =
        NAVIGATION_BY_ROLE[role] || [];


    if (navigation.length === 0) {

        sidebarNav.innerHTML = "";

        return;
    }


    sidebarNav.innerHTML =
        navigation.map(
            createNavigationSection
        ).join("");


    setActiveNavigationItem();
}


/* -----------------------------------------
   Create Navigation Section
   ----------------------------------------- */

function createNavigationSection(section) {

    const items =
        section.items
            .map(createNavigationItem)
            .join("");


    return `
        <section class="sidebar-nav__section">

            <div class="sidebar-nav__label">
                ${escapeSidebarText(section.section)}
            </div>

            <ul class="sidebar-nav__list">
                ${items}
            </ul>

        </section>
    `;
}


/* -----------------------------------------
   Create Navigation Item
   ----------------------------------------- */

function createNavigationItem(item) {

    const icon =
        NAVIGATION_ICONS[item.icon] || "";


    return `
        <li>

            <a
                href="${item.href}"
                class="nav-item"
                data-navigation="${escapeSidebarText(item.label)}"
            >

                <span class="nav-item__icon">
                    ${icon}
                </span>

                <span class="nav-item__label">
                    ${escapeSidebarText(item.label)}
                </span>

            </a>

        </li>
    `;
}


/* -----------------------------------------
   Active Navigation
   ----------------------------------------- */

function setActiveNavigationItem() {

    const currentFile =
        window.location.pathname
            .split("/")
            .pop()
            .toLowerCase();


    const navigationItems =
        document.querySelectorAll(
            ".sidebar-nav .nav-item"
        );


    navigationItems.forEach((item) => {

        const href =
            item.getAttribute("href");

        if (!href) {
            return;
        }


        const targetFile =
            href
                .split("/")
                .pop()
                .toLowerCase();


        const isActive =
            targetFile === currentFile;


        item.classList.toggle(
            "nav-item--active",
            isActive
        );

    });
}


/* -----------------------------------------
   Basic Text Escape
   ----------------------------------------- */

function escapeSidebarText(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}