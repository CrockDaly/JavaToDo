package edu.todo_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    private TaskService taskService;
    private  List<Task> tasks;
    private  Scanner scanner;

    @BeforeEach
    void setUp() {
        tasks = new ArrayList<>();
        scanner = mock(Scanner.class);
        taskService = new TaskService(tasks, scanner);
    }

    @Test
    void testAddTask() {
        when(scanner.nextLine())
                .thenReturn("Задача 1")
                .thenReturn("Описание 1")
                .thenReturn("12.12.25");
        taskService.addTask();

        assertEquals(1, tasks.size());
        assertEquals("Задача 1", tasks.get(0).getTitle());
        assertEquals(TaskStatus.TODO, tasks.get(0).getStatus());
    }

    @Test
    void testRemoveTasks() {
        tasks.add(new Task(1, "Тестовая задача", "Описание", LocalDate.now(), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1");
        taskService.removeTask();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void testEditTaskTitle() {
        Task task = new Task(1, "Старое название", "Описание", LocalDate.now(), TaskStatus.TODO);
        tasks.add(task);

        when(scanner.nextLine()).thenReturn("1")
                        .thenReturn("1")
                        .thenReturn("Новое название");
        taskService.editTask();

        assertEquals("Новое название", task.getTitle());
    }

    @Test
    void testEditTaskDescription() {
        Task task = new Task(1, "Название", "Старое описание", LocalDate.now(), TaskStatus.TODO);
        tasks.add(task);

        when(scanner.nextLine()).thenReturn("1")
                .thenReturn("2")
                .thenReturn("Новое описание");
        taskService.editTask();

        assertEquals("Новое описание", task.getDescription());
    }

    @Test
    void testEditTaskDueDate() {
        Task task = new Task(1, "Название", "Описание", LocalDate.of(2025, 1, 1), TaskStatus.TODO);
        tasks.add(task);

        when(scanner.nextLine()).thenReturn("1")
                .thenReturn("3")
                .thenReturn("31.01.25");
        taskService.editTask();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        String expectedDate = LocalDate.of(2025, 1, 31).format(formatter);
        assertEquals(expectedDate, task.getDueDate().format(formatter));
    }

    @Test
    void testEditTaskStatus_InProgress() {
        Task task = new Task(1, "Название", "Описание", LocalDate.now(), TaskStatus.TODO);
        tasks.add(task);

        when(scanner.nextLine()).thenReturn("1")
                .thenReturn("4")
                .thenReturn("1");
        taskService.editTask();

        assertEquals(TaskStatus.IN_PROCESS, task.getStatus());
    }

    @Test
    void testEditTaskStatus_Completed() {
        Task task = new Task(1, "Название", "Описание", LocalDate.now(), TaskStatus.TODO);
        tasks.add(task);

        when(scanner.nextLine()).thenReturn("1")
                .thenReturn("4")
                .thenReturn("2");
        taskService.editTask();

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void testTaskFinding() {
        Task task = new Task(1, "Название", "Описание", LocalDate.now(), TaskStatus.TODO);
        tasks.add(task);

        Task found = taskService.taskFinding("1");

        assertNotNull(found);
        assertEquals("Название", found.getTitle());
    }

    @Test
    void testFilterTasksByStatus() {
        tasks.add(new Task(1, "Название 1", "Описание 1", LocalDate.now(), TaskStatus.TODO));
        tasks.add(new Task(2, "Название 2", "Описание 2", LocalDate.now(), TaskStatus.IN_PROCESS));

        when(scanner.nextLine()).thenReturn("В процессе");
        taskService.filterTasksByStatus();

        assertEquals(1, tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROCESS).count());
    }

    @Test
    void testSortTasksByDueDate() {
        tasks.add(new Task(1, "Название 1", "Описание 1", LocalDate.of(2025, 01, 02), TaskStatus.TODO));
        tasks.add(new Task(2, "Название 2", "Описание 2", LocalDate. of(2025, 01, 01), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1");
        taskService.sortTasks();

        assertEquals("Название 2", tasks.get(0).getTitle());
    }

    @Test
    void testSortTasksByStatus() {
        tasks.add(new Task(1, "Задача 1", "Описание", LocalDate.now(), TaskStatus.DONE));
        tasks.add(new Task(2, "Задача 2", "Описание", LocalDate.now(), TaskStatus.IN_PROCESS));

        when(scanner.nextLine()).thenReturn("2");
        taskService.sortTasks();

        assertEquals(TaskStatus.IN_PROCESS, tasks.get(0).getStatus());
    }

    @Test
    void invalidDateInput() {
        when(scanner.nextLine())
                .thenReturn("Задача 1")
                .thenReturn("Описание 1")
                .thenReturn("2025, 12, 12")
                .thenReturn("12.12.25");

        taskService.addTask();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        assertEquals(1, tasks.size());
        assertEquals(LocalDate.of(2025, 12, 12).format(formatter), tasks.get(0).getDueDate().format(formatter));
    }

    @Test
    void testRemoveTaskFromEmptyList() {
        when(scanner.nextLine()).thenReturn("1");
        taskService.removeTask();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void testEditTaskInEmptyList() {
        when(scanner.nextLine()).thenReturn("1");
        taskService.editTask();
        assertTrue(tasks.isEmpty());
    }
}
