# 🧩 Task Manager (Java 8, Spring Boot 2.7.x)

A simple **Task Management CRUD API** built with **Spring Boot** and **in-memory storage**.  
This project demonstrates clean architecture using **Controller → Service → Repository** layers, along with **unit and integration tests**.

---

## ⚙️ Requirements
- **Java 8**
- **Maven 3.x**

---

## 🏗️ Setup & Run

### 1️⃣ Clone the repository

First, navigate to your desired folder in the terminal:

```bash
cd /path/to/your/desired/folder
git clone https://github.com/https://github.com/Chandan393/Task-Management-System.git

---

## 🚀 How to Run

### 1️⃣ Build the project
```bash
mvn clean package
```

### 2️⃣ Run the application
```bash
mvn spring-boot:run
```

Once the application starts, it will be available at:  
👉 **http://localhost:8080/tasks**

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|:-------:|:----------|:-------------|
| **POST** | `/tasks` | Create a new task |
| **GET** | `/tasks/{id}` | Get a task by ID |
| **PUT** | `/tasks/{id}` | Update a task |
| **DELETE** | `/tasks/{id}` | Delete a task |
| **GET** | `/tasks?status=PENDING&page=0&size=10` | List all tasks (optionally filter by status) |

---

## 🧾 Example JSON Request

### ➕ Create Task
```json
{
  "title": "My Task",
  "description": "Complete unit testing",
  "status": "PENDING",
  "dueDate": "2025-11-10"
}
```

---

## 🧠 Notes
- `title` and `dueDate` are **required**
- `dueDate` must be **in the future**
- Default port: **8080**

---

## 🧪 Run Tests
To run the full test suite (Controller + Service tests):

```bash
mvn test
```

---

## 📂 Project Structure

```
taskmanager/
├── src/
│   ├── main/java/com/example/taskmanager/
│   │   ├── controller/       # REST Controllers
│   │   ├── dto/              # Request and Response DTOs
│   │   ├── exception/        # Custom exceptions
│   │   ├── model/            # Entity (Task)
│   │   ├── repository/       # In-memory Repository
│   │   └── service/          # Business Logic Layer
│   └── test/java/com/example/taskmanager/
│       ├── TaskControllerTest.java  # Integration tests using MockMvc
│       └── TaskServiceImplTest.java # Unit tests with Mockito
├── pom.xml
└── README.md
```

---

## 🧰 Technologies Used
- **Java 8**
- **Spring Boot 2.7.x**
- **JUnit 4**
- **Mockito**
- **MockMvc** (for controller testing)
- **Maven** (for dependency management)

---

## 🧠 Example Usage (via curl)

### Create a Task
```bash
curl -X POST http://localhost:8080/tasks      -H "Content-Type: application/json"      -d '{"title":"New Task","description":"Write README","status":"PENDING","dueDate":"2025-11-10"}'
```

### Get a Task by ID
```bash
curl -X GET http://localhost:8080/tasks/{id}
```

### Update a Task
```bash
curl -X PUT http://localhost:8080/tasks/{id}      -H "Content-Type: application/json"      -d '{"title":"Updated Task Title","status":"IN_PROGRESS"}'
```

### Delete a Task
```bash
curl -X DELETE http://localhost:8080/tasks/{id}
```

### List All Tasks
```bash
curl -X GET "http://localhost:8080/tasks?status=PENDING&page=0&size=5"
```

---

## 👨‍💻 Author
**Chandan Gope**  
Full Stack Developer | Java • Spring Boot • React • Redux • PostgreSQL

