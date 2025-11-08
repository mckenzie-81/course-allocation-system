package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.EnrollmentRequestDto;
import com.courseallocation.course_allocation.dto.EnrollmentRequestResponse;
import com.courseallocation.course_allocation.dto.EnrollmentRequestStatusUpdate;
import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Enrollment;
import com.courseallocation.course_allocation.model.EnrollmentRequest;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.model.enums.EnrollmentStatus;
import com.courseallocation.course_allocation.model.enums.RequestStatus;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRequestRepository;
import com.courseallocation.course_allocation.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentRequestService {

    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentRequestResponse createRequest(EnrollmentRequestDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if request already exists
        enrollmentRequestRepository.findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Enrollment request already exists for this course");
                });

        // Check if already enrolled
        if (enrollmentRepository.findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId()).isPresent()) {
            throw new RuntimeException("Already enrolled in this course");
        }

        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudent(student);
        request.setCourse(course);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        request.setValidationErrors(dto.getRequestReason());

        EnrollmentRequest saved = enrollmentRequestRepository.save(request);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentRequestResponse> getStudentRequests(Long studentId) {
        return enrollmentRequestRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentRequestResponse> getCourseRequests(Long courseId) {
        return enrollmentRequestRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentRequestResponse> getPendingRequests() {
        return enrollmentRequestRepository.findByStatus(RequestStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnrollmentRequestResponse getRequestById(Long id) {
        EnrollmentRequest request = enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment request not found"));
        return mapToResponse(request);
    }

    public EnrollmentRequestResponse updateRequestStatus(Long id, EnrollmentRequestStatusUpdate update) {
        EnrollmentRequest request = enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment request not found"));

        RequestStatus newStatus = RequestStatus.valueOf(update.getStatus());
        request.setStatus(newStatus);
        request.setRejectionReason(update.getReason());

        // If approved, create enrollment
        if (newStatus == RequestStatus.APPROVED) {
            createEnrollmentFromRequest(request);
        }

        EnrollmentRequest updated = enrollmentRequestRepository.save(request);
        return mapToResponse(updated);
    }

    private void createEnrollmentFromRequest(EnrollmentRequest request) {
        // Check if enrollment doesn't already exist
        if (enrollmentRepository.findByStudentIdAndCourseId(
                request.getStudent().getId(), 
                request.getCourse().getId()).isPresent()) {
            return; // Already enrolled
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(request.getStudent());
        enrollment.setCourse(request.getCourse());
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollmentRepository.save(enrollment);
    }

    public void cancelRequest(Long id) {
        EnrollmentRequest request = enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Can only cancel pending requests");
        }

        enrollmentRequestRepository.delete(request);
    }

    public List<EnrollmentRequestResponse> bulkApprove(List<Long> requestIds) {
        return requestIds.stream()
                .map(id -> {
                    EnrollmentRequestStatusUpdate update = new EnrollmentRequestStatusUpdate();
                    update.setStatus("APPROVED");
                    return updateRequestStatus(id, update);
                })
                .collect(Collectors.toList());
    }

    private EnrollmentRequestResponse mapToResponse(EnrollmentRequest request) {
        EnrollmentRequestResponse response = new EnrollmentRequestResponse();
        response.setId(request.getId());
        response.setStudentId(request.getStudent().getId());
        response.setStudentIdNumber(request.getStudent().getStudentId());
        
        if (request.getStudent().getUser() != null) {
            response.setStudentName(request.getStudent().getUser().getFirstName() + " " + 
                    request.getStudent().getUser().getLastName());
        }
        
        response.setCourseId(request.getCourse().getId());
        response.setCourseCode(request.getCourse().getCourseCode());
        response.setCourseTitle(request.getCourse().getTitle());
        response.setRequestStatus(request.getStatus().name());
        response.setRequestReason(request.getValidationErrors());
        response.setRejectionReason(request.getRejectionReason());
        response.setRequestedAt(request.getRequestDate());
        response.setProcessedAt(request.getUpdatedAt());
        
        return response;
    }
}
