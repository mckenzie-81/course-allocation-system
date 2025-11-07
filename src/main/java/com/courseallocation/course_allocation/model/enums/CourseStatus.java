package com.courseallocation.course_allocation.model.enums;

public enum CourseStatus {
    DRAFT,      // Course created by HOD but not yet configured by lecturer
    ACTIVE,     // Course is open for enrollment
    CLOSED,     // Course enrollment closed
    CANCELLED   // Course cancelled
}
