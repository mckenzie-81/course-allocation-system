package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEligibilityResponse {
    private Boolean isEligible;
    private String message;
    private Boolean hasPrerequisites;
    private Boolean meetsGPARequirement;
    private Boolean meetsYearRequirement;
    private Boolean hasAvailableSeats;
    private Boolean alreadyEnrolled;
    private java.util.List<String> unmetRequirements;
}
