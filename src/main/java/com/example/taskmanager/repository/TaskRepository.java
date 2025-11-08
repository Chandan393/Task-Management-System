package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(String id);
    void deleteById(String id);
    List<Task> findAll();
    boolean existsById(String id);
}
