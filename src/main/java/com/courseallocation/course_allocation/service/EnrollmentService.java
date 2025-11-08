package com.courseallocation.course_allocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.EnrollmentResponse;
import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Enrollment;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.model.enums.EnrollmentStatus;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRepository;
import com.courseallocation.course_allocation.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentResponse enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        long currentEnrollment = enrollmentRepository.countByCourseId(courseId);
        if (currentEnrollment >= course.getMaxCapacity()) {
            throw new RuntimeException("Course is full");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponse(saved);
    }

    public List<EnrollmentResponse> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        Student student = enrollment.getStudent();
        
        return new EnrollmentResponse(
                enrollment.getId(),
                student != null ? student.getId() : null,
                student != null ? student.getStudentId() : null,
                enrollment.getCourse().getId(),
                enrollment.getCourse().getCourseCode(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus() != null ? enrollment.getStatus().name() : null,
                enrollment.getFinalGrade(),
                enrollment.getCreatedAt()
        );
    }
}
