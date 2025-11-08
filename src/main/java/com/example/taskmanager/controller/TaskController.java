package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.UpdateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.exception.NotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest req) {
        TaskStatus status = null;
        if (req.getStatus() != null) status = TaskStatus.valueOf(req.getStatus());
        Task t = service.createTask(req.getTitle(), req.getDescription(), status, req.getDueDate());
        return ResponseEntity.status(201).body(toResponse(t));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String id) {
        Task t = service.getTask(id).orElseThrow(new java.util.function.Supplier<NotFoundException>() {
            @Override public NotFoundException get() { return new NotFoundException("Task not found: " + id); }
        });
        return ResponseEntity.ok(toResponse(t));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String id, @RequestBody UpdateTaskRequest req) {
        Optional<String> title = Optional.ofNullable(req.getTitle());
        Optional<String> description = Optional.ofNullable(req.getDescription());
        Optional<TaskStatus> status = Optional.ofNullable(req.getStatus()).map(new java.util.function.Function<String, TaskStatus>() {
            @Override public TaskStatus apply(String s) { return TaskStatus.valueOf(s); }
        });
        Optional<java.time.LocalDate> dueDate = Optional.ofNullable(req.getDueDate());
        Task updated = service.updateTask(id, title, description, status, dueDate);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
        Optional<TaskStatus> statusFilter = (status == null) ? Optional.<TaskStatus>empty() : Optional.of(TaskStatus.valueOf(status));
        List<Task> tasks = service.listAllTasks(statusFilter, page, size);
        List<TaskResponse> resp = tasks.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(), t.getDueDate());
    }
}
