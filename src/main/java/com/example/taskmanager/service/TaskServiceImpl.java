package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task createTask(String title, String description, TaskStatus status, LocalDate dueDate) {
        validateTitle(title);
        validateDueDate(dueDate);

        Task task = new Task(
                UUID.randomUUID().toString(),
                title.trim(),
                description,
                status != null ? status : TaskStatus.PENDING,
                dueDate
        );
        return repository.save(task);
    }

    @Override
    public Optional<Task> getTask(String id) {
        return repository.findById(id);
    }

    @Override
    public Task updateTask(String id, Optional<String> title, Optional<String> description,
                           Optional<TaskStatus> status, Optional<LocalDate> dueDate) {

        Task task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));

        if (title.isPresent()) {
            String newTitle = title.get().trim();
            if (newTitle.isEmpty()) {
                throw new IllegalArgumentException("title cannot be blank");
            }
            task.setTitle(newTitle);
        }
        description.ifPresent(task::setDescription);
        status.ifPresent(task::setStatus);

        dueDate.ifPresent(d -> {
            validateDueDate(d);
            task.setDueDate(d);
        });

        return repository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Task not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Task> listAllTasks(Optional<TaskStatus> statusFilter, int page, int size) {
        List<Task> tasks = repository.findAll();

        // Apply status filter if present
        if (statusFilter.isPresent()) {
            tasks = tasks.stream()
                    .filter(task -> task.getStatus() == statusFilter.get())
                    .collect(Collectors.toList());
        }

        // Sort by due date
        tasks.sort(Comparator.comparing(Task::getDueDate));

        // Apply pagination
        int fromIndex = Math.max(0, page * size);
        if (fromIndex >= tasks.size()) return Collections.emptyList();

        int toIndex = Math.min(tasks.size(), fromIndex + size);
        return tasks.subList(fromIndex, toIndex);
    }

    /** Helper methods for validation **/

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title is required");
        }
    }

    private void validateDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("dueDate is required");
        }
        if (!dueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("dueDate must be in the future");
        }
    }
}
