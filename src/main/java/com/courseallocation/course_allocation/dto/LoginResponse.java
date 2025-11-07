package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long studentId;
    private String studentIdNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private Integer year;
    private String token;
}

