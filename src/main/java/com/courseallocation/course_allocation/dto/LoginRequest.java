package com.courseallocation.course_allocation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "PIN is required")
    private String pin;
}

