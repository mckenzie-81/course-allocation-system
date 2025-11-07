package com.courseallocation.course_allocation.model;

import java.time.LocalDateTime;

import com.courseallocation.course_allocation.model.enums.RequirementType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_requirements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_course_id")
    @JsonIgnore
    private Course prerequisiteCourse;

    @Column(length = 5)
    private String minGrade; // e.g., "C+", "B"

    private Integer minCreditsCompleted;

    private Integer requiredYear; // 1, 2, 3, or 4

    @Column(length = 100)
    private String requiredProgram; // e.g., "Computer Science"

    private Double minGPA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequirementType requirementType;

    @Column(nullable = false)
    private Boolean isMandatory = true;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
