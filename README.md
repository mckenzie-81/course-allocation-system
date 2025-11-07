# Course Allocation System

A comprehensive course allocation and enrollment management system for universities, built with Spring Boot, PostgreSQL, and JWT authentication. This system manages the complete lifecycle of course allocation from HOD assignment through student enrollment via a structured four-phase workflow.

## Table of Contents

- [System Overview](#system-overview)
- [Key Features](#key-features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [System Workflow](#system-workflow)
- [API Documentation](#api-documentation)
- [User Roles & Permissions](#user-roles--permissions)
- [API Endpoints](#api-endpoints)
- [Business Rules](#business-rules)
- [Troubleshooting](#troubleshooting)

## System Overview

### Purpose

The Course Allocation System automates and streamlines the course registration process with a role-based workflow:

- **Head of Department (HOD)**: Creates courses and assigns them to lecturers
- **Lecturers**: Define course requirements, prerequisites, and constraints
- **Students**: Browse courses and submit enrollment requests during pre-registration
- **System**: Validates requirements and allocates courses based on priority during post-registration
- **Administrators**: Monitor the allocation process, handle exceptions, and generate reports

### System Phases

1. **Setup Phase**: HOD creates courses and assigns to lecturers
2. **Configuration Phase**: Lecturers set prerequisites and requirements
3. **Pre-Registration Phase**: Students submit enrollment requests
4. **Post-Registration Phase**: System validates and allocates courses by priority

## Key Features

- **Multi-Role Support**: HOD, Lecturer, Student, Admin, and Registrar roles
- **Department Management**: Organize courses and users by departments
- **Course Requirements**: Define prerequisites, co-requisites, credit requirements, GPA minimums
- **Priority-Based Allocation**: Fair course allocation using calculated priority scores
- **Validation Engine**: Automated prerequisite, conflict, and constraint checking
- **Enrollment Requests**: Students request courses before final allocation
- **Waitlist Management**: Automatic waitlist for full courses
- **Audit Trail**: Complete logging of all system actions
- **Real-time Capacity Tracking**: Track course enrollment vs. capacity
- **JWT Authentication**: Secure token-based authentication

## Prerequisites

### Required Software

- **Java 17** or higher
- **PostgreSQL 14** or higher
- **Maven 3.6+** (optional - Maven Wrapper included)

### Installation Guides

#### Java 17

**macOS (Homebrew)**
```bash
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Ubuntu/Debian**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Windows**
1. Download from [Adoptium](https://adoptium.net/)
2. Run installer
3. Set JAVA_HOME environment variable

#### PostgreSQL 14

**macOS (Homebrew)**
```bash
brew install postgresql@14
brew services start postgresql@14
```

**Ubuntu/Debian**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Windows**
1. Download from [postgresql.org](https://www.postgresql.org/download/windows/)
2. Run installer
3. Remember the postgres user password

## Installation

### 1. Clone/Navigate to Project

```bash
cd /path/to/course-allocation-server
```

### 2. Verify Project Structure

```
course-allocation-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/courseallocation/course_allocation/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       │   └── enums/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── database/
│   └── test/
├── pom.xml
└── README.md
```

## Database Setup

### 1. Start PostgreSQL

**macOS**
```bash
brew services start postgresql@14
```

**Linux**
```bash
sudo systemctl start postgresql
```

**Windows**
- PostgreSQL service starts automatically
- Check Services (services.msc) if needed

### 2. Create Database

Connect to PostgreSQL:
```bash
# macOS/Linux
psql -U postgres

# Linux (if permission denied)
sudo -u postgres psql
```

Create the database:
```sql
CREATE DATABASE course_allocation;
\q
```

### 3. Verify Connection

```bash
psql -U postgres -d course_allocation
```

## Configuration

### Update Database Credentials

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/course_allocation
spring.datasource.username=postgres
spring.datasource.password=your_password_here

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080

# JWT Configuration
jwt.secret=yourSecretKeyMinimum256BitsForHS256Algorithm
jwt.expiration=86400000

# Logging
logging.level.com.courseallocation=DEBUG
```

**Important**: Update `spring.datasource.password` and `jwt.secret` before running.

## Running the Application

### Option 1: Maven Wrapper (Recommended)

```bash
# macOS/Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Option 2: Maven

```bash
mvn spring-boot:run
```

### Option 3: IDE

1. Open project in IntelliJ IDEA or Eclipse
2. Wait for Maven dependencies to download
3. Run `CourseAllocationApplication.java`

### Verify Application Started

Look for:
```
Started CourseAllocationApplication in X.XXX seconds
```

Application URL: `http://localhost:8080`

## System Workflow

### Phase 1: Course Setup (HOD)

```
HOD logs in → Creates course → Assigns to lecturer → Sets capacity
```

**Example:**
- Create "CS 301: Database Systems"
- Assign to Dr. Mensah
- Set capacity: 40 students

### Phase 2: Requirements Configuration (Lecturer)

```
Lecturer logs in → Views assigned courses → Adds requirements
```

**Requirements Types:**
- **Prerequisites**: Must complete Course A before Course B
- **Co-requisites**: Must take courses simultaneously  
- **Year**: Must be in specific year (1-4)
- **Credit**: Minimum credits completed
- **Program**: Must be in specific program/major
- **GPA**: Minimum GPA requirement

**Example:**
- Course: CS 301 (Database Systems)
- Prerequisite: CS 201 (Data Structures) with min grade B
- Year requirement: Year 2+
- Program requirement: Computer Science major

### Phase 3: Pre-Registration (Students)

```
Student logs in → Browses courses → Checks eligibility → Submits request
```

**Student Actions:**
- Browse available courses
- View course details and requirements
- Validate prerequisites
- Submit enrollment requests
- View request status

### Phase 4: Post-Registration (System Allocation)

```
Admin triggers allocation → System validates → Calculates priority →  
Allocates by priority → Updates enrollments → Notifies students
```

**Allocation Process:**
1. Fetch all pending enrollment requests
2. Calculate priority for each request
3. Sort requests by priority (highest first)
4. Process each request:
   - Validate prerequisites
   - Check seat availability
   - Check time conflicts
   - Allocate or waitlist
5. Generate allocation report
6. Send notifications

## API Documentation

### Swagger UI

Once running, access interactive API documentation:

```
http://localhost:8080/docs
```

### OpenAPI JSON Specification

```
http://localhost:8080/v3/api-docs
```

## User Roles & Permissions

### STUDENT
- Browse courses
- View course requirements
- Submit enrollment requests
- Cancel pending requests
- View enrolled courses
- Drop courses (during add/drop period)

### LECTURER
- View assigned courses
- Add/remove course requirements
- Set course status (ACTIVE/CLOSED)
- View course enrollments
- Download class roster

### HOD (Head of Department)
- Create courses
- Assign courses to lecturers
- Update course capacity
- View department enrollment statistics
- Manage department courses

### ADMIN/REGISTRAR
- Trigger allocation process
- Override enrollment decisions
- View allocation reports
- Access audit logs
- Handle student petitions
- Manage system-wide settings

## API Endpoints

### Authentication

```
POST   /api/auth/login                        Login (all roles)
```

**Request:**
```json
{
  "username": "student001",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "username": "student001",
    "role": "STUDENT",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### HOD Endpoints

```
POST   /api/hod/courses                       Create course
PUT    /api/hod/courses/{id}/assign           Assign to lecturer
GET    /api/hod/courses                       List department courses
PUT    /api/hod/courses/{id}/capacity         Update capacity
GET    /api/hod/reports/enrollment            Department statistics
```

**Create Course Example:**
```json
{
  "courseCode": "CS301",
  "title": "Database Systems",
  "credits": 3,
  "level": 300,
  "maxCapacity": 40,
  "semesterId": 1,
  "academicYear": "2024/2025",
  "description": "Introduction to database design and SQL"
}
```

### Lecturer Endpoints

```
GET    /api/lecturer/courses                  List assigned courses
POST   /api/lecturer/courses/{id}/requirements Add requirement
DELETE /api/lecturer/courses/{id}/requirements/{reqId} Remove requirement
GET    /api/lecturer/courses/{id}/enrollments View enrollments
GET    /api/lecturer/courses/{id}/roster      Download roster
PUT    /api/lecturer/courses/{id}/status      Update status
```

**Add Requirement Example:**
```json
{
  "prerequisiteCourseId": 5,
  "minGrade": "B",
  "requirementType": "PREREQUISITE",
  "isMandatory": true,
  "description": "Must pass CS 201 with B or higher"
}
```

### Student Endpoints

```
GET    /api/student/courses                   Browse available courses
GET    /api/student/courses/{id}              View course details
POST   /api/student/enrollment-requests       Submit enrollment request
GET    /api/student/enrollment-requests       View my requests
DELETE /api/student/enrollment-requests/{id}  Cancel request
GET    /api/student/enrollments               View enrolled courses
POST   /api/student/enrollments/{id}/drop     Drop course
GET    /api/student/validate/{courseId}       Check eligibility
```

**Submit Request Example:**
```json
{
  "courseId": 10
}
```

### Admin Endpoints

```
POST   /api/admin/allocation/run              Trigger allocation
GET    /api/admin/allocation/status           Check allocation status
POST   /api/admin/requests/{id}/override      Manual approve/reject
GET    /api/admin/reports/summary             Allocation summary
GET    /api/admin/audit-logs                  View audit trail
```

**Trigger Allocation Example:**
```json
{
  "semesterId": 1,
  "academicYear": "2024/2025"
}
```

### Semester Management

```
POST   /api/semesters                         Create semester
GET    /api/semesters                         List all semesters
GET    /api/semesters/{id}                    Get semester details
GET    /api/semesters/active                  Get active semester
PUT    /api/semesters/{id}                    Update semester
DELETE /api/semesters/{id}                    Delete semester
```

## Business Rules

### Priority Calculation

Student priority is calculated using the formula:

```
Priority = (Year × 100) + (CreditsCompleted × 10) + (GPA × 5) + MajorBonus

Where:
- Year: 4 (Senior) to 1 (Freshman)
- CreditsCompleted: Total credits earned
- GPA: 0.0 to 4.0
- MajorBonus: +50 if course is in student's major
```

**Example:**
- Senior (Year 4): 400 points
- 90 credits completed: 900 points
- GPA 3.5: 17.5 points
- Major match: 50 points
- **Total Priority: 1367.5**

### Credit Limits

- **Minimum**: 12 credits per semester (full-time status)
- **Maximum**: 18 credits per semester
- **With Approval**: Up to 21 credits (requires HOD approval)
- **Maximum Courses**: 7 courses per semester

### Validation Rules

**Enrollment Request Validation:**
1. Student must meet all mandatory prerequisites
2. Student must meet year level requirements
3. Student must meet program requirements
4. Student must meet minimum GPA (if specified)
5. Course must be in ACTIVE status
6. No duplicate requests for same course
7. No time schedule conflicts
8. Total credits must not exceed maximum

**Allocation Process Validation:**
1. Re-validate all prerequisites (grades may have updated)
2. Check seat availability
3. Verify no schedule conflicts with already-allocated courses
4. Confirm student not on academic hold
5. Verify enrollment limits not exceeded

### Course Status Transitions

```
DRAFT → ACTIVE → CLOSED
  ↓       ↓
CANCELLED CANCELLED
```

- **DRAFT**: Created by HOD, awaiting lecturer configuration
- **ACTIVE**: Open for student enrollment requests
- **CLOSED**: Enrollment period ended
- **CANCELLED**: Course cancelled (no allocations made)

### Waitlist Processing

When a seat becomes available:
1. System notifies top 3 waitlist students
2. Students have 24 hours to accept
3. Highest priority student gets seat if accepted
4. Process repeats if declined/timeout

## Troubleshooting

### Database Connection Issues

**Error**: `Connection refused`

**Solution**:
```bash
# Check PostgreSQL is running
# macOS
brew services list

# Linux
sudo systemctl status postgresql

# Restart if needed
brew services restart postgresql@14          # macOS
sudo systemctl restart postgresql           # Linux
```

### Port 8080 Already in Use

**Solution**:
```bash
# Find process using port 8080
lsof -i :8080                               # macOS/Linux
netstat -ano | findstr :8080                # Windows

# Kill process or change port in application.properties
server.port=8081
```

### JWT Token Issues

**Error**: `Invalid or expired token`

**Solution**:
- Token expires after 24 hours - login again
- Ensure full token is copied (no spaces)
- In Swagger, paste token WITHOUT "Bearer" prefix

### Hibernate DDL Errors

**Error**: `Table already exists` or schema conflicts

**Solution**:
```properties
# Temporarily use this to recreate schema
spring.jpa.hibernate.ddl-auto=create-drop

# Then switch back to
spring.jpa.hibernate.ddl-auto=update
```

### Allocation Process Fails

**Check**:
1. Ensure semester is ACTIVE
2. Verify students have enrollment requests with PENDING status
3. Check application logs for validation errors
4. Verify courses have ACTIVE status
5. Confirm course capacities are set correctly

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.5.7** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence layer
- **PostgreSQL 14** - Relational database
- **JWT (jjwt 0.12.5)** - Token-based authentication
- **Swagger/OpenAPI** - API documentation
- **Lombok** - Boilerplate code reduction
- **Maven** - Build automation

## Project Structure

```
src/main/java/com/courseallocation/course_allocation/
├── CourseAllocationApplication.java       # Main application class
├── config/
│   ├── SecurityConfig.java                # Security & JWT configuration
│   ├── JwtTokenProvider.java              # JWT token utilities
│   ├── TokenAuthenticationFilter.java     # JWT filter
│   ├── SwaggerConfig.java                 # API documentation config
│   └── DataInitializer.java               # Sample data seeder
├── controller/
│   ├── AuthController.java                # Authentication endpoints
│   ├── HODController.java                 # HOD endpoints
│   ├── LecturerController.java            # Lecturer endpoints
│   ├── StudentController.java             # Student endpoints
│   ├── AdminController.java               # Admin endpoints
│   └── SemesterController.java            # Semester management
├── dto/
│   ├── ApiResponse.java                   # Standard API response
│   ├── LoginRequest.java / LoginResponse.java
│   ├── CourseRequest.java / CourseResponse.java
│   ├── EnrollmentRequestDTO.java
│   ├── AllocationResultDTO.java
│   ├── ValidationResultDTO.java
│   └── CourseRequirementDTO.java
├── exception/
│   └── GlobalExceptionHandler.java        # Centralized error handling
├── model/
│   ├── entity/
│   │   ├── User.java                      # System users
│   │   ├── Department.java                # Academic departments
│   │   ├── Student.java                   # Student records
│   │   ├── Course.java                    # Course catalog
│   │   ├── CourseRequirement.java         # Course prerequisites
│   │   ├── EnrollmentRequest.java         # Student requests
│   │   ├── Enrollment.java                # Final enrollments
│   │   ├── Semester.java                  # Academic semesters
│   │   └── AuditLog.java                  # Audit trail
│   └── enums/
│       ├── UserRole.java
│       ├── CourseStatus.java
│       ├── RequestStatus.java
│       ├── EnrollmentStatus.java
│       └── RequirementType.java
├── repository/
│   ├── UserRepository.java
│   ├── DepartmentRepository.java
│   ├── StudentRepository.java
│   ├── CourseRepository.java
│   ├── CourseRequirementRepository.java
│   ├── EnrollmentRequestRepository.java
│   ├── EnrollmentRepository.java
│   ├── SemesterRepository.java
│   └── AuditLogRepository.java
└── service/
    ├── AuthenticationService.java         # User authentication
    ├── CourseService.java                 # Course management
    ├── AllocationService.java             # Core allocation logic
    ├── ValidationService.java             # Requirement validation
    ├── EnrollmentService.java             # Enrollment management
    ├── SemesterService.java               # Semester operations
    ├── AuditService.java                  # Audit logging
    └── NotificationService.java           # Email notifications
```

## Security Features

- **Role-Based Access Control (RBAC)**: Different permissions per role
- **JWT Authentication**: Stateless token-based auth
- **Token Expiration**: 24-hour token validity
- **Password Encryption**: BCrypt password hashing
- **Protected Endpoints**: All endpoints except /auth/login require authentication
- **Audit Trail**: Complete logging of all system actions

## Sample Data

The system automatically seeds sample data on first startup (via `DataInitializer.java`):

- Departments: Computer Science, Mathematics, Engineering
- Users: HODs, Lecturers, Students with various roles
- Courses: Sample courses for each department
- Semester: Active semester for current academic year

## Support & Contribution

For issues, questions, or contributions:
1. Check application logs in console
2. Review Swagger documentation at `/docs`
3. Verify database connection and data
4. Check JWT token validity
5. Review audit logs for system actions

## License

This project is for educational purposes.

---

**Version**: 2.0  
**Last Updated**: November 2025  
**Documentation**: Complete system implementation guide
