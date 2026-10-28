package com.IIITV.EMS.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("auth/login")
    public String login() {
        return "login";
    }

    @GetMapping("auth/student/dashboard")
    public String studentDashboard() {
        return "student-dashboard";
    }

    @GetMapping("auth/teacher/dashboard")
    public String teacherDashboard() {
        return "teacher-dashboard";
    }

    @GetMapping("auth/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("auth/admin/users")
    public String adminUsers() {
        return "admin-users";
    }

    @GetMapping("/auth/admin/settings")
    public String adminSettings() {
        return "admin-settings";
    }

    @GetMapping("/auth/admin/activity")
    public String adminActivity() {
        return "admin-activity";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordPage() {
        return "forget-password";
    }

    @GetMapping("/auth/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

}
