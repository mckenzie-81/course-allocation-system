package com.courseallocation.course_allocation.model.enums;

public enum RequestStatus {
    PENDING,        // Awaiting allocation process
    APPROVED,       // Successfully allocated
    REJECTED,       // Failed validation or no seats
    WAITLISTED,     // Course full, added to waitlist
    CANCELLED       // Student cancelled request
}
