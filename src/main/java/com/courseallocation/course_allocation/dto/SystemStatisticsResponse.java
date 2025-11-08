package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatisticsResponse {
    private Integer totalStudents;
    private Integer totalCourses;
    private Integer totalEnrollments;
    private Integer totalDepartments;
    private Integer activeSemesters;
    private Integer pendingEnrollmentRequests;
    private Double averageGPA;
    private Integer totalCreditsAllocated;
}
