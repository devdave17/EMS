package com.IIITV.EMS.Controller;

import com.IIITV.EMS.DTO.LoginRequest;
import com.IIITV.EMS.Model.User;
import com.IIITV.EMS.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(
                        request.getEmail(),
                        request.getPassword()
                )
        );
    }
}