package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    List<Student> findByProgram(String program);
    List<Student> findByYearOfStudy(Integer yearOfStudy);
    List<Student> findByUser_Department_Id(Long departmentId);
    boolean existsByStudentId(String studentId);
}

