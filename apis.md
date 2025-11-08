 API Documentation

### Authentication APIs

**POST** `/api/auth/login`  
Authenticate user and receive JWT token

**POST** `/api/auth/register`  
Register new user account

**POST** `/api/auth/refresh`  
Refresh expired JWT token

**GET** `/api/auth/profile`  
Get current user profile

---

### Department Management APIs

**GET** `/api/departments`  
List all departments

**GET** `/api/departments/{id}`  
Get department by ID

**POST** `/api/departments`  
Create new department (Admin only)

**PUT** `/api/departments/{id}`  
Update department details

**GET** `/api/departments/{id}/courses`  
Get all courses in a department

---

### Course Management APIs

**GET** `/api/courses`  
Browse all available courses with filters

**GET** `/api/courses/{id}`  
Get course details by ID

**POST** `/api/courses`  
Create new course (HOD only)

**PUT** `/api/courses/{id}`  
Update course information

**DELETE** `/api/courses/{id}`  
Delete course (Admin/HOD only)

**GET** `/api/courses/{id}/requirements`  
Get all requirements for a course

**GET** `/api/courses/{id}/enrollments`  
Get all students enrolled in course

**GET** `/api/courses/search`  
Search courses by title, code, or department

---

### HOD-Specific APIs

**POST** `/api/hod/courses`  
Create new course in department

**PUT** `/api/hod/courses/{courseId}/assign-lecturer`  
Assign course to a lecturer

**GET** `/api/hod/courses`  
List all department courses

**PUT** `/api/hod/courses/{courseId}/capacity`  
Update maximum course capacity

**DELETE** `/api/hod/courses/{courseId}/assignment`  
Unassign lecturer from course

**GET** `/api/hod/lecturers`  
List all lecturers in department

**GET** `/api/hod/reports/enrollment-statistics`  
View department enrollment statistics

**GET** `/api/hod/reports/course-demand`  
Analyze course demand and waitlists

**POST** `/api/hod/courses/{courseId}/clone`  
Clone course for new semester

---

### Lecturer-Specific APIs

**GET** `/api/lecturer/courses`  
List all assigned courses

**GET** `/api/lecturer/courses/{courseId}`  
Get detailed course information

**POST** `/api/lecturer/courses/{courseId}/requirements`  
Add prerequisite or requirement to course

**PUT** `/api/lecturer/courses/{courseId}/requirements/{requirementId}`  
Update existing course requirement

**DELETE** `/api/lecturer/courses/{courseId}/requirements/{requirementId}`  
Remove course requirement

**GET** `/api/lecturer/courses/{courseId}/enrollments`  
View all enrolled students in course

**GET** `/api/lecturer/courses/{courseId}/enrollment-requests`  
View pending enrollment requests

**GET** `/api/lecturer/courses/{courseId}/roster`  
Download printable class roster

**PUT** `/api/lecturer/courses/{courseId}/status`  
Update course status (ACTIVE, CLOSED, CANCELLED)

**GET** `/api/lecturer/courses/{courseId}/statistics`  
View course enrollment and performance stats

**POST** `/api/lecturer/courses/{courseId}/announcements`  
Post announcement to enrolled students

---

### Student-Specific APIs

**GET** `/api/student/courses`  
Browse available courses for enrollment

**GET** `/api/student/courses/{courseId}`  
View detailed course information with requirements

**GET** `/api/student/courses/{courseId}/validate`  
Check eligibility for a specific course

**POST** `/api/student/enrollment-requests`  
Submit enrollment request for a course

**GET** `/api/student/enrollment-requests`  
View all my enrollment requests

**GET** `/api/student/enrollment-requests/{requestId}`  
Get specific enrollment request details

**DELETE** `/api/student/enrollment-requests/{requestId}`  
Cancel pending enrollment request

**GET** `/api/student/enrollments`  
View all currently enrolled courses

**GET** `/api/student/enrollments/history`  
View enrollment history with grades

**POST** `/api/student/enrollments/{enrollmentId}/drop`  
Drop an enrolled course

**GET** `/api/student/schedule`  
View current semester schedule

**GET** `/api/student/transcript`  
View academic transcript

**GET** `/api/student/progress`  
Check degree progress and requirements

**GET** `/api/student/recommendations`  
Get recommended courses based on history

---

### Enrollment Request Management APIs

**GET** `/api/enrollment-requests`  
List all enrollment requests (Admin/Registrar)

**GET** `/api/enrollment-requests/{id}`  
Get enrollment request by ID

**PUT** `/api/enrollment-requests/{id}/status`  
Update request status (APPROVED, REJECTED, WAITLISTED)

**POST** `/api/enrollment-requests/bulk-approve`  
Approve multiple requests at once

**GET** `/api/enrollment-requests/pending`  
Get all pending requests

**GET** `/api/enrollment-requests/course/{courseId}`  
Get all requests for specific course

---

### Allocation Management APIs

**POST** `/api/admin/allocation/run`  
Trigger course allocation process

**GET** `/api/admin/allocation/status`  
Check current allocation process status

**POST** `/api/admin/allocation/cancel`  
Cancel running allocation process

**GET** `/api/admin/allocation/results`  
View detailed allocation results

**POST** `/api/admin/allocation/rollback`  
Rollback last allocation

**GET** `/api/admin/allocation/history`  
View allocation history

**POST** `/api/admin/allocation/simulate`  
Run allocation simulation without committing

---

### Waitlist Management APIs

**GET** `/api/waitlists/course/{courseId}`  
View waitlist for a specific course

**POST** `/api/waitlists/{waitlistId}/notify`  
Notify waitlisted students of opening

**DELETE** `/api/waitlists/{waitlistId}`  
Remove student from waitlist

**POST** `/api/waitlists/{waitlistId}/promote`  
Manually promote student from waitlist

---

### Admin/Registrar Override APIs

**POST** `/api/admin/enrollment-requests/{requestId}/override`  
Override system decision on enrollment request

**POST** `/api/admin/enrollments/force-enroll`  
Force enroll student bypassing all rules

**POST** `/api/admin/enrollments/{enrollmentId}/force-drop`  
Force drop student from course

**PUT** `/api/admin/courses/{courseId}/emergency-capacity`  
Emergency capacity increase

**POST** `/api/admin/prerequisites/waive`  
Waive prerequisite requirement for student

---

### Reporting & Analytics APIs

**GET** `/api/reports/enrollment-summary`  
Overall enrollment summary by semester

**GET** `/api/reports/department/{departmentId}/analytics`  
Department-specific enrollment analytics

**GET** `/api/reports/course/{courseId}/trends`  
Historical enrollment trends for course

**GET** `/api/reports/student/{studentId}/academic-progress`  
Student academic progress report

**GET** `/api/reports/capacity-utilization`  
Course capacity utilization report

**GET** `/api/reports/waitlist-analysis`  
Waitlist statistics and patterns

**GET** `/api/reports/allocation-efficiency`  
Allocation process efficiency metrics

**POST** `/api/reports/custom`  
Generate custom report with filters

**GET** `/api/reports/export/{reportId}`  
Export report as PDF or Excel

---

### Audit & Logging APIs

**GET** `/api/audit-logs`  
View system audit trail

**GET** `/api/audit-logs/user/{userId}`  
View specific user's activity log

**GET** `/api/audit-logs/entity/{entityType}/{entityId}`  
View audit trail for specific entity

**GET** `/api/audit-logs/export`  
Export audit logs for compliance

---

### Notification APIs

**GET** `/api/notifications`  
Get all user notifications

**PUT** `/api/notifications/{id}/read`  
Mark notification as read

**DELETE** `/api/notifications/{id}`  
Delete notification

**POST** `/api/notifications/preferences`  
Update notification preferences

---

### System Configuration APIs

**GET** `/api/config/settings`  
Get system configuration settings

**PUT** `/api/config/settings`  
Update system settings (Admin only)

**GET** `/api/config/registration-periods`  
Get registration period configurations

**POST** `/api/config/registration-periods`  
Create new registration period

**PUT** `/api/config/registration-periods/{id}`  
Update registration period

**GET** `/api/config/academic-calendar`  
Get current academic calendar

**POST** `/api/config/academic-calendar`  
Create academic calendar for new year