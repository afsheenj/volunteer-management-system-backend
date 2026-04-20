package com.vserve.project.controller;


import com.vserve.project.dto.user.LoginRequest;
import com.vserve.project.security.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final OrganizationDetailsService organizationDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            CustomUserDetailsService userDetailsService,
            OrganizationDetailsService organizationDetailsService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userDetailsService = userDetailsService;
        this.organizationDetailsService = organizationDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest request) {
        if (request.username() == null || request.password() == null ||
                request.username().isBlank() || request.password().isBlank()) {

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username and password are required"
            ));
        }

        UserPrincipal principal =
                (UserPrincipal) userDetailsService
                        .loadUserByUsername(request.username());

        if (!passwordEncoder.matches(request.password(), principal.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateUserToken(principal);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token
        ));
    }

    @PostMapping("/organization/login")
    public ResponseEntity<?> organizationLogin(@RequestBody LoginRequest request) {
        if (request.username() == null || request.password() == null ||
                request.username().isBlank() || request.password().isBlank()) {

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username and password are required"
            ));
        }

        OrganizationPrincipal principal =
                (OrganizationPrincipal) organizationDetailsService
                        .loadUserByUsername(request.username());

        if (!passwordEncoder.matches(request.password(), principal.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateOrganizationToken(principal);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token
        ));
    }

//    @PutMapping("")
}