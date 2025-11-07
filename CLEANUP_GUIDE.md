# Removed/Cleaned Up Components

## Overview

This document lists all components that should be removed or significantly refactored as they are no longer relevant to the Course Allocation System architecture.

## Controllers to Remove/Refactor

### ❌ CourseSelectionController.java
**Reason**: This controller implemented a direct course selection model. The new system uses an enrollment request → allocation workflow.

**Old Endpoints** (to be removed):
- `GET /api/student/courses/available` - Replaced by StudentController browsing
- `POST /api/student/courses/select` - Replaced by enrollment request submission
- `DELETE /api/student/courses/drop/{enrollmentId}` - Replaced by proper drop workflow
- `GET /api/student/courses/summary` - Replaced by enrollment status endpoint

**Action**: 
```bash
# Remove this file entirely
rm src/main/java/com/courseallocation/course_allocation/controller/CourseSelectionController.java
```

### 🔄 EnrollmentController.java
**Current endpoints** (generic CRUD):
- POST /api/enrollments
- GET /api/enrollments
- GET /api/enrollments/{id}
- PUT /api/enrollments/{id}
- DELETE /api/enrollments/{id}

**Issues**:
- Direct enrollment creation bypasses validation
- No role-based access control
- No audit trail
- Doesn't fit allocation workflow

**Action**: Either remove entirely OR refactor to admin-only manual enrollment override

### 🔄 CourseController.java
**Current endpoints** (generic CRUD):
- POST /api/courses
- GET /api/courses
- GET /api/courses/{id}
- PUT /api/courses/{id}
- DELETE /api/courses/{id}

**Issues**:
- No role-based restrictions (should be HOD/Admin only)
- Missing lecturer assignment workflow
- No status management

**Action**: Refactor endpoints to split between:
- HODController - course creation/assignment
- LecturerController - course configuration
- StudentController - course browsing (read-only)

### 🔄 StudentController.java
**Keep but refactor** to align with new workflow:

**Remove**:
- Any direct enrollment endpoints

**Add**:
- Browse courses endpoint
- Submit enrollment request
- View my requests
- Cancel pending request
- View my enrollments
- Validate prerequisites for a course

## Services to Remove/Refactor

### ❌ CourseSelectionService.java
**Reason**: Direct selection logic replaced by request → validation → allocation workflow

**Current methods** (likely):
- `getAvailableCourses()`
- `selectCourse()`
- `dropCourse()`
- `getStudentSummary()`

**Action**: 
```bash
# Remove this file
rm src/main/java/com/courseallocation/course_allocation/service/CourseSelectionService.java
```

Functionality replaced by:
- ValidationService - prerequisite checking
- AllocationService - course allocation
- EnrollmentService - request management

### 🔄 EnrollmentService.java
**Refactor** to handle:
- Enrollment request submission
- Request cancellation
- Viewing requests and enrollments
- Drop course (during add/drop period)

**Remove**:
- Direct enrollment creation (should only happen via allocation)

### 🔄 CourseService.java
**Refactor** to split by role:
- HOD methods: create, assign lecturer, set capacity
- Lecturer methods: add requirements, update status
- Student methods: browse (read-only)
- Admin methods: full CRUD

### 🔄 StudentService.java
**Update** for new Student entity structure:
- Handle User relationship
- Update profile with GPA, credits
- Link to enrollment requests

### 🔄 AuthenticationService.java
**Update** to handle:
- User entity instead of/in addition to Student
- Multiple roles (STUDENT, LECTURER, HOD, ADMIN)
- Role-based login responses

## DTOs to Update

### 🔄 Current DTOs to review:

1. **LoginRequest.java**
   - Change from studentId/pin to username/password
   - Support all user roles

2. **LoginResponse.java**
   - Include user role
   - Include department (if applicable)
   - Different response based on role

3. **CourseRequest/Response.java**
   - Add status field
   - Add lecturer assignment
   - Add department
   - Add requirements list

4. **EnrollmentRequest/Response.java**
   - Distinguish between EnrollmentRequest (student request) and Enrollment (final)
   - Add status, priority fields

5. **StudentRequest/Response.java**
   - Add User relationship
   - Add program, year, credits, GPA

### ❌ DTOs to potentially remove:

1. **StudentCourseSummary.java**
   - If this was specific to old selection system
   - Replace with proper enrollment status DTO

## Database Changes

### Tables to Add:
- users
- departments
- course_requirements
- enrollment_requests
- audit_logs

### Tables to Update:
- students (add user_id, program, year_of_study, credits_completed, current_gpa)
- courses (add status, assigned_lecturer_id, department_id, academic_year, level, version)
- enrollments (add enrollment_date, final_grade, change status to enum)

### Columns to Remove:
- students.firstName → move to users.first_name
- students.lastName → move to users.last_name
- students.email → move to users.email
- courses.instructor (String) → replaced by assigned_lecturer_id FK
- courses.courseName → rename to title
- courses.department (String) → replaced by department_id FK

## Configuration Files

### 🔄 application.properties
**Review and update**:
- Ensure database URL correct
- Update JWT settings
- Add logging levels for new packages

### 🔄 SecurityConfig.java
**Major refactor needed**:
- Remove student-specific authentication
- Add role-based endpoint security
- Configure:
  - HOD endpoints → HOD role only
  - Lecturer endpoints → LECTURER role only
  - Admin endpoints → ADMIN/REGISTRAR roles
  - Student endpoints → STUDENT role only
  - Public endpoints → /api/auth/login only

### 🔄 SwaggerConfig.java
**Update** to document:
- New role-based endpoints
- New DTOs
- New error responses

## Test Files

### ❌ Tests to remove:
- Any tests for CourseSelectionController
- Any tests for direct enrollment creation

### 🔄 Tests to update:
- Authentication tests (multi-role)
- Course tests (role-based access)
- Enrollment tests (request workflow)

### ✅ Tests to add:
- ValidationService tests
- AllocationService tests (priority calculation, allocation logic)
- Role-based access control tests
- Audit logging tests

## Data Initialization

### 🔄 DataInitializer.java
**Complete rewrite needed**:

**Remove**:
- Simple student seeding
- Direct enrollment creation

**Add**:
1. Create departments
2. Create HOD users for each department
3. Create lecturer users
4. Create admin users
5. Create student users
6. Create Student records linked to student users
7. Create courses assigned to lecturers
8. Create course requirements
9. Create semester
10. Create sample enrollment requests

## Workflow Comparison

### ❌ OLD Workflow (to remove):
```
Student → Browse Courses → Click "Enroll" → Immediately Enrolled
```

### ✅ NEW Workflow:
```
Phase 1: HOD → Create Course → Assign to Lecturer
Phase 2: Lecturer → Add Requirements → Set Status ACTIVE
Phase 3: Student → Browse → Submit Request (PENDING)
Phase 4: Admin → Trigger Allocation → System Validates → Allocates/Rejects
```

## API Endpoint Changes Summary

### Remove These Endpoints:
```
DELETE /api/student/courses/select          (CourseSelectionController)
DELETE /api/student/courses/drop            (CourseSelectionController)
DELETE /api/student/courses/summary         (CourseSelectionController)
POST   /api/enrollments                     (EnrollmentController - direct create)
```

### Change These Endpoints:
```
GET /api/courses                            → Split by role
POST /api/courses                           → Move to HODController
GET /api/student/courses/available          → GET /api/student/courses
```

### Add These Endpoints:
```
# HOD
POST   /api/hod/courses
PUT    /api/hod/courses/{id}/assign

# Lecturer
POST   /api/lecturer/courses/{id}/requirements
GET    /api/lecturer/courses/{id}/enrollments

# Student
POST   /api/student/enrollment-requests
GET    /api/student/enrollment-requests
DELETE /api/student/enrollment-requests/{id}
GET    /api/student/validate/{courseId}

# Admin
POST   /api/admin/allocation/run
GET    /api/admin/audit-logs
```

## Step-by-Step Cleanup Process

### Step 1: Backup
```bash
git add .
git commit -m "Backup before refactoring to allocation system"
git branch backup-course-selection
```

### Step 2: Remove Obsolete Files
```bash
rm src/main/java/com/courseallocation/course_allocation/controller/CourseSelectionController.java
rm src/main/java/com/courseallocation/course_allocation/service/CourseSelectionService.java
```

### Step 3: Create New Files
Follow MIGRATION_GUIDE.md to create:
- New repositories
- New services
- New controllers
- New DTOs

### Step 4: Update Existing Files
Systematically update:
1. All existing entities (already done)
2. All existing repositories (add new methods)
3. All existing services (refactor for new workflow)
4. All existing controllers (role-based access)
5. SecurityConfig (role-based security)

### Step 5: Update Configuration
- application.properties
- SecurityConfig.java
- JwtTokenProvider.java
- DataInitializer.java

### Step 6: Test
- Unit tests
- Integration tests
- Manual API testing with Swagger

### Step 7: Update Database
- Run application with `spring.jpa.hibernate.ddl-auto=update`
- Or manually run SQL migrations

## Migration Checklist

- [ ] Remove CourseSelectionController.java
- [ ] Remove CourseSelectionService.java
- [ ] Create all new repositories
- [ ] Create ValidationService
- [ ] Create AllocationService
- [ ] Create AuditService
- [ ] Create all new DTOs
- [ ] Create HODController
- [ ] Create LecturerController
- [ ] Create AdminController
- [ ] Refactor StudentController
- [ ] Update AuthenticationService
- [ ] Update SecurityConfig
- [ ] Update JwtTokenProvider
- [ ] Update DataInitializer
- [ ] Update database schema
- [ ] Remove/update tests
- [ ] Test complete workflow
- [ ] Update API documentation in Swagger

## Notes

- Keep README_OLD.md as reference
- All old code in backup branch if needed
- Test incrementally as you refactor
- Use feature flags if deploying gradually

---

**Important**: Do NOT remove old files until new functionality is tested and working!
