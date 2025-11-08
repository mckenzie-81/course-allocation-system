package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.EnrollmentRequest;
import com.courseallocation.course_allocation.model.enums.RequestStatus;

@Repository
public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {
    List<EnrollmentRequest> findByStudentId(Long studentId);
    List<EnrollmentRequest> findByCourseId(Long courseId);
    List<EnrollmentRequest> findByStatus(RequestStatus status);
    List<EnrollmentRequest> findByStudentIdAndStatus(Long studentId, RequestStatus status);
    List<EnrollmentRequest> findByCourseIdAndStatus(Long courseId, RequestStatus status);
    Optional<EnrollmentRequest> findByStudentIdAndCourseId(Long studentId, Long courseId);
    long countByCourseIdAndStatus(Long courseId, RequestStatus status);
}
