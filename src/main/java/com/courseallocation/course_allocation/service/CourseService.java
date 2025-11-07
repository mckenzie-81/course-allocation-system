package com.courseallocation.course_allocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.CourseRequest;
import com.courseallocation.course_allocation.dto.CourseResponse;
import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Department;
import com.courseallocation.course_allocation.model.Semester;
import com.courseallocation.course_allocation.model.User;
import com.courseallocation.course_allocation.model.enums.CourseStatus;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.DepartmentRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRepository;
import com.courseallocation.course_allocation.repository.SemesterRepository;
import com.courseallocation.course_allocation.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new RuntimeException("Course with code " + request.getCourseCode() + " already exists");
        }

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Semester not found"));

        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLevel(request.getLevel());
        course.setCredits(request.getCredits());
        course.setMaxCapacity(request.getMaxCapacity());
        course.setSemester(semester);
        course.setAcademicYear(request.getAcademicYear());
        course.setStatus(CourseStatus.DRAFT);

        // Set department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            course.setDepartment(department);
        }

        // Set assigned lecturer if provided
        if (request.getAssignedLecturerId() != null) {
            User lecturer = userRepository.findById(request.getAssignedLecturerId())
                    .orElseThrow(() -> new RuntimeException("Lecturer not found"));
            course.setAssignedLecturer(lecturer);
        }

        Course saved = courseRepository.save(course);
        return mapToResponse(saved);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return mapToResponse(course);
    }

    public CourseResponse getCourseByCourseCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
        return mapToResponse(course);
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByDepartmentId(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesBySemesterId(Long semesterId) {
        return courseRepository.findBySemesterId(semesterId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByTitleContaining(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        if (!course.getCourseCode().equals(request.getCourseCode()) &&
            courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new RuntimeException("Course with code " + request.getCourseCode() + " already exists");
        }

        course.setCourseCode(request.getCourseCode());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLevel(request.getLevel());
        course.setCredits(request.getCredits());
        course.setMaxCapacity(request.getMaxCapacity());
        course.setAcademicYear(request.getAcademicYear());

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new RuntimeException("Semester not found"));
            course.setSemester(semester);
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            course.setDepartment(department);
        }

        if (request.getAssignedLecturerId() != null) {
            User lecturer = userRepository.findById(request.getAssignedLecturerId())
                    .orElseThrow(() -> new RuntimeException("Lecturer not found"));
            course.setAssignedLecturer(lecturer);
        }

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseResponse mapToResponse(Course course) {
        long currentEnrollment = enrollmentRepository.countByCourseId(course.getId());
        
        return new CourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getCredits(),
                course.getMaxCapacity(),
                (int) currentEnrollment,
                course.getDepartment() != null ? course.getDepartment().getCode() : null,
                course.getDepartment() != null ? course.getDepartment().getName() : null,
                course.getAssignedLecturer() != null ? course.getAssignedLecturer().getId() : null,
                course.getAssignedLecturer() != null ? 
                    course.getAssignedLecturer().getFirstName() + " " + course.getAssignedLecturer().getLastName() : null,
                course.getSemester() != null ? course.getSemester().getId() : null,
                course.getSemester() != null ? course.getSemester().getSemesterCode() : null,
                course.getAcademicYear(),
                course.getStatus() != null ? course.getStatus().name() : null,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
