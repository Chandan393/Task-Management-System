package com.example.taskmanager;

import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceImplTest {

    private TaskRepository repository;
    private TaskServiceImpl taskService;
    private Task sampleTask;

    @Before
    public void setup() {
        repository = Mockito.mock(TaskRepository.class);
        taskService = new TaskServiceImpl(repository);

        sampleTask = new Task(
                "1",
                "Sample Task",
                "Sample Description",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(2)
        );
    }

    @Test
    public void testCreateTask_Success() {
        when(repository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.createTask(
                "Sample Task",
                "Sample Description",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(3)
        );

        assertNotNull(result);
        assertEquals("Sample Task", result.getTitle());
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTask_MissingTitle() {
        taskService.createTask("", "desc", TaskStatus.PENDING, LocalDate.now().plusDays(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTask_PastDueDate() {
        taskService.createTask("Task 1", "desc", TaskStatus.PENDING, LocalDate.now().minusDays(1));
    }

    @Test
    public void testGetTask_Found() {
        when(repository.findById("1")).thenReturn(Optional.of(sampleTask));

        Optional<Task> result = taskService.getTask("1");

        assertTrue(result.isPresent());
        assertEquals("Sample Task", result.get().getTitle());
    }

    @Test
    public void testGetTask_NotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTask("999");

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateTask_Success() {
        when(repository.findById("1")).thenReturn(Optional.of(sampleTask));
        when(repository.save(any(Task.class))).thenReturn(sampleTask);

        Task updated = taskService.updateTask(
                "1",
                Optional.of("Updated Title"),
                Optional.of("Updated Desc"),
                Optional.of(TaskStatus.IN_PROGRESS),
                Optional.of(LocalDate.now().plusDays(5))
        );

        assertEquals("Updated Title", updated.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateTask_NotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        taskService.updateTask(
                "999",
                Optional.of("New Title"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    @Test
    public void testDeleteTask_Success() {
        when(repository.existsById("1")).thenReturn(true);
        doNothing().when(repository).deleteById("1");

        taskService.deleteTask("1");

        verify(repository, times(1)).deleteById("1");
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteTask_NotFound() {
        when(repository.existsById("999")).thenReturn(false);
        taskService.deleteTask("999");
    }

    @Test
    public void testListAllTasks_NoFilter() {
        Task t1 = new Task("1", "Task 1", "Desc1", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task t2 = new Task("2", "Task 2", "Desc2", TaskStatus.DONE, LocalDate.now().plusDays(3));

        when(repository.findAll()).thenReturn(Arrays.asList(t2, t1)); // out of order

        List<Task> tasks = taskService.listAllTasks(Optional.empty(), 0, 10);

        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle()); // should be sorted by due date
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testListAllTasks_WithFilter() {
        Task t1 = new Task("1", "Task 1", "Desc1", TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Task t2 = new Task("2", "Task 2", "Desc2", TaskStatus.DONE, LocalDate.now().plusDays(3));

        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Task> filtered = taskService.listAllTasks(Optional.of(TaskStatus.DONE), 0, 10);

        assertEquals(1, filtered.size());
        assertEquals(TaskStatus.DONE, filtered.get(0).getStatus());
    }
}
