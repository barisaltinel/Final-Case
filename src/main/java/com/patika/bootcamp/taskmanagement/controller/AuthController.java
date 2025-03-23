package com.patika.bootcamp.taskmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;

    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String rawPassword = user.get("password");
        String hashedPassword = passwordEncoder.encode(rawPassword);

        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("hashedPassword", hashedPassword);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
