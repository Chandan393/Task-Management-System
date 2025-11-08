package com.example.taskmanager;

import com.example.taskmanager.repository.InMemoryTaskRepository;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

    @Bean
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }
}
