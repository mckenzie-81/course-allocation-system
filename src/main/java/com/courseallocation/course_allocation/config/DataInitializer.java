package com.courseallocation.course_allocation.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.courseallocation.course_allocation.model.Course;
import com.courseallocation.course_allocation.model.Semester;
import com.courseallocation.course_allocation.model.Student;
import com.courseallocation.course_allocation.repository.CourseRepository;
import com.courseallocation.course_allocation.repository.SemesterRepository;
import com.courseallocation.course_allocation.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

// Temporarily disabled - needs update for new entity structure
// @Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Temporarily disabled - update entities first
        // if (semesterRepository.count() == 0) {
        //     seedData();
        // }
    }

    private void seedData() {
        // TODO: Update this method to use new entity structure
        /*
        Semester fall2024 = new Semester();
        fall2024.setSemesterCode("FALL2024");
        fall2024.setName("Fall 2024");
        fall2024.setStartDate(LocalDate.of(2024, 9, 1));
        fall2024.setEndDate(LocalDate.of(2024, 12, 15));
        fall2024.setIsActive(true);
        fall2024 = semesterRepository.save(fall2024);

        Student student1 = new Student();
        student1.setStudentId("STU001");
        student1.setFirstName("Kwame");
        student1.setLastName("Asante");
        student1.setEmail("kwame.asante@university.edu");
        student1.setDepartment("Computer Science");
        student1.setYear(2);
        student1.setPin("1234");
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setStudentId("STU002");
        student2.setFirstName("Ama");
        student2.setLastName("Mensah");
        student2.setEmail("ama.mensah@university.edu");
        student2.setDepartment("Computer Science");
        student2.setYear(3);
        student2.setPin("5678");
        studentRepository.save(student2);

        Student student3 = new Student();
        student3.setStudentId("STU003");
        student3.setFirstName("Kofi");
        student3.setLastName("Osei");
        student3.setEmail("kofi.osei@university.edu");
        student3.setDepartment("Mathematics");
        student3.setYear(1);
        student3.setPin("9012");
        studentRepository.save(student3);

        Student student4 = new Student();
        student4.setStudentId("STU004");
        student4.setFirstName("Akosua");
        student4.setLastName("Boateng");
        student4.setEmail("akosua.boateng@university.edu");
        student4.setDepartment("Computer Science");
        student4.setYear(2);
        student4.setPin("3456");
        studentRepository.save(student4);

        Course course1 = new Course();
        course1.setCourseCode("CS201");
        course1.setCourseName("Data Structures and Algorithms");
        course1.setDepartment("Computer Science");
        course1.setLevel(2);
        course1.setCredits(3);
        course1.setMaxCapacity(30);
        course1.setDescription("Introduction to data structures and algorithm analysis");
        course1.setInstructor("Dr. Sarah Johnson");
        course1.setSemester(fall2024);
        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setCourseCode("CS301");
        course2.setCourseName("Database Systems");
        course2.setDepartment("Computer Science");
        course2.setLevel(3);
        course2.setCredits(3);
        course2.setMaxCapacity(25);
        course2.setDescription("Fundamentals of database design and SQL");
        course2.setInstructor("Dr. Michael Brown");
        course2.setSemester(fall2024);
        courseRepository.save(course2);

        Course course3 = new Course();
        course3.setCourseCode("CS202");
        course3.setCourseName("Object-Oriented Programming");
        course3.setDepartment("Computer Science");
        course3.setLevel(2);
        course3.setCredits(4);
        course3.setMaxCapacity(35);
        course3.setDescription("Advanced OOP concepts and design patterns");
        course3.setInstructor("Dr. Emily Davis");
        course3.setSemester(fall2024);
        courseRepository.save(course3);

        Course course4 = new Course();
        course4.setCourseCode("MATH101");
        course4.setCourseName("Calculus I");
        course4.setDepartment("Mathematics");
        course4.setLevel(1);
        course4.setCredits(4);
        course4.setMaxCapacity(40);
        course4.setDescription("Introduction to differential and integral calculus");
        course4.setInstructor("Dr. Robert Wilson");
        course4.setSemester(fall2024);
        courseRepository.save(course4);

        Course course5 = new Course();
        course5.setCourseCode("CS401");
        course5.setCourseName("Software Engineering");
        course5.setDepartment("Computer Science");
        course5.setLevel(4);
        course5.setCredits(3);
        course5.setMaxCapacity(20);
        course5.setDescription("Software development lifecycle and methodologies");
        course5.setInstructor("Dr. David Martinez");
        course5.setSemester(fall2024);
        courseRepository.save(course5);

        Course course6 = new Course();
        course6.setCourseCode("CS302");
        course6.setCourseName("Operating Systems");
        course6.setDepartment("Computer Science");
        course6.setLevel(3);
        course6.setCredits(3);
        course6.setMaxCapacity(28);
        course6.setDescription("OS concepts, processes, memory management");
        course6.setInstructor("Dr. Lisa Anderson");
        course6.setSemester(fall2024);
        courseRepository.save(course6);
        */
    }
}

