package com.courseallocation.course_allocation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestStatusUpdate {
    @NotBlank(message = "Status is required")
    private String status; // APPROVED, REJECTED, WAITLISTED
    
    private String reason;
}
