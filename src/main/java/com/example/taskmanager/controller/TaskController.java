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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest req) {
        Task task = service.createTask(
                req.getTitle(),
                req.getDescription(),
                req.getStatus(),
                req.getDueDate()
        );
        return ResponseEntity.status(201).body(toResponse(task));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String id) {
        Task task = service.getTask(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
        return ResponseEntity.ok(toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String id, @RequestBody UpdateTaskRequest req) {
        Task updated = service.updateTask(
                id,
                Optional.ofNullable(req.getTitle()),
                Optional.ofNullable(req.getDescription()),
                Optional.ofNullable(req.getStatus()).map(TaskStatus::valueOf),
                Optional.ofNullable(req.getDueDate())
        );
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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size) {

        Optional<TaskStatus> statusFilter = Optional.ofNullable(status).map(TaskStatus::valueOf);
        List<TaskResponse> response = service.listAllTasks(statusFilter, page, size)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    //convert Task entity object into a TaskResponse DTO
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }
}
