package com.courseallocation.course_allocation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SemesterRequest {
    @NotBlank(message = "Semester code is required")
    private String semesterCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Boolean isActive = false;
}

