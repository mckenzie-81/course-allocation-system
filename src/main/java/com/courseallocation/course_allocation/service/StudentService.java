package com.courseallocation.course_allocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.StudentRequest;
import com.courseallocation.course_allocation.dto.StudentResponse;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.model.User;
import com.courseallocation.course_allocation.model.enums.UserRole;
import com.courseallocation.course_allocation.repository.DepartmentRepository;
import com.courseallocation.course_allocation.repository.StudentRepository;
import com.courseallocation.course_allocation.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByStudentId(request.getStudentId())) {
            throw new RuntimeException("Student with ID " + request.getStudentId() + " already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        if (userRepository.existsByUsername(request.getStudentId())) {
            throw new RuntimeException("Username " + request.getStudentId() + " already exists");
        }

        // Create User entity first
        User user = new User();
        user.setUsername(request.getStudentId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPin()); // TODO: Add password encoding
        user.setRole(UserRole.STUDENT);
        user.setIsActive(true);

        // Set department if provided
        if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            departmentRepository.findByCode(request.getDepartment())
                    .ifPresent(user::setDepartment);
        }

        user = userRepository.save(user);

        // Create Student entity
        Student student = new Student();
        student.setUser(user);
        student.setStudentId(request.getStudentId());
        student.setProgram(request.getProgram() != null ? request.getProgram() : "General");
        student.setYearOfStudy(request.getYear() != null ? request.getYear() : 1);
        student.setCreditsCompleted(0);
        student.setPin(request.getPin()); // TODO: Add password encoding

        Student saved = studentRepository.save(student);
        return mapToResponse(saved);
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return mapToResponse(student);
    }

    public StudentResponse getStudentByStudentId(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with student ID: " + studentId));
        return mapToResponse(student);
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StudentResponse> getStudentsByProgram(String program) {
        return studentRepository.findByProgram(program).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StudentResponse> getStudentsByYear(Integer year) {
        return studentRepository.findByYearOfStudy(year).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        User user = student.getUser();
        if (user == null) {
            throw new RuntimeException("Student has no associated user");
        }

        // Update user information
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email " + request.getEmail() + " already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            departmentRepository.findByCode(request.getDepartment())
                    .ifPresent(user::setDepartment);
        }

        userRepository.save(user);

        // Update student information
        if (request.getProgram() != null) {
            student.setProgram(request.getProgram());
        }
        if (request.getYear() != null) {
            student.setYearOfStudy(request.getYear());
        }
        if (request.getPin() != null && !request.getPin().isEmpty()) {
            student.setPin(request.getPin()); // TODO: Add password encoding
            user.setPassword(request.getPin()); // TODO: Add password encoding
            userRepository.save(user);
        }

        Student updated = studentRepository.save(student);
        return mapToResponse(updated);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        // Delete student (this will also handle the user via cascade if configured)
        studentRepository.delete(student);
    }

    private StudentResponse mapToResponse(Student student) {
        User user = student.getUser();
        return new StudentResponse(
                student.getId(),
                student.getStudentId(),
                user != null ? user.getFirstName() : null,
                user != null ? user.getLastName() : null,
                user != null ? user.getEmail() : null,
                user != null && user.getDepartment() != null ? user.getDepartment().getCode() : null,
                student.getYearOfStudy(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}
