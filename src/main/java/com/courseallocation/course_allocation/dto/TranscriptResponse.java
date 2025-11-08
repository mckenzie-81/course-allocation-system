package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptResponse {
    private String studentId;
    private String studentName;
    private String program;
    private Integer yearOfStudy;
    private Integer totalCreditsCompleted;
    private Double currentGPA;
    private java.util.List<CourseGradeRecord> courseHistory;
    private LocalDateTime generatedAt;
}
