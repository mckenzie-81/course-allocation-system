package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private String studentId;
    private String semesterCode;
    private java.util.List<ScheduledCourse> courses;
    private Integer totalCredits;
    private LocalDateTime generatedAt;
}
