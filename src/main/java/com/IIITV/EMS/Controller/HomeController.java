package com.IIITV.EMS.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("auth/login")
    public String login(){
        return "pages/auth/login";
    }

    @GetMapping("auth/student/dashboard")
    public String studentDashboard(){
        return "pages/student/dashboard";
    }

    @GetMapping("auth/teacher/dashboard")
    public String teacherDashboard(){
        return "pages/teacher/dashboard";
    }

    @GetMapping("auth/admin/dashboard")
    public String adminDashboard(){
        return "pages/admin/dashboard";
    }


}
