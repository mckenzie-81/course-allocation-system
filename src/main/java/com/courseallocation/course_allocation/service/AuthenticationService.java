package com.courseallocation.course_allocation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.config.JwtTokenProvider;
import com.courseallocation.course_allocation.dto.LoginRequest;
import com.courseallocation.course_allocation.dto.LoginResponse;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.model.User;
import com.courseallocation.course_allocation.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final StudentRepository studentRepository;
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
}
