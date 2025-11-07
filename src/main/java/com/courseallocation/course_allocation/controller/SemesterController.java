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
import com.courseallocation.course_allocation.dto.SemesterRequest;
import com.courseallocation.course_allocation.dto.SemesterResponse;
import com.courseallocation.course_allocation.service.SemesterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
@Tag(name = "Semester Management", description = "APIs for managing semesters")
public class SemesterController {

    private final SemesterService semesterService;

    @PostMapping
    @Operation(summary = "Create a new semester", description = "Register a new semester in the system")
    public ResponseEntity<ApiResponse<SemesterResponse>> createSemester(@Valid @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.createSemester(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Semester created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get semester by ID", description = "Retrieve semester information by ID")
    public ResponseEntity<ApiResponse<SemesterResponse>> getSemesterById(@PathVariable Long id) {
        SemesterResponse response = semesterService.getSemesterById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Semester retrieved successfully", response));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active semester", description = "Retrieve the currently active semester")
    public ResponseEntity<ApiResponse<SemesterResponse>> getActiveSemester() {
        SemesterResponse response = semesterService.getActiveSemester();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active semester retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all semesters", description = "Retrieve a list of all semesters")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getAllSemesters() {
        List<SemesterResponse> response = semesterService.getAllSemesters();
        return ResponseEntity.ok(new ApiResponse<>(true, "Semesters retrieved successfully", response));
    }

    @GetMapping("/active/list")
    @Operation(summary = "Get all active semesters", description = "Retrieve a list of all active semesters")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getActiveSemesters() {
        List<SemesterResponse> response = semesterService.getActiveSemesters();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active semesters retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update semester", description = "Update semester information")
    public ResponseEntity<ApiResponse<SemesterResponse>> updateSemester(
            @PathVariable Long id,
            @Valid @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.updateSemester(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Semester updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete semester", description = "Remove a semester from the system")
    public ResponseEntity<ApiResponse<Void>> deleteSemester(@PathVariable Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Semester deleted successfully", null));
    }
}

