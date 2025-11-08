package com.courseallocation.course_allocation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.LoginRequest;
import com.courseallocation.course_allocation.dto.LoginResponse;
import com.courseallocation.course_allocation.dto.RegisterRequest;
import com.courseallocation.course_allocation.dto.TokenRefreshRequest;
import com.courseallocation.course_allocation.dto.UserProfileResponse;
import com.courseallocation.course_allocation.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Student login", description = "Authenticate user with student ID and PIN")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authenticationService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new student account")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Registration successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Refresh an expired or expiring JWT token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            String newToken = authenticationService.refreshToken(request.getToken());
            return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", newToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile information")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            String token = authHeader.substring(7);
            UserProfileResponse profile = authenticationService.getProfile(token);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
