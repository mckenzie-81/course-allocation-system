package com.courseallocation.course_allocation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.EnrollmentResponse;
import com.courseallocation.course_allocation.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "Enrollment management APIs")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    @Operation(summary = "Enroll student in course")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        EnrollmentResponse enrollment = enrollmentService.enrollStudent(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Student enrolled successfully", enrollment));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getStudentEnrollments(@PathVariable Long studentId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get course enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getCourseEnrollments(@PathVariable Long courseId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getCourseEnrollments(courseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Enrollments retrieved successfully", enrollments));
    }

    @DeleteMapping("/{enrollmentId}/drop")
    @Operation(summary = "Drop enrollment")
    public ResponseEntity<ApiResponse<Void>> dropEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.dropEnrollment(enrollmentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Enrollment dropped successfully", null));
    }
}
