package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Department;
import com.courseallocation.course_allocation.model.Semester;
import com.courseallocation.course_allocation.model.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByDepartment(Department department);
    List<Course> findByDepartmentId(Long departmentId);
    List<Course> findByAssignedLecturer(User assignedLecturer);
    List<Course> findByAssignedLecturerId(Long lecturerId);
    List<Course> findBySemester(Semester semester);
    List<Course> findBySemesterId(Long semesterId);
    List<Course> findByTitleContainingIgnoreCase(String title);
    List<Course> findByDepartmentIdAndLevelAndSemesterId(Long departmentId, Integer level, Long semesterId);
    boolean existsByCourseCode(String courseCode);
}

