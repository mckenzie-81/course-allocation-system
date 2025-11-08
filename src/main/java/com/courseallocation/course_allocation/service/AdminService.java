package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.EnrollmentResponse;
import com.courseallocation.course_allocation.dto.ForceEnrollmentRequest;
import com.courseallocation.course_allocation.dto.SystemStatisticsResponse;
import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Enrollment;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.model.enums.EnrollmentStatus;
import com.courseallocation.course_allocation.model.enums.RequestStatus;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.DepartmentRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRequestRepository;
import com.courseallocation.course_allocation.repository.SemesterRepository;
import com.courseallocation.course_allocation.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final EnrollmentRequestRepository enrollmentRequestRepository;

    @Transactional(readOnly = true)
    public SystemStatisticsResponse getSystemStatistics() {
        SystemStatisticsResponse stats = new SystemStatisticsResponse();
        
        stats.setTotalStudents((int) studentRepository.count());
        stats.setTotalCourses((int) courseRepository.count());
        stats.setTotalEnrollments((int) enrollmentRepository.count());
        stats.setTotalDepartments((int) departmentRepository.count());
        
        long activeSemesters = semesterRepository.findAll().stream()
                .filter(s -> s.getIsActive())
                .count();
        stats.setActiveSemesters((int) activeSemesters);
        
        long pendingRequests = enrollmentRequestRepository.findByStatus(RequestStatus.PENDING).size();
        stats.setPendingEnrollmentRequests((int) pendingRequests);
        
        // Calculate average GPA
        Double avgGPA = studentRepository.findAll().stream()
                .map(Student::getCurrentGPA)
                .filter(gpa -> gpa != null && gpa > 0)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        stats.setAverageGPA(Math.round(avgGPA * 100.0) / 100.0);
        
        // Calculate total credits allocated
        int totalCredits = enrollmentRepository.findAll().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.COMPLETED)
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        stats.setTotalCreditsAllocated(totalCredits);
        
        return stats;
    }

    public EnrollmentResponse forceEnroll(ForceEnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if already enrolled
        if (enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId()).isPresent()) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        // Force enrollment regardless of capacity or prerequisites
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        
        Enrollment saved = enrollmentRepository.save(enrollment);
        
        return mapToEnrollmentResponse(saved);
    }

    public void forceDropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    public void updateCourseEmergencyCapacity(Long courseId, Integer newCapacity) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setMaxCapacity(newCapacity);
        courseRepository.save(course);
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentIdNumber(enrollment.getStudent().getStudentId());
        response.setCourseId(enrollment.getCourse().getId());
        response.setCourseCode(enrollment.getCourse().getCourseCode());
        response.setCourseName(enrollment.getCourse().getTitle());
        response.setStatus(enrollment.getStatus().name());
        response.setGrade(enrollment.getFinalGrade());
        response.setCreatedAt(enrollment.getEnrollmentDate());
        return response;
    }
}
