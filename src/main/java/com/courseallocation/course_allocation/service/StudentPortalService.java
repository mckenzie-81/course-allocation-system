package com.courseallocation.course_allocation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseallocation.course_allocation.dto.CourseEligibilityResponse;
import com.courseallocation.course_allocation.dto.CourseGradeRecord;
import com.courseallocation.course_allocation.dto.CourseResponse;
import com.courseallocation.course_allocation.dto.ScheduleResponse;
import com.courseallocation.course_allocation.dto.ScheduledCourse;
import com.courseallocation.course_allocation.dto.TranscriptResponse;
import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.CourseRequirement;
import com.courseallocation.course_allocation.model.Enrollment;
import com.courseallocation.course_allocation.model.Semester;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.model.enums.EnrollmentStatus;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.CourseRequirementRepository;
import com.courseallocation.course_allocation.repository.EnrollmentRepository;
import com.courseallocation.course_allocation.repository.SemesterRepository;
import com.courseallocation.course_allocation.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentPortalService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SemesterRepository semesterRepository;
    private final CourseRequirementRepository courseRequirementRepository;
    private final CourseService courseService;

    public List<CourseResponse> getAvailableCourses(Long studentId, Long semesterId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Course> courses;
        if (semesterId != null) {
            courses = courseRepository.findBySemesterId(semesterId);
        } else {
            // Get active semester courses
            Semester activeSemester = semesterRepository.findAll().stream()
                    .filter(s -> s.getIsActive())
                    .findFirst()
                    .orElse(null);
            
            if (activeSemester != null) {
                courses = courseRepository.findBySemesterId(activeSemester.getId());
            } else {
                courses = courseRepository.findAll();
            }
        }

        return courses.stream()
                .map(course -> courseService.getCourseById(course.getId()))
                .collect(Collectors.toList());
    }

    public CourseEligibilityResponse checkCourseEligibility(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<String> unmetRequirements = new ArrayList<>();
        boolean isEligible = true;

        // Check if already enrolled
        boolean alreadyEnrolled = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent();
        if (alreadyEnrolled) {
            unmetRequirements.add("Already enrolled in this course");
            isEligible = false;
        }

        // Check available seats
        long currentEnrollment = enrollmentRepository.countByCourseId(courseId);
        boolean hasAvailableSeats = currentEnrollment < course.getMaxCapacity();
        if (!hasAvailableSeats) {
            unmetRequirements.add("Course is full (capacity: " + course.getMaxCapacity() + ")");
            isEligible = false;
        }

        // Check prerequisites
        List<CourseRequirement> requirements = courseRequirementRepository.findByCourseId(courseId);
        boolean meetsPrerequisites = checkPrerequisites(student, requirements, unmetRequirements);
        
        // Check GPA requirement
        boolean meetsGPA = true;
        for (CourseRequirement req : requirements) {
            if (req.getMinGPA() != null && student.getCurrentGPA() < req.getMinGPA()) {
                unmetRequirements.add("Minimum GPA required: " + req.getMinGPA() + " (Current: " + student.getCurrentGPA() + ")");
                meetsGPA = false;
                isEligible = false;
            }
        }

        // Check year requirement
        boolean meetsYear = true;
        for (CourseRequirement req : requirements) {
            if (req.getRequiredYear() != null && student.getYearOfStudy() < req.getRequiredYear()) {
                unmetRequirements.add("Minimum year required: " + req.getRequiredYear() + " (Current: " + student.getYearOfStudy() + ")");
                meetsYear = false;
                isEligible = false;
            }
        }

        isEligible = isEligible && meetsPrerequisites;

        String message = isEligible ? "You are eligible to enroll in this course" : 
                "You do not meet the requirements for this course";

        return new CourseEligibilityResponse(
                isEligible,
                message,
                !requirements.isEmpty(),
                meetsGPA,
                meetsYear,
                hasAvailableSeats,
                alreadyEnrolled,
                unmetRequirements
        );
    }

    private boolean checkPrerequisites(Student student, List<CourseRequirement> requirements, List<String> unmetRequirements) {
        boolean meetsAll = true;

        for (CourseRequirement req : requirements) {
            if (req.getPrerequisiteCourse() != null) {
                // Check if student has completed the prerequisite
                boolean hasCompleted = enrollmentRepository
                        .findByStudentIdAndCourseId(student.getId(), req.getPrerequisiteCourse().getId())
                        .map(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                        .orElse(false);

                if (!hasCompleted) {
                    unmetRequirements.add("Prerequisite required: " + req.getPrerequisiteCourse().getCourseCode() + 
                            " - " + req.getPrerequisiteCourse().getTitle());
                    meetsAll = false;
                }
            }
        }

        return meetsAll;
    }

    @Transactional(readOnly = true)
    public TranscriptResponse getTranscript(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        List<CourseGradeRecord> courseHistory = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .map(enrollment -> new CourseGradeRecord(
                        enrollment.getCourse().getCourseCode(),
                        enrollment.getCourse().getTitle(),
                        enrollment.getCourse().getCredits(),
                        enrollment.getCourse().getSemester() != null ? 
                                enrollment.getCourse().getSemester().getSemesterCode() : "N/A",
                        enrollment.getFinalGrade(),
                        enrollment.getStatus().name()
                ))
                .collect(Collectors.toList());

        String studentName = student.getUser() != null ? 
                student.getUser().getFirstName() + " " + student.getUser().getLastName() : "N/A";

        return new TranscriptResponse(
                student.getStudentId(),
                studentName,
                student.getProgram(),
                student.getYearOfStudy(),
                student.getCreditsCompleted(),
                student.getCurrentGPA(),
                courseHistory,
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getCurrentSchedule(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get active semester
        Semester activeSemester = semesterRepository.findAll().stream()
                .filter(s -> s.getIsActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active semester found"));

        List<Enrollment> currentEnrollments = enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> e.getCourse().getSemester() != null && 
                        e.getCourse().getSemester().getId().equals(activeSemester.getId()))
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .collect(Collectors.toList());

        List<ScheduledCourse> courses = currentEnrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    String lecturerName = course.getAssignedLecturer() != null ?
                            course.getAssignedLecturer().getFirstName() + " " + 
                            course.getAssignedLecturer().getLastName() : "TBA";
                    
                    return new ScheduledCourse(
                            course.getCourseCode(),
                            course.getTitle(),
                            course.getCredits(),
                            lecturerName,
                            enrollment.getStatus().name()
                    );
                })
                .collect(Collectors.toList());

        int totalCredits = courses.stream()
                .mapToInt(ScheduledCourse::getCredits)
                .sum();

        return new ScheduleResponse(
                student.getStudentId(),
                activeSemester.getSemesterCode(),
                courses,
                totalCredits,
                LocalDateTime.now()
        );
    }
}
