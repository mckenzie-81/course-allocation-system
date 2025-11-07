package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentIdNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String status; // EnrollmentStatus enum value
    private String grade;
    private LocalDateTime createdAt;
}

