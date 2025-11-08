package com.courseallocation.course_allocation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String code;
    private String name;
    private Long hodUserId;
    private String hodName;
    private Integer totalCourses;
    private Integer totalLecturers;
    private LocalDateTime createdAt;
}
