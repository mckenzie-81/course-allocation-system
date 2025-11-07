package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private String department;
    private Integer level;
    private Integer credits;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private String description;
    private String instructor;
    private Long semesterId;
    private String semesterCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

