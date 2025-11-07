package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Enrollment;
import com.courseallocation.course_allocation.model.Student;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
    boolean existsByStudentAndCourse(Student student, Course course);
    long countByCourse(Course course);
    long countByCourseId(Long courseId);
}

