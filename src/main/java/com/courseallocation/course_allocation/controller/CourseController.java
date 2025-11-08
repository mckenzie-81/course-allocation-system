package com.courseallocation.course_allocation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.CourseRequest;
import com.courseallocation.course_allocation.dto.CourseRequirementResponse;
import com.courseallocation.course_allocation.dto.CourseResponse;
import com.courseallocation.course_allocation.dto.EnrollmentResponse;
import com.courseallocation.course_allocation.service.CourseService;
import com.courseallocation.course_allocation.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "Course management APIs")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse course = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Course created successfully", course));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course retrieved successfully", course));
    }

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(new ApiResponse<>(true, "Courses retrieved successfully", courses));
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get courses by semester")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesBySemester(@PathVariable Long semesterId) {
        List<CourseResponse> courses = courseService.getCoursesBySemesterId(semesterId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Courses retrieved successfully", courses));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        CourseResponse course = courseService.updateCourse(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course updated successfully", course));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course deleted successfully", null));
    }

    @GetMapping("/{id}/requirements")
    @Operation(summary = "Get course requirements", description = "Get all prerequisites and requirements for a course")
    public ResponseEntity<ApiResponse<List<CourseRequirementResponse>>> getCourseRequirements(@PathVariable Long id) {
        try {
            List<CourseRequirementResponse> requirements = courseService.getCourseRequirements(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Requirements retrieved successfully", requirements));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/enrollments")
    @Operation(summary = "Get all enrollments for a course", description = "View all students enrolled in a specific course")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getCourseEnrollments(@PathVariable Long id) {
        try {
            List<EnrollmentResponse> enrollments = enrollmentService.getCourseEnrollments(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Enrollments retrieved successfully", enrollments));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses", description = "Search courses by title, code, or department")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> searchCourses(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long semesterId) {
        try {
            List<CourseResponse> courses;
            
            if (departmentId != null) {
                courses = courseService.getCoursesByDepartmentId(departmentId);
            } else if (semesterId != null) {
                courses = courseService.getCoursesBySemesterId(semesterId);
            } else if (query != null && !query.isEmpty()) {
                courses = courseService.searchCourses(query);
            } else {
                courses = courseService.getAllCourses();
            }
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Search completed successfully", courses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
