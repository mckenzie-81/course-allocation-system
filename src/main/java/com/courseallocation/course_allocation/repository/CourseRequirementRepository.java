package com.courseallocation.course_allocation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.CourseRequirement;

@Repository
public interface CourseRequirementRepository extends JpaRepository<CourseRequirement, Long> {
    List<CourseRequirement> findByCourseId(Long courseId);
    List<CourseRequirement> findByPrerequisiteCourseId(Long prerequisiteCourseId);
}
