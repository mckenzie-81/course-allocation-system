package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String departmentCode;
    private String departmentName;
    private String program;
    private Integer yearOfStudy;
    private Integer creditsCompleted;
    private Double currentGPA;
    private String role;
}
