package edu.todoapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    private TaskService taskService;
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        scanner = mock(Scanner.class);
        taskService = new TaskService(scanner);
    }

    @Test
    void testAddTask() {
        when(scanner.nextLine())
                .thenReturn("Задача 1")
                .thenReturn("Описание 1")
                .thenReturn("12.12.25");
        taskService.addTask();

        List<Task> tasks = taskService.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Задача 1", tasks.get(0).getTitle());
        assertEquals(TaskStatus.TODO, tasks.get(0).getStatus());
    }

    @Test
    void testRemoveTasks() {
        taskService.getTasks().add(new Task(1, "Тестовая задача", "Описание", LocalDate.now(), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1");
        taskService.removeTask();

        assertTrue(taskService.getTasks().isEmpty());
    }

    @Test
    void testEditTaskTitle() {
        taskService.getTasks().add(new Task(1, "Старое название", "Описание", LocalDate.now(), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1", "1", "Новое название");
        taskService.editTask();

        assertEquals("Новое название", taskService.getTasks().get(0).getTitle());
    }

    @Test
    void testEditTaskDescription() {
        taskService.getTasks().add(new Task(1, "Название", "Старое описание", LocalDate.now(), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1", "2", "Новое описание");
        taskService.editTask();

        assertEquals("Новое описание", taskService.getTasks().get(0).getDescription());
    }

    @Test
    void testEditTaskDueDate() {
        taskService.getTasks().add(new Task(1, "Название", "Описание", LocalDate.of(2025, 1, 1), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1", "3", "31.01.25");
        taskService.editTask();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        assertEquals("31.01.25", taskService.getTasks().get(0).getDueDate().format(formatter));
    }

    @Test
    void testEditTaskStatus_InProgress() {
        taskService.getTasks().add(new Task(1, "Название", "Описание", LocalDate.now(), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1", "4", "1");
        taskService.editTask();

        assertEquals(TaskStatus.IN_PROCESS, taskService.getTasks().get(0).getStatus());
    }

    @Test
    void testEditTaskStatus_Completed() {
        taskService.getTasks().add(new Task(1, "Название", "Описание", LocalDate.now(), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1", "4", "2");
        taskService.editTask();

        assertEquals(TaskStatus.DONE, taskService.getTasks().get(0).getStatus());
    }

    @Test
    void testTaskFinding() {
        taskService.getTasks().add(new Task(1, "Название", "Описание", LocalDate.now(), TaskStatus.TODO));

        Optional<Task> found = taskService.getTask("1");

        assertNotNull(found);
        assertEquals("Название", found.get().getTitle());
    }

    @Test
    void testFilterTasksByStatus() {
        taskService.getTasks().add(new Task(1, "Название 1", "Описание 1", LocalDate.now(), TaskStatus.TODO));
        taskService.getTasks().add(new Task(2, "Название 2", "Описание 2", LocalDate.now(), TaskStatus.IN_PROCESS));

        when(scanner.nextLine()).thenReturn("В процессе");
        taskService.filterTasksByStatus();

        assertEquals(1, taskService.getTasks().stream().filter(t -> t.getStatus() == TaskStatus.IN_PROCESS).count());
    }

    @Test
    void testSortTasksByDueDate() {
        taskService.getTasks().add(new Task(1, "Название 1", "Описание 1", LocalDate.of(2025, 1, 2), TaskStatus.TODO));
        taskService.getTasks().add(new Task(2, "Название 2", "Описание 2", LocalDate.of(2025, 1, 1), TaskStatus.TODO));

        when(scanner.nextLine()).thenReturn("1");
        taskService.sortTasks();

        assertEquals("Название 2", taskService.getTasks().get(0).getTitle());
    }

    @Test
    void testSortTasksByStatus() {
        taskService.getTasks().add(new Task(1, "Задача 1", "Описание", LocalDate.now(), TaskStatus.DONE));
        taskService.getTasks().add(new Task(2, "Задача 2", "Описание", LocalDate.now(), TaskStatus.IN_PROCESS));

        when(scanner.nextLine()).thenReturn("2");
        taskService.sortTasks();

        assertEquals(TaskStatus.IN_PROCESS, taskService.getTasks().get(0).getStatus());
    }

    @Test
    void invalidDateInput() {
        when(scanner.nextLine()).thenReturn("Задача 1", "Описание 1", "2025, 12, 12", "12.12.25");
        taskService.addTask();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        assertEquals(1, taskService.getTasks().size());
        assertEquals(LocalDate.of(2025, 12, 12).format(formatter), taskService.getTasks().get(0).getDueDate().format(formatter));
    }

    @Test
    void testRemoveTaskFromEmptyList() {
        when(scanner.nextLine()).thenReturn("1");
        taskService.removeTask();

        assertTrue(taskService.getTasks().isEmpty());
    }

    @Test
    void testEditTaskInEmptyList() {
        when(scanner.nextLine()).thenReturn("1");
        taskService.editTask();
        assertTrue(taskService.getTasks().isEmpty());
    }
}
