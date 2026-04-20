# Spring Boot REST API Examples

This Spring Boot application provides several example REST API GET endpoints to demonstrate common patterns.

## Starting the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Available Endpoints

### 1. Simple Test Endpoint
- **URL:** `GET /api/test`
- **Description:** Simple "Hello World" endpoint
- **Example:** `http://localhost:8080/api/test`
- **Response:** Plain text string

### 2. Status Endpoint
- **URL:** `GET /api/status`
- **Description:** Returns application status as JSON
- **Example:** `http://localhost:8080/api/status`
- **Response:** JSON object with service info, status, timestamp, and version

### 3. Get All Users
- **URL:** `GET /api/users`
- **Description:** Returns list of all users
- **Example:** `http://localhost:8080/api/users`
- **Response:** Array of user objects

### 4. Get User by ID
- **URL:** `GET /api/users/{userId}`
- **Description:** Returns specific user by ID
- **Example:** `http://localhost:8080/api/users/1`
- **Response:** User object or 404 if not found

### 5. Search Users with Query Parameters
- **URL:** `GET /api/users/search`
- **Description:** Search users with optional filters
- **Parameters:**
  - `name` (optional): Filter by name (case-insensitive partial match)
  - `minAge` (optional): Minimum age filter
  - `maxAge` (optional): Maximum age filter
- **Examples:**
  - `http://localhost:8080/api/users/search?name=john`
  - `http://localhost:8080/api/users/search?minAge=25&maxAge=35`
  - `http://localhost:8080/api/users/search?name=doe&minAge=20`
- **Response:** Array of matching user objects

### 6. Get User Profile Section
- **URL:** `GET /api/users/{userId}/profile/{section}`
- **Description:** Returns specific section of user's profile
- **Path Variables:**
  - `userId`: User ID
  - `section`: Profile section (`basic`, `contact`, or `demographics`)
- **Examples:**
  - `http://localhost:8080/api/users/1/profile/basic`
  - `http://localhost:8080/api/users/2/profile/contact`  
  - `http://localhost:8080/api/users/3/profile/demographics`
- **Response:** JSON object with requested profile section data

## Sample Data

The application includes sample users for testing:
- User 1: John Doe (john.doe@example.com, age 30)
- User 2: Jane Smith (jane.smith@example.com, age 25)  
- User 3: Bob Johnson (bob.johnson@example.com, age 35)

## Testing with curl

```bash
# Test basic endpoint
curl http://localhost:8080/api/test

# Get status
curl http://localhost:8080/api/status

# Get all users
curl http://localhost:8080/api/users

# Get specific user
curl http://localhost:8080/api/users/1

# Search users
curl "http://localhost:8080/api/users/search?name=john"
curl "http://localhost:8080/api/users/search?minAge=25&maxAge=35"

# Get profile sections
curl http://localhost:8080/api/users/1/profile/basic
curl http://localhost:8080/api/users/2/profile/contact
curl http://localhost:8080/api/users/3/profile/demographics
```

## Features Demonstrated

1. **Simple string responses**
2. **JSON object responses** 
3. **Path variables** (`@PathVariable`)
4. **Query parameters** (`@RequestParam`)
5. **Multiple path variables**
6. **HTTP status codes** (200 OK, 404 Not Found, 400 Bad Request)
7. **ResponseEntity** for flexible HTTP responses
8. **Data filtering and searching**
9. **Logging** for debugging and monitoring
10. **In-memory data storage** (for demonstration)