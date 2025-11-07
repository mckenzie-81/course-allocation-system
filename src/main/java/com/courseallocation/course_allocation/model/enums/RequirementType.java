package com.courseallocation.course_allocation.model.enums;

public enum RequirementType {
    PREREQUISITE,   // Must complete course before
    COREQUISITE,    // Must take simultaneously
    YEAR,           // Year level requirement
    CREDIT,         // Minimum credits completed
    PROGRAM,        // Program/major requirement
    GPA             // Minimum GPA requirement
}
