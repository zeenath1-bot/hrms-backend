package org.example.hrmanagementsystem.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.auth.dto.LoginRequestDTO;
import org.example.hrmanagementsystem.auth.dto.LoginResponseDTO;
import org.example.hrmanagementsystem.auth.dto.RegisterRequestDTO;
import org.example.hrmanagementsystem.auth.dto.RegisterResponseDTO;
import org.example.hrmanagementsystem.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>("User registered successfully" ,authService.register(dto)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>("Login  successful" ,authService.login(dto)));
    }
}
