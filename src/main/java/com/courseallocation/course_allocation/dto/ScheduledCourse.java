package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledCourse {
    private String courseCode;
    private String title;
    private Integer credits;
    private String lecturerName;
    private String status;
}
