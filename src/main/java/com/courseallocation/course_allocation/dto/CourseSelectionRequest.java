package com.courseallocation.course_allocation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseSelectionRequest {
    @NotNull(message = "Course ID is required")
    private Long courseId;
}

