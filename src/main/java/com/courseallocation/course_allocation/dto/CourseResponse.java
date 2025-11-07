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
    private String title;
    private String description;
    private Integer level;
    private Integer credits;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private String departmentCode;
    private String departmentName;
    private Long assignedLecturerId;
    private String lecturerName;
    private Long semesterId;
    private String semesterCode;
    private String academicYear;
    private String status; // CourseStatus enum value
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

