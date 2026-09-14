package org.example.hrmanagementsystem.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.dto.*;
import org.springframework.security.core.Authentication;

import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.Duration;


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
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO dto, HttpServletResponse response) {
        LoginResponseDTO loginResponse =  authService.login(dto);

        ResponseCookie cookie = ResponseCookie
                .from("accessToken" , loginResponse.getToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(24))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE , cookie.toString());
        LoginResponseDTO responseWithoutToken = new LoginResponseDTO(null,
                loginResponse.getUsername(), loginResponse.getRole(),loginResponse.isMustChangePassword());
        return ResponseEntity.ok(new ApiResponse<>("Login successful" ,responseWithoutToken));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponseDTO>> getCurrentUser(
            Authentication authentication
    ){
        String username = authentication.getName();
        return ResponseEntity.ok(
                new ApiResponse<>("User Retrieved successfully" ,
                        authService.getCurrentUser(username)
        )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout (HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie
                .from("accessToken","")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(
                new ApiResponse<>("Logout successful" , null)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        authService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse<>("Password reset successfull.", null));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request){
        authService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse<>("If the account exists , a  reset link has been sent.", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication){
        authService.changePassword(authentication.getName() , request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse<>("Password changed successfully.",null));
    }
}
