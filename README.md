# Task Manager (Java 8, Spring Boot 2.7.x)

Simple Task Management API with in-memory storage.

## Requirements
- Java 8
- Maven

## Run
```
mvn clean package
mvn spring-boot:run
```

API base: `http://localhost:8080/api/tasks`

### Endpoints
- POST /api/tasks
  - body: { "title": "T", "description": "D", "status": "PENDING", "dueDate": "YYYY-MM-DD" }
- GET /api/tasks/{id}
- PUT /api/tasks/{id}
- DELETE /api/tasks/{id}
- GET /api/tasks?status=PENDING&page=0&size=10

Title and dueDate are required. dueDate must be in the future.

## Tests
```
mvn test
```

