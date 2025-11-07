package com.courseallocation.course_allocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.SemesterRequest;
import com.courseallocation.course_allocation.dto.SemesterResponse;
import com.courseallocation.course_allocation.model.Semester;
import com.courseallocation.course_allocation.repository.SemesterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public SemesterResponse createSemester(SemesterRequest request) {
        if (semesterRepository.existsBySemesterCode(request.getSemesterCode())) {
            throw new RuntimeException("Semester with code " + request.getSemesterCode() + " already exists");
        }

        if (request.getIsActive() != null && request.getIsActive()) {
            semesterRepository.findByIsActive(true).forEach(s -> {
                s.setIsActive(false);
                semesterRepository.save(s);
            });
        }

        Semester semester = new Semester();
        semester.setSemesterCode(request.getSemesterCode());
        semester.setName(request.getName());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        semester.setIsActive(request.getIsActive() != null ? request.getIsActive() : false);

        Semester saved = semesterRepository.save(semester);
        return mapToResponse(saved);
    }

    public SemesterResponse getSemesterById(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));
        return mapToResponse(semester);
    }

    public SemesterResponse getActiveSemester() {
        Semester semester = semesterRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active semester found"));
        return mapToResponse(semester);
    }

    public List<SemesterResponse> getAllSemesters() {
        return semesterRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<SemesterResponse> getActiveSemesters() {
        return semesterRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SemesterResponse updateSemester(Long id, SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));

        if (!semester.getSemesterCode().equals(request.getSemesterCode()) && 
            semesterRepository.existsBySemesterCode(request.getSemesterCode())) {
            throw new RuntimeException("Semester with code " + request.getSemesterCode() + " already exists");
        }

        if (request.getIsActive() != null && request.getIsActive() && !semester.getIsActive()) {
            semesterRepository.findByIsActive(true).forEach(s -> {
                s.setIsActive(false);
                semesterRepository.save(s);
            });
        }

        semester.setSemesterCode(request.getSemesterCode());
        semester.setName(request.getName());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        if (request.getIsActive() != null) {
            semester.setIsActive(request.getIsActive());
        }

        Semester updated = semesterRepository.save(semester);
        return mapToResponse(updated);
    }

    public void deleteSemester(Long id) {
        if (!semesterRepository.existsById(id)) {
            throw new RuntimeException("Semester not found with id: " + id);
        }
        semesterRepository.deleteById(id);
    }

    private SemesterResponse mapToResponse(Semester semester) {
        return new SemesterResponse(
                semester.getId(),
                semester.getSemesterCode(),
                semester.getName(),
                semester.getStartDate(),
                semester.getEndDate(),
                semester.getIsActive(),
                semester.getCreatedAt(),
                semester.getUpdatedAt()
        );
    }
}

