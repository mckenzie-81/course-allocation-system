package com.courseallocation.course_allocation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.CourseEligibilityResponse;
import com.courseallocation.course_allocation.dto.CourseResponse;
import com.courseallocation.course_allocation.dto.EnrollmentResponse;
import com.courseallocation.course_allocation.dto.ScheduleResponse;
import com.courseallocation.course_allocation.dto.TranscriptResponse;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.service.AuthenticationService;
import com.courseallocation.course_allocation.service.EnrollmentService;
import com.courseallocation.course_allocation.service.StudentPortalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student Portal", description = "Student-specific APIs for course browsing and management")
public class StudentPortalController {

    private final StudentPortalService studentPortalService;
    private final EnrollmentService enrollmentService;
    private final AuthenticationService authenticationService;

    @GetMapping("/courses")
    @Operation(summary = "Browse available courses", description = "Get list of courses available for enrollment")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAvailableCourses(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long semesterId) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            List<CourseResponse> courses = studentPortalService.getAvailableCourses(student.getId(), semesterId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Courses retrieved successfully", courses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/courses/{courseId}")
    @Operation(summary = "View course details", description = "Get detailed information about a specific course")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseDetails(
            @PathVariable Long courseId) {
        try {
            CourseResponse course = studentPortalService.getAvailableCourses(null, null).stream()
                    .filter(c -> c.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Course found", course));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/courses/{courseId}/validate")
    @Operation(summary = "Check course eligibility", description = "Validate if student meets requirements for a course")
    public ResponseEntity<ApiResponse<CourseEligibilityResponse>> checkEligibility(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long courseId) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            CourseEligibilityResponse eligibility = studentPortalService.checkCourseEligibility(student.getId(), courseId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Eligibility checked", eligibility));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/enrollments")
    @Operation(summary = "View enrolled courses", description = "Get all currently enrolled courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            List<EnrollmentResponse> enrollments = enrollmentService.getStudentEnrollments(student.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Enrollments retrieved", enrollments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/schedule")
    @Operation(summary = "View current semester schedule", description = "Get schedule for active semester")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getCurrentSchedule(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            ScheduleResponse schedule = studentPortalService.getCurrentSchedule(student.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Schedule retrieved", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/transcript")
    @Operation(summary = "View academic transcript", description = "Get complete academic transcript with grades")
    public ResponseEntity<ApiResponse<TranscriptResponse>> getTranscript(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            TranscriptResponse transcript = studentPortalService.getTranscript(student.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Transcript generated", transcript));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/enrollments/history")
    @Operation(summary = "View enrollment history", description = "Get complete enrollment history with all statuses and grades")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentHistory(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            List<EnrollmentResponse> history = enrollmentService.getStudentEnrollments(student.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Enrollment history retrieved", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/enrollments/{enrollmentId}/drop")
    @Operation(summary = "Drop enrolled course", description = "Drop a course that student is currently enrolled in")
    public ResponseEntity<ApiResponse<Void>> dropCourse(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long enrollmentId) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            enrollmentService.dropEnrollment(enrollmentId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Course dropped successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
