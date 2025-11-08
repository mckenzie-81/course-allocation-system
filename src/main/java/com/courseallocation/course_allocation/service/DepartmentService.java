package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.CourseResponse;
import com.courseallocation.course_allocation.dto.DepartmentRequest;
import com.courseallocation.course_allocation.dto.DepartmentResponse;
import com.courseallocation.course_allocation.model.Department;
import com.courseallocation.course_allocation.model.User;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.DepartmentRepository;
import com.courseallocation.course_allocation.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        // Check if department code already exists
        if (departmentRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Department with code " + request.getCode() + " already exists");
        }

        Department department = new Department();
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setCreatedAt(LocalDateTime.now());

        // Set HOD if provided
        if (request.getHodUserId() != null) {
            User hod = userRepository.findById(request.getHodUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getHodUserId()));
            department.setHod(hod);
        }

        Department savedDepartment = departmentRepository.save(department);
        return mapToResponse(savedDepartment);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return mapToResponse(department);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Department not found with code: " + code));
        return mapToResponse(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if new code conflicts with existing department
        if (!department.getCode().equals(request.getCode())) {
            departmentRepository.findByCode(request.getCode()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("Department with code " + request.getCode() + " already exists");
                }
            });
        }

        department.setCode(request.getCode());
        department.setName(request.getName());

        // Update HOD if provided
        if (request.getHodUserId() != null) {
            User hod = userRepository.findById(request.getHodUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getHodUserId()));
            department.setHod(hod);
        }

        Department updatedDepartment = departmentRepository.save(department);
        return mapToResponse(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if department has courses
        long courseCount = courseRepository.findByDepartmentId(id).size();
        if (courseCount > 0) {
            throw new RuntimeException("Cannot delete department with existing courses. Remove courses first.");
        }

        departmentRepository.delete(department);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getDepartmentCourses(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        return courseRepository.findByDepartmentId(id).stream()
                .map(course -> {
                    CourseResponse response = new CourseResponse();
                    response.setId(course.getId());
                    response.setCourseCode(course.getCourseCode());
                    response.setTitle(course.getTitle());
                    response.setDescription(course.getDescription());
                    response.setCredits(course.getCredits());
                    response.setLevel(course.getLevel());
                    response.setMaxCapacity(course.getMaxCapacity());
                    response.setDepartmentCode(course.getDepartment().getCode());
                    response.setDepartmentName(course.getDepartment().getName());
                    
                    // Count current enrollment
                    long enrollment = courseRepository.findById(course.getId())
                            .map(c -> (int) c.getEnrollments().size())
                            .orElse(0);
                    response.setCurrentEnrollment((int) enrollment);
                    
                    if (course.getSemester() != null) {
                        response.setSemesterId(course.getSemester().getId());
                        response.setSemesterCode(course.getSemester().getSemesterCode());
                    }
                    
                    response.setAcademicYear(course.getAcademicYear());
                    
                    if (course.getAssignedLecturer() != null) {
                        response.setAssignedLecturerId(course.getAssignedLecturer().getId());
                        response.setLecturerName(course.getAssignedLecturer().getFirstName() + " " + 
                                                 course.getAssignedLecturer().getLastName());
                    }
                    
                    response.setStatus(course.getStatus() != null ? course.getStatus().name() : null);
                    response.setCreatedAt(course.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private DepartmentResponse mapToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setCode(department.getCode());
        response.setName(department.getName());
        response.setCreatedAt(department.getCreatedAt());

        if (department.getHod() != null) {
            response.setHodUserId(department.getHod().getId());
            response.setHodName(department.getHod().getFirstName() + " " + department.getHod().getLastName());
        }

        // Count courses and lecturers
        response.setTotalCourses((int) courseRepository.findByDepartmentId(department.getId()).size());
        response.setTotalLecturers((int) userRepository.findByDepartmentId(department.getId()).size());

        return response;
    }
}
