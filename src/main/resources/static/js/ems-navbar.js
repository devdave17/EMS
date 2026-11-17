(function () {
    const path = window.location.pathname;
    const role = path.includes("/student/")
        ? "student"
        : path.includes("/teacher/")
            ? "teacher"
            : path.includes("/admin/")
                ? "admin"
                : null;

    if (!role || document.getElementById("sidebar")) {
        return;
    }

    const menus = {
        student: {
            label: "Student Workspace",
            activeBackground: "#eef2ff",
            activeColor: "#4338ca",
            dashboard: "/auth/student/dashboard",
            links: [
                ["▦", "Dashboard", "/auth/student/dashboard"],
                ["▤", "My Exams", "/auth/student/exams"],
                ["◷", "Results", "/auth/student/results"],
                ["◎", "My Profile", "/auth/student/profile"]
            ]
        },
        teacher: {
            label: "Teaching Workspace",
            activeBackground: "#ecfdf5",
            activeColor: "#047857",
            dashboard: "/auth/teacher/dashboard",
            links: [
                ["▦", "Dashboard", "/auth/teacher/dashboard"],
                ["▤", "Manage Exams", "/auth/teacher/exams"],
                ["☷", "Question Bank", "/auth/teacher/questions"],
                ["◷", "Results", "/auth/teacher/results"],
                ["◎", "My Profile", "/auth/teacher/profile"]
            ]
        },
        admin: {
            label: "Administration",
            activeBackground: "#f5f3ff",
            activeColor: "#6d28d9",
            dashboard: "/auth/admin/dashboard",
            links: [
                ["▦", "Dashboard", "/auth/admin/dashboard"],
                ["♙", "User Management", "/auth/admin/users"],
                ["♧", "Students", "/auth/admin/students"],
                ["♙", "Teachers", "/auth/admin/teachers"],
                ["▧", "Examinations", "/auth/admin/exams"],
                ["◷", "Results", "/auth/admin/results"],
                ["◴", "Activity Logs", "/auth/admin/activity"],
                ["⚙", "Settings", "/auth/admin/settings"]
            ]
        }
    };

    const menu = menus[role];
    const style = document.createElement("style");
    style.textContent = `
        .ems-navbar {
            position: fixed;
            inset: 0 auto 0 0;
            z-index: 60;
            display: flex;
            width: 260px;
            flex-direction: column;
            border-right: 1px solid #e2e8f0;
            background: #fff;
            box-shadow: 4px 0 18px rgba(15, 23, 42, .04);
        }
        .ems-navbar-brand {
            display: flex;
            height: 80px;
            align-items: center;
            gap: 12px;
            border-bottom: 1px solid #f1f5f9;
            padding: 0 24px;
            color: #0f172a;
            text-decoration: none;
        }
        .ems-navbar-brand img { height: 48px; width: 48px; border-radius: 8px; object-fit: contain; }
        .ems-navbar-brand strong { display: block; font-size: 20px; letter-spacing: -.025em; }
        .ems-navbar-brand small { color: #64748b; font-size: 11px; }
        .ems-navbar-section { padding: 24px 16px; }
        .ems-navbar-label {
            margin: 0 12px 12px;
            color: #94a3b8;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: .08em;
            text-transform: uppercase;
        }
        .ems-navbar-links { display: grid; gap: 4px; }
        .ems-navbar-link {
            display: flex;
            align-items: center;
            gap: 12px;
            border-radius: 12px;
            color: #475569;
            font-size: 14px;
            padding: 12px;
            text-decoration: none;
        }
        .ems-navbar-link:hover, .ems-navbar-link.active {
            background: ${menu.activeBackground};
            color: ${menu.activeColor};
        }
        .ems-navbar-link.active { font-weight: 600; }
        .ems-navbar-footer { margin-top: auto; padding: 16px; }
        .ems-navbar-logout {
            width: 100%;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            background: #fff;
            color: #dc2626;
            cursor: pointer;
            font-size: 14px;
            padding: 11px 12px;
            text-align: left;
        }
        .ems-navbar-logout:hover { background: #fef2f2; }
        .ems-navbar-toggle {
            display: none;
            position: fixed;
            top: 12px;
            left: 12px;
            z-index: 59;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            background: #fff;
            padding: 8px 10px;
            font-size: 18px;
        }
        body:has(.ems-navbar) { padding-left: 260px; }
        @media (max-width: 767px) {
            .ems-navbar { transform: translateX(-100%); transition: transform .25s ease; }
            .ems-navbar.open { transform: translateX(0); }
            .ems-navbar-toggle { display: block; }
            body:has(.ems-navbar) { padding-left: 0; }
            .ems-navbar-overlay {
                position: fixed;
                inset: 0;
                z-index: 50;
                display: none;
                background: rgba(15, 23, 42, .4);
            }
            .ems-navbar-overlay.show { display: block; }
        }
    `;
    document.head.appendChild(style);

    const oldAside = document.querySelector("body > aside, body > div > aside");
    if (oldAside && !oldAside.id) {
        oldAside.remove();
    }

    const nav = document.createElement("aside");
    nav.className = "ems-navbar";
    nav.innerHTML = `
        <a class="ems-navbar-brand" href="${menu.dashboard}">
            <img src="/EMSlogo.png" alt="EMS logo">
            <span><strong>EMS</strong><small>Examination Management</small></span>
        </a>
        <div class="ems-navbar-section">
            <p class="ems-navbar-label">${menu.label}</p>
            <nav class="ems-navbar-links" aria-label="${menu.label}">
                ${menu.links.map(([icon, label, href]) => `
                    <a class="ems-navbar-link${path === href || path.startsWith(`${href}/`) ? " active" : ""}" href="${href}">
                        <span aria-hidden="true">${icon}</span>${label}
                    </a>
                `).join("")}
            </nav>
        </div>
        <div class="ems-navbar-footer">
            <button type="button" class="ems-navbar-logout">Log out</button>
        </div>
    `;

    const overlay = document.createElement("div");
    overlay.className = "ems-navbar-overlay";
    const toggle = document.createElement("button");
    toggle.className = "ems-navbar-toggle";
    toggle.type = "button";
    toggle.textContent = "☰";
    toggle.setAttribute("aria-label", "Open menu");
    document.body.prepend(overlay);
    document.body.prepend(nav);
    document.body.prepend(toggle);

    const close = () => {
        nav.classList.remove("open");
        overlay.classList.remove("show");
    };
    toggle.addEventListener("click", () => {
        nav.classList.toggle("open");
        overlay.classList.toggle("show");
    });
    overlay.addEventListener("click", close);
    nav.querySelector(".ems-navbar-logout").addEventListener("click", () => {
        localStorage.removeItem("token");
        localStorage.removeItem("ems_current_user");
        window.location.replace("/auth/login");
    });
})();
