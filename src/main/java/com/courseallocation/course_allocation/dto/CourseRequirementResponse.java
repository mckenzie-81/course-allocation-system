package com.courseallocation.course_allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequirementResponse {
    private Long id;
    private Long courseId;
    private String courseCode;
    private Long prerequisiteCourseId;
    private String prerequisiteCourseCode;
    private String prerequisiteCourseTitle;
    private String minGrade;
    private Integer minCreditsCompleted;
    private Integer requiredYear;
    private String requiredProgram;
    private Double minGPA;
    private String requirementType;
    private Boolean isMandatory;
    private String description;
}
