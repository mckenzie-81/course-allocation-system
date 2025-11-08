package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestResponse {
    private Long id;
    private Long studentId;
    private String studentIdNumber;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String requestStatus; // PENDING, APPROVED, REJECTED, WAITLISTED
    private String requestReason;
    private String rejectionReason;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String processedBy;
}
