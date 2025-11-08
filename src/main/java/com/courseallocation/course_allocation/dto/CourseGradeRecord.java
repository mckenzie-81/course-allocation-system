package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseGradeRecord {
    private String courseCode;
    private String courseTitle;
    private Integer credits;
    private String semesterCode;
    private String finalGrade;
    private String status;
}
