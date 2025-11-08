package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {
    private String code;
    private String name;
    private Long hodUserId; // Head of Department User ID
}
