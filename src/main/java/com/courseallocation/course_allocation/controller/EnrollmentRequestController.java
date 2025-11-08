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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseallocation.course_allocation.dto.ApiResponse;
import com.courseallocation.course_allocation.dto.EnrollmentRequestDto;
import com.courseallocation.course_allocation.dto.EnrollmentRequestResponse;
import com.courseallocation.course_allocation.dto.EnrollmentRequestStatusUpdate;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.service.AuthenticationService;
import com.courseallocation.course_allocation.service.EnrollmentRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollment-requests")
@RequiredArgsConstructor
@Tag(name = "Enrollment Requests", description = "Manage enrollment request lifecycle and approval workflow")
public class EnrollmentRequestController {

    private final EnrollmentRequestService enrollmentRequestService;
    private final AuthenticationService authenticationService;

    @PostMapping
    @Operation(summary = "Submit enrollment request", description = "Student submits request to enroll in a course")
    public ResponseEntity<ApiResponse<EnrollmentRequestResponse>> createRequest(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody EnrollmentRequestDto dto) {
        try {
            String token = authHeader.substring(7);
            Student student = authenticationService.getStudentByToken(token);
            dto.setStudentId(student.getId()); // Override with authenticated student
            
            EnrollmentRequestResponse response = enrollmentRequestService.createRequest(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Enrollment request submitted", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping
    @Operation(summary = "List all enrollment requests", description = "Get all enrollment requests (Admin/Registrar)")
    public ResponseEntity<ApiResponse<List<EnrollmentRequestResponse>>> getAllRequests() {
        try {
            List<EnrollmentRequestResponse> requests = enrollmentRequestService.getPendingRequests();
            return ResponseEntity.ok(new ApiResponse<>(true, "Requests retrieved", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment request by ID", description = "Retrieve specific enrollment request details")
    public ResponseEntity<ApiResponse<EnrollmentRequestResponse>> getRequestById(@PathVariable Long id) {
        try {
            EnrollmentRequestResponse request = enrollmentRequestService.getRequestById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Request found", request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending requests", description = "List all requests awaiting approval")
    public ResponseEntity<ApiResponse<List<EnrollmentRequestResponse>>> getPendingRequests() {
        try {
            List<EnrollmentRequestResponse> requests = enrollmentRequestService.getPendingRequests();
            return ResponseEntity.ok(new ApiResponse<>(true, "Pending requests retrieved", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get requests for course", description = "List all enrollment requests for a specific course")
    public ResponseEntity<ApiResponse<List<EnrollmentRequestResponse>>> getCourseRequests(@PathVariable Long courseId) {
        try {
            List<EnrollmentRequestResponse> requests = enrollmentRequestService.getCourseRequests(courseId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Course requests retrieved", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update request status", description = "Approve, reject, or waitlist an enrollment request")
    public ResponseEntity<ApiResponse<EnrollmentRequestResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentRequestStatusUpdate update) {
        try {
            EnrollmentRequestResponse response = enrollmentRequestService.updateRequestStatus(id, update);
            return ResponseEntity.ok(new ApiResponse<>(true, "Request status updated", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/bulk-approve")
    @Operation(summary = "Bulk approve requests", description = "Approve multiple enrollment requests at once")
    public ResponseEntity<ApiResponse<List<EnrollmentRequestResponse>>> bulkApprove(
            @RequestBody List<Long> requestIds) {
        try {
            List<EnrollmentRequestResponse> responses = enrollmentRequestService.bulkApprove(requestIds);
            return ResponseEntity.ok(new ApiResponse<>(true, "Requests approved", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel enrollment request", description = "Student cancels their pending enrollment request")
    public ResponseEntity<ApiResponse<Void>> cancelRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            enrollmentRequestService.cancelRequest(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Request cancelled", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
