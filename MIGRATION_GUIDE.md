# Migration Guide: Course Selection to Course Allocation System

This guide documents all changes needed to transform the existing Course Selection System into the comprehensive Course Allocation System.

## Overview of Changes

The system is being refactored from a simple course selection system to a multi-phase, role-based course allocation system with the following key additions:

1. **New User Roles**: HOD, LECTURER, ADMIN in addition to STUDENT
2. **Department Management**: Courses organized by departments
3. **Course Requirements System**: Prerequisites, co-requisites, GPA requirements
4. **Enrollment Request Workflow**: Students request, system allocates
5. **Priority-Based Allocation**: Fair allocation using calculated priorities
6. **Audit Logging**: Complete trail of all system actions

## Changes Summary

### Completed

1. Created new enums in `model/enums/`:
   - `UserRole.java`
   - `CourseStatus.java`
   - `RequestStatus.java`
   - `EnrollmentStatus.java`
   - `RequirementType.java`

2. Created new entities in `model/`:
   - `User.java` - System users with roles
   - `Department.java` - Academic departments
   - `CourseRequirement.java` - Course prerequisites and requirements
   - `EnrollmentRequest.java` - Student enrollment requests
   - `AuditLog.java` - System audit trail

3. Updated existing entities:
   - `Student.java` - Added link to User, program, GPA, credits
   - `Course.java` - Added status, lecturer assignment, requirements list
   - `Enrollment.java` - Added proper status enum, grade field

4. Updated README.md with comprehensive documentation

### 🔄 In Progress / To Do

#### 1. Create New Repositories

Create these repository interfaces in `repository/`:

**UserRepository.java**
```java
package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.User;
import com.courseallocation.course_allocation.model.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByDepartmentId(Long departmentId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

**DepartmentRepository.java**
```java
package com.courseallocation.course_allocation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
    Optional<Department> findByName(String name);
    boolean existsByCode(String code);
}
```

**CourseRequirementRepository.java**
```java
package com.courseallocation.course_allocation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.CourseRequirement;
import com.courseallocation.course_allocation.model.enums.RequirementType;

@Repository
public interface CourseRequirementRepository extends JpaRepository<CourseRequirement, Long> {
    List<CourseRequirement> findByCourseId(Long courseId);
    List<CourseRequirement> findByCourseIdAndRequirementType(Long courseId, RequirementType type);
    List<CourseRequirement> findByCourseIdAndIsMandatory(Long courseId, Boolean isMandatory);
}
```

**EnrollmentRequestRepository.java**
```java
package com.courseallocation.course_allocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.EnrollmentRequest;
import com.courseallocation.course_allocation.model.enums.RequestStatus;

@Repository
public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {
    List<EnrollmentRequest> findByStudentId(Long studentId);
    List<EnrollmentRequest> findByCourseId(Long courseId);
    List<EnrollmentRequest> findByStatus(RequestStatus status);
    List<EnrollmentRequest> findByStudentIdAndStatus(Long studentId, RequestStatus status);
    Optional<EnrollmentRequest> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    @Query("SELECT er FROM EnrollmentRequest er WHERE er.course.semester.id = :semesterId AND er.status = :status ORDER BY er.priority DESC")
    List<EnrollmentRequest> findBySemesterAndStatusOrderByPriorityDesc(@Param("semesterId") Long semesterId, @Param("status") RequestStatus status);
}
```

**AuditLogRepository.java**
```java
package com.courseallocation.course_allocation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courseallocation.course_allocation.model.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByActorId(Long actorId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
```

#### 2. Update Existing Repositories

**Update StudentRepository.java** - Add method for finding by User:
```java
Optional<Student> findByUserId(Long userId);
Optional<Student> findByUser(User user);
```

**Update CourseRepository.java** - Add methods for new fields:
```java
List<Course> findByStatus(CourseStatus status);
List<Course> findByAssignedLecturerId(Long lecturerId);
List<Course> findByDepartmentId(Long departmentId);
List<Course> findBySemesterIdAndStatus(Long semesterId, CourseStatus status);
```

**Update EnrollmentRepository.java** - Add status enum methods:
```java
List<Enrollment> findByStatus(EnrollmentStatus status);
List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
```

#### 3. Create New Services

**ValidationService.java** - Core validation logic:
```java
package com.courseallocation.course_allocation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.ValidationResultDTO;
import com.courseallocation.course_allocation.model.*;
import com.courseallocation.course_allocation.model.enums.*;
import com.courseallocation.course_allocation.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidationService {

    private final CourseRequirementRepository requirementRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ValidationResultDTO validateEnrollment(Student student, Course course) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Check course status
        if (course.getStatus() != CourseStatus.ACTIVE) {
            errors.add("Course is not active for enrollment");
        }

        // Check capacity
        if (!course.hasAvailableSeats()) {
            errors.add("Course is full");
        }

        // Validate prerequisites
        List<CourseRequirement> requirements = requirementRepository.findByCourseId(course.getId());
        for (CourseRequirement req : requirements) {
            validateRequirement(student, req, errors, warnings);
        }

        // Check credit limits
        int currentCredits = calculateCurrentSemesterCredits(student, course.getSemester().getId());
        if (currentCredits + course.getCredits() > 18) {
            errors.add("Exceeds maximum credit limit (18)");
        }

        return new ValidationResultDTO(errors.isEmpty(), errors, warnings);
    }

    private void validateRequirement(Student student, CourseRequirement req, 
                                    List<String> errors, List<String> warnings) {
        switch (req.getRequirementType()) {
            case PREREQUISITE:
                validatePrerequisite(student, req, errors, warnings);
                break;
            case YEAR:
                validateYear(student, req, errors, warnings);
                break;
            case CREDIT:
                validateCredit(student, req, errors, warnings);
                break;
            case GPA:
                validateGPA(student, req, errors, warnings);
                break;
            case PROGRAM:
                validateProgram(student, req, errors, warnings);
                break;
        }
    }

    private void validatePrerequisite(Student student, CourseRequirement req,
                                     List<String> errors, List<String> warnings) {
        if (req.getPrerequisiteCourse() == null) return;

        Enrollment prereqEnrollment = enrollmentRepository
            .findByStudentIdAndCourseId(student.getId(), req.getPrerequisiteCourse().getId())
            .orElse(null);

        if (prereqEnrollment == null || prereqEnrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            String message = "Missing prerequisite: " + req.getPrerequisiteCourse().getTitle();
            if (req.getIsMandatory()) {
                errors.add(message);
            } else {
                warnings.add(message);
            }
            return;
        }

        // Check minimum grade if specified
        if (req.getMinGrade() != null && !meetsMinimumGrade(prereqEnrollment.getFinalGrade(), req.getMinGrade())) {
            String message = "Prerequisite grade requirement not met. Required: " + req.getMinGrade() 
                           + ", Achieved: " + prereqEnrollment.getFinalGrade();
            if (req.getIsMandatory()) {
                errors.add(message);
            } else {
                warnings.add(message);
            }
        }
    }

    private void validateYear(Student student, CourseRequirement req,
                             List<String> errors, List<String> warnings) {
        if (req.getRequiredYear() != null && student.getYearOfStudy() < req.getRequiredYear()) {
            String message = "Year requirement not met. Required: Year " + req.getRequiredYear() 
                           + ", Current: Year " + student.getYearOfStudy();
            if (req.getIsMandatory()) {
                errors.add(message);
            } else {
                warnings.add(message);
            }
        }
    }

    private void validateCredit(Student student, CourseRequirement req,
                               List<String> errors, List<String> warnings) {
        if (req.getMinCreditsCompleted() != null && student.getCreditsCompleted() < req.getMinCreditsCompleted()) {
            String message = "Credit requirement not met. Required: " + req.getMinCreditsCompleted() 
                           + " credits, Completed: " + student.getCreditsCompleted();
            if (req.getIsMandatory()) {
                errors.add(message);
            } else {
                warnings.add(message);
            }
        }
    }

    private void validateGPA(Student student, CourseRequirement req,
                            List<String> errors, List<String> warnings) {
        if (req.getMinGPA() != null && (student.getCurrentGPA() == null || student.getCurrentGPA() < req.getMinGPA())) {
            String message = "GPA requirement not met. Required: " + req.getMinGPA() 
                           + ", Current: " + (student.getCurrentGPA() != null ? student.getCurrentGPA() : "N/A");
            if (req.getIsMandatory()) {
                errors.add(message);
            } else {
                warnings.add(message);
            }
        }
    }

    private void validateProgram(Student student, CourseRequirement req,
                                List<String> errors, List<String> warnings) {
        if (req.getRequiredProgram() != null && !student.getProgram().equalsIgnoreCase(req.getRequiredProgram())) {
            String message = "Program requirement not met. Required: " + req.getRequiredProgram() 
                           + ", Current: " + student.getProgram();
            if (req.getIsMandatory()) {
                errors.add(message);
            } else {
                warnings.add(message);
            }
        }
    }

    private boolean meetsMinimumGrade(String actualGrade, String minGrade) {
        // Implement grade comparison logic (A > B+ > B > C+ > C > D > F)
        String[] gradeOrder = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};
        int actualIndex = indexOf(gradeOrder, actualGrade);
        int minIndex = indexOf(gradeOrder, minGrade);
        return actualIndex <= minIndex; // Lower index = higher grade
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return array.length; // Worst case
    }

    private int calculateCurrentSemesterCredits(Student student, Long semesterId) {
        return enrollmentRepository.findByStudentId(student.getId()).stream()
            .filter(e -> e.getCourse().getSemester().getId().equals(semesterId))
            .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
    }
}
```

**AllocationService.java** - Priority-based allocation:
```java
package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.AllocationResultDTO;
import com.courseallocation.course_allocation.dto.ValidationResultDTO;
import com.courseallocation.course_allocation.model.*;
import com.courseallocation.course_allocation.model.enums.*;
import com.courseallocation.course_allocation.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AllocationService {

    private final EnrollmentRequestRepository requestRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final ValidationService validationService;
    private final AuditService auditService;

    public AllocationResultDTO runAllocation(Long semesterId) {
        log.info("Starting allocation process for semester: {}", semesterId);

        AllocationResultDTO result = new AllocationResultDTO();
        
        // 1. Fetch all pending requests for semester
        List<EnrollmentRequest> requests = requestRepository
            .findBySemesterAndStatusOrderByPriorityDesc(semesterId, RequestStatus.PENDING);

        log.info("Found {} pending enrollment requests", requests.size());

        // 2. Calculate priorities
        requests.forEach(this::calculatePriority);

        // 3. Sort by priority (descending)
        requests.sort(Comparator.comparing(EnrollmentRequest::getPriority).reversed());

        // 4. Process each request
        for (EnrollmentRequest request : requests) {
            processRequest(request, result);
        }

        // 5. Audit
        auditService.logAllocation(result, semesterId);

        log.info("Allocation complete. Approved: {}, Rejected: {}, Waitlisted: {}", 
                 result.getApproved(), result.getRejected(), result.getWaitlisted());

        return result;
    }

    private void processRequest(EnrollmentRequest request, AllocationResultDTO result) {
        try {
            ValidationResultDTO validation = validationService.validateEnrollment(
                request.getStudent(), request.getCourse());

            if (validation.isValid()) {
                allocate(request);
                result.incrementApproved();
                log.debug("Approved request {} for student {} in course {}", 
                         request.getId(), request.getStudent().getStudentId(), 
                         request.getCourse().getCourseCode());
            } else {
                reject(request, validation.getErrors());
                result.incrementRejected();
                log.debug("Rejected request {} due to: {}", request.getId(), validation.getErrors());
            }
        } catch (Exception e) {
            waitlist(request, e.getMessage());
            result.incrementWaitlisted();
            log.warn("Waitlisted request {} due to exception: {}", request.getId(), e.getMessage());
        }
    }

    private void calculatePriority(EnrollmentRequest request) {
        Student student = request.getStudent();
        Course course = request.getCourse();

        int priority = (student.getYearOfStudy() * 100) +
                      (student.getCreditsCompleted() * 10) +
                      (int)((student.getCurrentGPA() != null ? student.getCurrentGPA() : 0.0) * 5);

        // Major bonus
        if (course.getDepartment() != null && 
            student.getProgram().equalsIgnoreCase(course.getDepartment().getName())) {
            priority += 50;
        }

        request.setPriority(priority);
    }

    private void allocate(EnrollmentRequest request) {
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(request.getStudent());
        enrollment.setCourse(request.getCourse());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollmentRepository.save(enrollment);

        // Update request status
        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        // Increment course enrollment (with optimistic locking)
        Course course = request.getCourse();
        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        courseRepository.save(course);
    }

    private void reject(EnrollmentRequest request, List<String> errors) {
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(String.join("; ", errors));
        requestRepository.save(request);
    }

    private void waitlist(EnrollmentRequest request, String reason) {
        request.setStatus(RequestStatus.WAITLISTED);
        request.setRejectionReason(reason);
        requestRepository.save(request);
    }
}
```

**AuditService.java** - Audit logging:
```java
package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.AllocationResultDTO;
import com.courseallocation.course_allocation.model.AuditLog;
import com.courseallocation.course_allocation.model.User;
import com.courseallocation.course_allocation.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(User actor, String action, String entityType, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public void logAllocation(AllocationResultDTO result, Long semesterId) {
        String details = String.format(
            "{\"approved\": %d, \"rejected\": %d, \"waitlisted\": %d, \"semesterId\": %d}",
            result.getApproved(), result.getRejected(), result.getWaitlisted(), semesterId
        );
        
        AuditLog log = new AuditLog();
        log.setAction("ALLOCATION_RUN");
        log.setEntityType("SEMESTER");
        log.setEntityId(semesterId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
```

#### 4. Create New DTOs

Create in `dto/` package:

- **ValidationResultDTO.java**
- **AllocationResultDTO.java**
- **CourseRequirementDTO.java**
- **EnrollmentRequestDTO.java**

#### 5. Create New Controllers

- **HODController.java** - Course creation, lecturer assignment
- **LecturerController.java** - Requirement management
- **AdminController.java** - Allocation triggering, reports

#### 6. Update Security Configuration

Update `SecurityConfig.java` to handle new roles and endpoints:
- Add UserRole enum handling
- Configure endpoint permissions by role
- Update JWT token to include role information

#### 7. Update Database Schema

Update `src/main/resources/database/create-database.sql` with all new tables.

#### 8. Update DataInitializer

Add sample data for:
- Users with different roles
- Departments with HODs
- Courses assigned to lecturers
- Course requirements
- Student enrollment requests

## Testing Strategy

1. **Unit Tests**: Test each service method independently
2. **Integration Tests**: Test allocation workflow end-to-end
3. **API Tests**: Test all endpoints with different roles
4. **Performance Tests**: Test allocation with many students/courses

## Rollback Plan

The old README is saved as `README_OLD.md`. To rollback:
```bash
mv README.md README_NEW.md
mv README_OLD.md README.md
```

## Next Steps

1. Complete repository implementations
2. Implement all services (ValidationService, AllocationService, AuditService)
3. Create DTOs for all data transfers
4. Implement controllers (HOD, Lecturer, Admin)
5. Update SecurityConfig for new roles
6. Update database schema
7. Update DataInitializer
8. Test complete workflow
9. Deploy and monitor

## Notes

- All entity relationships are properly configured with Lazy loading to prevent N+1 queries
- Optimistic locking (@Version) used on Course to prevent race conditions during enrollment
- Transaction management ensures data consistency during allocation
- Audit logs provide complete traceability

---

**Status**: Entities and Enums Created, Documentation Updated  
**Next**: Repository Layer Implementation  
**Priority**: Core services (Validation, Allocation, Audit)
