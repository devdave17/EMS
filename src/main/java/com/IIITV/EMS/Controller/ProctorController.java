package com.IIITV.EMS.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProctorController {
    @RequestMapping("/proctor")
    public String proctorDashboard() {
        return "proctor";
    }
    @GetMapping("/")
    public String index(){
        return "index";
    }
}
