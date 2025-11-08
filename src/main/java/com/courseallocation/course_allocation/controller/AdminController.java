package com.courseallocation.course_allocation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.EnrollmentResponse;
import com.courseallocation.course_allocation.dto.ForceEnrollmentRequest;
import com.courseallocation.course_allocation.dto.SystemStatisticsResponse;
import com.courseallocation.course_allocation.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative operations and system management")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/statistics")
    @Operation(summary = "Get system statistics", description = "Retrieve overall system statistics and metrics")
    public ResponseEntity<ApiResponse<SystemStatisticsResponse>> getSystemStatistics() {
        try {
            SystemStatisticsResponse stats = adminService.getSystemStatistics();
            return ResponseEntity.ok(new ApiResponse<>(true, "Statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/enrollments/force-enroll")
    @Operation(summary = "Force enroll student", description = "Force enroll a student bypassing all rules and capacity limits")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> forceEnroll(
            @Valid @RequestBody ForceEnrollmentRequest request) {
        try {
            EnrollmentResponse enrollment = adminService.forceEnroll(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Student force-enrolled successfully", enrollment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/enrollments/{enrollmentId}/force-drop")
    @Operation(summary = "Force drop enrollment", description = "Force drop a student from a course (Admin override)")
    public ResponseEntity<ApiResponse<Void>> forceDropEnrollment(@PathVariable Long enrollmentId) {
        try {
            adminService.forceDropEnrollment(enrollmentId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Enrollment force-dropped", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/courses/{courseId}/emergency-capacity")
    @Operation(summary = "Update emergency capacity", description = "Emergency capacity increase for a course")
    public ResponseEntity<ApiResponse<Void>> updateEmergencyCapacity(
            @PathVariable Long courseId,
            @RequestBody Integer newCapacity) {
        try {
            adminService.updateCourseEmergencyCapacity(courseId, newCapacity);
            return ResponseEntity.ok(new ApiResponse<>(true, "Course capacity updated", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
