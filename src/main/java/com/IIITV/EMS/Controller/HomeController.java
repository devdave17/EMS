package com.IIITV.EMS.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("auth/login")
    public String login(){
        return "login";
    }

    @GetMapping("auth/student/dashboard")
    public String studentDashboard(){
        return "student-dashboard";
    }

    @GetMapping("auth/teacher/dashboard")
    public String teacherDashboard(){
        return "teacher-dashboard";
    }

    @GetMapping("auth/admin/dashboard")
    public String adminDashboard(){
        return "admin-dashboard";
    }

    @GetMapping("auth/admin/users")
    public String adminUsers() {
        return "admin-users";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordPage() {
        return "forget-password";
    }

    @GetMapping("/auth/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @GetMapping("/auth/teacher/questions")
    public String questionBankPage() {
        return "question-bank";
    }

    @GetMapping("/auth/teacher/questions/new")
    public String createQuestionBankPage() {
        return "question-bank-create";
    }

    @GetMapping("/auth/teacher/questions/{bankId}")
    public String questionBankDetailPage(
            @PathVariable String bankId,
            Model model) {

        model.addAttribute("bankId", bankId);
        return "question-bank-detail";
    }

    @GetMapping("/auth/teacher/exams/new")
    public String createExamPage() {
        return "exam-create";
    }
    @GetMapping("/auth/teacher/exams")
    public String manageExamsPage() {
        return "manage-exams";
    }

    @GetMapping("/auth/student/exams")
    public String studentExams() {
        return "exams";
    }

    @GetMapping("/auth/student/exam")
    public String studentExamPage() {
        return "student-exam";
    }
}
