package com.IIITV.EMS.Controller;

import com.IIITV.EMS.DTO.LoginRequest;
import com.IIITV.EMS.Model.User;
import com.IIITV.EMS.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {

        User user = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(user);
    }
}