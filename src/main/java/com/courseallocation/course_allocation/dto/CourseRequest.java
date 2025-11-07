package com.courseallocation.course_allocation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Level is required")
    @Min(value = 1, message = "Level must be at least 1")
    private Integer level;

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    @NotNull(message = "Max capacity is required")
    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacity;

    private String description;

    @NotBlank(message = "Instructor is required")
    private String instructor;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;
}

