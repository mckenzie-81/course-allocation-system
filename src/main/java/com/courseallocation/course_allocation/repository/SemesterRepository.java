package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findBySemesterCode(String semesterCode);
    List<Semester> findByIsActive(Boolean isActive);
    Optional<Semester> findFirstByIsActiveTrue();
    boolean existsBySemesterCode(String semesterCode);
}

