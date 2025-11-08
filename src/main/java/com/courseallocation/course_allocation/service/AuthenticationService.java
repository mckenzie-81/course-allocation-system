package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.config.JwtTokenProvider;
import com.courseallocation.course_allocation.dto.LoginRequest;
import com.courseallocation.course_allocation.dto.LoginResponse;
import com.courseallocation.course_allocation.dto.RegisterRequest;
import com.courseallocation.course_allocation.dto.UserProfileResponse;
import com.courseallocation.course_allocation.model.Department;
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
public class AuthenticationService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        Student student = studentRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Invalid student ID or PIN"));

        // TODO: Add proper password encoding and verification
        if (!student.getPin().equals(request.getPin())) {
            throw new RuntimeException("Invalid student ID or PIN");
        }

        String token = jwtTokenProvider.generateToken(student.getId(), student.getStudentId());

        User user = student.getUser();
        return new LoginResponse(
                student.getId(),
                student.getStudentId(),
                user != null ? user.getFirstName() : null,
                user != null ? user.getLastName() : null,
                user != null ? user.getEmail() : null,
                user != null && user.getDepartment() != null ? user.getDepartment().getCode() : null,
                student.getYearOfStudy(),
                token
        );
    }

    public Student getStudentByToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
        Long studentId = jwtTokenProvider.getStudentIdFromToken(token);
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public LoginResponse register(RegisterRequest request) {
        // Check if student ID already exists
        if (studentRepository.findByStudentId(request.getStudentId()).isPresent()) {
            throw new RuntimeException("Student with this ID already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create User entity
        User user = new User();
        user.setUsername(request.getStudentId());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPin()); // TODO: Add password encoding
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.STUDENT);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Set department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(department);
        }

        User savedUser = userRepository.save(user);

        // Create Student entity
        Student student = new Student();
        student.setStudentId(request.getStudentId());
        student.setPin(request.getPin()); // TODO: Add password encoding
        student.setUser(savedUser);
        student.setProgram(request.getProgram());
        student.setYearOfStudy(request.getYearOfStudy() != null ? request.getYearOfStudy() : 1);
        student.setCreditsCompleted(0);
        student.setCurrentGPA(0.0);
        student.setCreatedAt(LocalDateTime.now());

        Student savedStudent = studentRepository.save(student);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedStudent.getId(), savedStudent.getStudentId());

        return new LoginResponse(
                savedStudent.getId(),
                savedStudent.getStudentId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getDepartment() != null ? savedUser.getDepartment().getCode() : null,
                savedStudent.getYearOfStudy(),
                token
        );
    }

    public String refreshToken(String oldToken) {
        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new RuntimeException("Invalid or expired token");
        }

        Long studentId = jwtTokenProvider.getStudentIdFromToken(oldToken);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Generate new token
        return jwtTokenProvider.generateToken(student.getId(), student.getStudentId());
    }

    public UserProfileResponse getProfile(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        Long studentId = jwtTokenProvider.getStudentIdFromToken(token);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User user = student.getUser();
        
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(student.getId());
        profile.setStudentId(student.getStudentId());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        
        if (user.getDepartment() != null) {
            profile.setDepartmentCode(user.getDepartment().getCode());
            profile.setDepartmentName(user.getDepartment().getName());
        }
        
        profile.setProgram(student.getProgram());
        profile.setYearOfStudy(student.getYearOfStudy());
        profile.setCreditsCompleted(student.getCreditsCompleted());
        profile.setCurrentGPA(student.getCurrentGPA());
        profile.setRole(user.getRole().name());
        
        return profile;
    }
}
