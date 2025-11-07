package com.courseallocation.course_allocation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseSummary {
    private Long studentId;
    private String studentName;
    private String department;
    private Integer year;
    private Integer totalCourses;
    private Integer totalCredits;
    private Integer maxCreditsAllowed;
    private Integer maxCoursesAllowed;
    private List<EnrollmentResponse> enrolledCourses;
}

