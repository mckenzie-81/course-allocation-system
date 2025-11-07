package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

