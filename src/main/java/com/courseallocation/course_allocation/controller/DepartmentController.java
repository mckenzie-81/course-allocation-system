package com.courseallocation.course_allocation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.CourseResponse;
import com.courseallocation.course_allocation.dto.DepartmentRequest;
import com.courseallocation.course_allocation.dto.DepartmentResponse;
import com.courseallocation.course_allocation.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management", description = "APIs for managing academic departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Create new department", description = "Create a new academic department (Admin only)")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@RequestBody DepartmentRequest request) {
        try {
            DepartmentResponse department = departmentService.createDepartment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Department created successfully", department));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve department details by ID")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {
        try {
            DepartmentResponse department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Department found", department));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping
    @Operation(summary = "List all departments", description = "Get list of all academic departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(new ApiResponse<>(true, "Departments retrieved successfully", departments));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department", description = "Update department details (Admin only)")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentRequest request) {
        try {
            DepartmentResponse department = departmentService.updateDepartment(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Department updated successfully", department));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Delete a department (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Department deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/courses")
    @Operation(summary = "Get department courses", description = "Get all courses in a department")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getDepartmentCourses(@PathVariable Long id) {
        try {
            List<CourseResponse> courses = departmentService.getDepartmentCourses(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Courses retrieved successfully", courses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
