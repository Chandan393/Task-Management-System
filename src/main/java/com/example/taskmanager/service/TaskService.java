package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(String title, String description, TaskStatus status, LocalDate dueDate);
    Optional<Task> getTask(String id);
    Task updateTask(String id, Optional<String> title, Optional<String> description, Optional<TaskStatus> status, Optional<LocalDate> dueDate);
    void deleteTask(String id);
    List<Task> listAllTasks(Optional<TaskStatus> statusFilter, int page, int size);
}
