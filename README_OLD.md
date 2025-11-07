# Course Allocation System

A comprehensive course allocation and enrollment management system built with Spring Boot, PostgreSQL, and JWT authentication. This system manages the complete lifecycle of course allocation from HOD assignment to student enrollment through a structured four-phase workflow.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Project Configuration](#project-configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Sample Data](#sample-data)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)

## Prerequisites

### 1. Install Java 17

#### macOS (using Homebrew)
```bash
brew install openjdk@17
```

#### macOS (Manual Installation)
1. Download Java 17 from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [OpenJDK](https://adoptium.net/)
2. Install the downloaded package
3. Verify installation:
```bash
java -version
```
You should see: `openjdk version "17.x.x"`

#### Set JAVA_HOME on macOS (if needed)
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Linux (Fedora/RHEL/CentOS)
```bash
sudo dnf install java-17-openjdk-devel
```

#### Linux (Arch Linux)
```bash
sudo pacman -S jdk-openjdk
```

#### Set JAVA_HOME on Linux (if needed)
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

#### Windows
1. Download Java 17 from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [OpenJDK](https://adoptium.net/)
2. Run the installer (`.exe` file)
3. Follow the installation wizard
4. Verify installation:
```cmd
java -version
```

#### Set JAVA_HOME on Windows
1. Right-click "This PC" → Properties → Advanced System Settings
2. Click "Environment Variables"
3. Under "System Variables", click "New"
4. Variable name: `JAVA_HOME`
5. Variable value: `C:\Program Files\Java\jdk-17` (adjust path if different)
6. Click OK and restart Command Prompt/PowerShell

### 2. Install PostgreSQL

#### macOS (using Homebrew)
```bash
brew install postgresql@14
brew services start postgresql@14
```

#### macOS (Manual Installation)
1. Download PostgreSQL from [postgresql.org](https://www.postgresql.org/download/macosx/)
2. Install the downloaded package
3. Start PostgreSQL service

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Linux (Fedora/RHEL/CentOS)
```bash
sudo dnf install postgresql-server postgresql-contrib
sudo postgresql-setup --initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Linux (Arch Linux)
```bash
sudo pacman -S postgresql
sudo -u postgres initdb -D /var/lib/postgres/data
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Windows
1. Download PostgreSQL from [postgresql.org](https://www.postgresql.org/download/windows/)
2. Run the installer (`.exe` file)
3. During installation:
   - Choose installation directory (default is fine)
   - Set password for `postgres` user (remember this password!)
   - Choose port (default 5432 is fine)
4. Complete the installation
5. PostgreSQL service starts automatically

#### Verify PostgreSQL Installation
```bash
psql --version
```

**Note:** On Linux, you may need to switch to the `postgres` user first:
```bash
sudo -u postgres psql
```

### 3. Install Maven (Optional - Spring Boot includes Maven Wrapper)

#### macOS (using Homebrew)
```bash
brew install maven
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt install maven
```

#### Linux (Fedora/RHEL/CentOS)
```bash
sudo dnf install maven
```

#### Linux (Arch Linux)
```bash
sudo pacman -S maven
```

#### Windows
1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract the zip file to a directory (e.g., `C:\Program Files\Apache\maven`)
3. Add Maven to PATH:
   - Right-click "This PC" → Properties → Advanced System Settings
   - Click "Environment Variables"
   - Under "System Variables", find "Path" and click "Edit"
   - Add: `C:\Program Files\Apache\maven\bin`
   - Click OK and restart Command Prompt/PowerShell

#### Verify Maven Installation
```bash
mvn -version
```

**Note:** The project includes Maven Wrapper (`mvnw` on Unix/Mac, `mvnw.cmd` on Windows), so Maven installation is optional.

## Installation

### 1. Clone or Navigate to Project Directory

#### macOS / Linux
```bash
cd /path/to/course-allocation
```

#### Windows
```cmd
cd C:\path\to\course-allocation
```

### 2. Verify Project Structure
Ensure you have the following structure:
```
course-allocation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── pom.xml
└── README.md
```

## Database Setup

### 1. Start PostgreSQL Service

#### macOS (Homebrew)
```bash
brew services start postgresql@14
```

#### macOS (Manual Start)
```bash
pg_ctl -D /usr/local/var/postgres start
```

#### Linux (systemd)
```bash
sudo systemctl start postgresql
```

#### Linux (Check Status)
```bash
sudo systemctl status postgresql
```

#### Windows
PostgreSQL service should start automatically after installation. To manage it:
1. Open "Services" (Win + R, type `services.msc`)
2. Find "postgresql-x64-XX" service
3. Right-click → Start (if not running)

### 2. Create Database

#### macOS / Linux
Connect to PostgreSQL:
```bash
psql -U postgres
```

If you get a connection error, try:
```bash
# Linux - switch to postgres user first
sudo -u postgres psql

# Or try without user specification
psql postgres
```

#### Windows
1. Open Command Prompt or PowerShell
2. Navigate to PostgreSQL bin directory (usually `C:\Program Files\PostgreSQL\XX\bin`)
3. Run:
```cmd
psql -U postgres
```
Or use pgAdmin (GUI tool installed with PostgreSQL)

#### Create the Database
Once connected, run:
```sql
CREATE DATABASE course_allocation;
```

#### Verify Database Creation
```sql
\l
```

You should see `course_allocation` in the list.

#### Exit PostgreSQL
```sql
\q
```

### 3. Verify Database Connection

#### macOS / Linux
Test connection:
```bash
psql -U postgres -d course_allocation
```

#### Windows
```cmd
psql -U postgres -d course_allocation
```

If successful, you'll see the PostgreSQL prompt. Exit with `\q`.

## Project Configuration

### 1. Update Database Credentials (if needed)

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/course_allocation
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**Important:** Update the `username` and `password` if your PostgreSQL setup uses different credentials.

### 2. JWT Configuration

The JWT secret and expiration are already configured in `application.properties`:

```properties
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationInCourseAllocationSystem2024
jwt.expiration=86400000
```

**Security Note:** For production, change the JWT secret to a strong, randomly generated key.

## Running the Application

### Method 1: Using Maven Wrapper (Recommended)

```bash
./mvnw spring-boot:run
```

On Windows:
```bash
mvnw.cmd spring-boot:run
```

### Method 2: Using Maven (if installed)

```bash
mvn spring-boot:run
```

### Method 3: Using IDE (IntelliJ IDEA / Eclipse)

1. Open the project in your IDE
2. Wait for Maven dependencies to download
3. Locate `CourseAllocationApplication.java`
4. Right-click and select "Run" or "Debug"

### 4. Verify Application is Running

You should see output like:
```
Started CourseAllocationApplication in X.XXX seconds
```

The application runs on: `http://localhost:8080`

## API Documentation

### Access Swagger UI

Once the application is running, open your browser and navigate to:

```
http://localhost:8080/docs
```

You'll see the Swagger UI with all available API endpoints.

### API Documentation JSON

The OpenAPI JSON specification is available at:
```
http://localhost:8080/v3/api-docs
```

## Authentication

### 1. Login to Get JWT Token

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "studentId": "STU001",
  "pin": "1234"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "studentId": 1,
    "studentIdNumber": "STU001",
    "firstName": "Kwame",
    "lastName": "Asante",
    "email": "kwame.asante@university.edu",
    "department": "Computer Science",
    "year": 2,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 2. Using Token in Swagger UI

1. Copy the `token` value from the login response
2. Click the **"Authorize"** button (lock icon) at the top right of Swagger UI
3. Paste **only the token** (without "Bearer" prefix)
4. Click **"Authorize"**
5. All protected endpoints will now work

### 3. Using Token in API Requests

Include the token in the Authorization header:

```
Authorization: Bearer <your-token-here>
```

## Sample Data

The application automatically seeds sample data on first startup:

### Students (Pre-loaded)

| Student ID | Name | Department | Year | PIN |
|------------|------|------------|------|-----|
| STU001 | Kwame Asante | Computer Science | 2 | 1234 |
| STU002 | Ama Mensah | Computer Science | 3 | 5678 |
| STU003 | Kofi Osei | Mathematics | 1 | 9012 |
| STU004 | Akosua Boateng | Computer Science | 2 | 3456 |

### Courses (Pre-loaded)

- **CS201** - Data Structures and Algorithms (Level 2, 3 credits)
- **CS301** - Database Systems (Level 3, 3 credits)
- **CS202** - Object-Oriented Programming (Level 2, 4 credits)
- **MATH101** - Calculus I (Level 1, 4 credits)
- **CS401** - Software Engineering (Level 4, 3 credits)
- **CS302** - Operating Systems (Level 3, 3 credits)

### Semester

- **Fall 2024** (Active) - September 1, 2024 to December 15, 2024

## API Endpoints

### Authentication
- `POST /api/auth/login` - Student login (Public)

### Student Management (Protected)
- `POST /api/students` - Create student
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `GET /api/students/student-id/{studentId}` - Get by student ID
- `GET /api/students/department/{department}` - Get by department
- `GET /api/students/year/{year}` - Get by year
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Course Management (Protected)
- `POST /api/courses` - Create course
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `GET /api/courses/semester/{semesterId}` - Get by semester
- `GET /api/courses/department/{department}` - Get by department
- `GET /api/courses/instructor/{instructor}` - Get by instructor
- `GET /api/courses/search?name={name}` - Search courses
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

### Semester Management (Protected)
- `POST /api/semesters` - Create semester
- `GET /api/semesters` - Get all semesters
- `GET /api/semesters/{id}` - Get semester by ID
- `GET /api/semesters/active` - Get active semester
- `GET /api/semesters/active/list` - Get all active semesters
- `PUT /api/semesters/{id}` - Update semester
- `DELETE /api/semesters/{id}` - Delete semester

### Course Selection (Student Endpoints - Protected)
- `GET /api/student/courses/available?studentId={id}&semesterId={id}` - Get available courses
- `POST /api/student/courses/select?studentId={id}` - Select/enroll in course
- `DELETE /api/student/courses/drop/{enrollmentId}?studentId={id}` - Drop course
- `GET /api/student/courses/summary?studentId={id}&semesterId={id}` - Get enrollment summary

### Enrollment Management (Protected)
- `POST /api/enrollments` - Create enrollment
- `GET /api/enrollments` - Get all enrollments
- `GET /api/enrollments/{id}` - Get enrollment by ID
- `GET /api/enrollments/student/{studentId}` - Get by student
- `GET /api/enrollments/course/{courseId}` - Get by course
- `PUT /api/enrollments/{id}` - Update enrollment
- `DELETE /api/enrollments/{id}` - Delete enrollment

## Testing

### Quick Test Flow

1. **Start the application**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Access Swagger UI**
   - Open: `http://localhost:8080/docs`

3. **Login**
   - Use `POST /api/auth/login`
   - Student ID: `STU001`
   - PIN: `1234`
   - Copy the token from response

4. **Authorize in Swagger**
   - Click "Authorize" button
   - Paste token
   - Click "Authorize"

5. **Get Available Courses**
   - Use `GET /api/student/courses/available`
   - Parameters: `studentId=1`, `semesterId=1`

6. **Select a Course**
   - Use `POST /api/student/courses/select`
   - Parameter: `studentId=1`
   - Body: `{"courseId": 1}`

7. **View Enrollment Summary**
   - Use `GET /api/student/courses/summary`
   - Parameters: `studentId=1`, `semesterId=1`

## Troubleshooting

### Database Connection Issues

**Error:** `Connection refused` or `Failed to determine a suitable driver class`

**Solution:**

#### macOS
1. Verify PostgreSQL is running:
   ```bash
   brew services list
   ```
2. Check database exists:
   ```bash
   psql -U postgres -l
   ```

#### Linux
1. Verify PostgreSQL is running:
   ```bash
   sudo systemctl status postgresql
   ```
2. Start if not running:
   ```bash
   sudo systemctl start postgresql
   ```
3. Check database exists:
   ```bash
   sudo -u postgres psql -l
   ```

#### Windows
1. Verify PostgreSQL service is running:
   - Open Services (Win + R, type `services.msc`)
   - Find "postgresql-x64-XX" service
   - Ensure it's "Running"
2. Check database exists:
   ```cmd
   psql -U postgres -l
   ```

#### All Platforms
3. Verify credentials in `application.properties`
4. Ensure database name matches: `course_allocation`

### Port Already in Use

**Error:** `Port 8080 is already in use`

**Solution:**

#### macOS / Linux
1. Find process using port 8080:
   ```bash
   lsof -i :8080
   # or
   netstat -tulpn | grep 8080
   ```
2. Kill the process:
   ```bash
   kill -9 <PID>
   ```
3. Or change port in `application.properties`:
   ```properties
   server.port=8081
   ```

#### Windows
1. Find process using port 8080:
   ```cmd
   netstat -ano | findstr :8080
   ```
2. Kill the process (replace `<PID>` with the process ID):
   ```cmd
   taskkill /PID <PID> /F
   ```
3. Or change port in `application.properties`:
   ```properties
   server.port=8081
   ```

### JWT Token Issues

**Error:** `Invalid or expired token`

**Solution:**
1. Token expires after 24 hours - login again to get a new token
2. Ensure you're pasting the full token (it's a long string)
3. Don't include "Bearer" prefix in Swagger UI

### Swagger Not Loading

**Error:** `Failed to load API definition` or 500 error

**Solution:**
1. Ensure application started successfully
2. Check application logs for errors
3. Try accessing: `http://localhost:8080/v3/api-docs` directly
4. Clear browser cache and try again

## Project Structure

```
course-allocation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/courseallocation/course_allocation/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── exception/       # Exception handlers
│   │   │       ├── model/           # JPA entities
│   │   │       ├── repository/      # Data access layer
│   │   │       └── service/         # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.5.7** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **JWT (jjwt 0.12.5)** - Token-based authentication
- **Swagger/OpenAPI** - API documentation
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

## Features

- Student authentication with JWT tokens
- Course filtering by program and year level
- Course selection with credit and course limits
- Enrollment management
- Semester management
- Comprehensive API documentation
- Data validation
- Error handling
- Sample data seeding

## Security Features

- JWT-based authentication
- Token expiration (24 hours)
- Role-based access control
- Protected endpoints
- Secure password handling

## Credit and Course Limits

- **Maximum Credits:** 21 per semester
- **Maximum Courses:** 7 per semester
- **Minimum Credits:** 12 per semester (recommended)

## Support

For issues or questions, please check:
1. Application logs in the console
2. Swagger UI documentation at `/docs`
3. Database connection status
4. JWT token validity

## License

This project is for educational purposes.

---

**Happy Coding! - Joe**

