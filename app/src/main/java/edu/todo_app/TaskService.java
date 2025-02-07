package edu.todo_app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TaskService {
    private final List<Task> tasks;
    private final Scanner scanner;
    private int nextId = 1;

    public TaskService(List<Task> tasks, Scanner scanner) {
        this.tasks = tasks;
        this.scanner = scanner;
    }

    public void addTask() {
        System.out.println("Введите название задачи: ");
        String title = scanner.nextLine();

        System.out.println("Введите описание задачи: ");
        String description = scanner.nextLine();

        System.out.println("Введите срок выполнения задачи в формате дд.мм.гг ");

        LocalDate dueDate = null;
        while (dueDate == null) {
            System.out.println("Введите срок выполнения задачи в формате дд.мм.гг ");
            String dueDateString = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");

            try {
                dueDate = LocalDate.parse(dueDateString, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты! Используйте формат дд.мм.гг.");
            }
        }

        tasks.add(new Task(nextId++, title, description, dueDate, TaskStatus.DONE));
    }

    public void displayTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        } else {
            tasks.forEach(System.out::println);
        }
    }
    private void displayFilteredTasks(List<Task> taskList) {
        if (taskList.isEmpty()) {
            System.out.println("Нет задач с таким статусом");
        } else {
            taskList.forEach(System.out::println);
        }
    }


    public void editTask() {
        if (tasks.isEmpty()) {
            System.out.println("Ошибка редактирования: cписок задач пуст");
            return;
        }
        System.out.println("Выберите задачу, которую вы хотите изменить: ");
        displayTasks();
        String id = scanner.nextLine();
        Task task =  taskFinding(id);

        System.out.println("""
                Что бы вы хотели изменить?
                1. Название
                2. Описание
                3. Срок выполнения
                4. Статус
                5. Выйти
                """);
        System.out.print(">> ");
        String editChoice = scanner.nextLine();

        switch (editChoice) {
            case "1" -> {
                System.out.println("Введите новое название задачи: ");
                String newTitle = scanner.nextLine();
                task.setTitle(newTitle);
                System.out.println("Название изменено!");
            }
            case "2" -> {
                System.out.println("Введите новое описание задачи: ");
                String newDescription = scanner.nextLine();
                task.setDescription(newDescription);
                System.out.println("Описание изменено!");
            }
            case "3" -> {
                System.out.println("Введите новый срок выполнения задачи в формате дд.мм.гг");
                String newDueDate = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
                task.setDueDate(LocalDate.parse(newDueDate, formatter));
                System.out.println("Срок выполнения изменён!");
            }
            case "4" -> {
                System.out.println("""
                        Выберите статус задачи:
                        1. В процессе
                        2. Выполнено
                        3. Выход
                        """);
                System.out.print(">> ");
                String statusChoice = scanner.nextLine();
                switch (statusChoice) {
                    case "1" -> {
                        task.setStatus(TaskStatus.IN_PROCESS);
                        System.out.println("Статус изменён!");
                    }
                    case "2" -> {
                        task.setStatus(TaskStatus.DONE);
                        System.out.println("Статус изменён!");
                    }
                    case "3" -> {
                        return;
                    }
                    default -> System.out.println("Неверный ввод, попробуйте снова");
                }
            }
            case "5" -> {
                return;
            }
            default -> System.out.println("Неверный ввод, попробуйте снова");

        }
    }

    public void removeTask() {
        if (tasks.isEmpty()) {
            System.out.println("Ошибка удаления: cписок задач пуст");
            return;
        }

        System.out.println("Выберите задачу, которую вы хотите удалить: ");
        displayTasks();
        String id = scanner.nextLine();
        Task task = taskFinding(id);
        if (task != null) {
            tasks.remove(task);
            reindexTasks();
            System.out.println("Выбранная задача удалена!");
        } else {
            System.out.println("Удаление не выполнено: задача не найдена");
        }
    }

    public void filterTasksByStatus() {
        if (tasks.isEmpty()) {
            System.out.println("Ошибка фильтрации: cписок задач пуст");
            return;
        }
        System.out.print("Введите статус (Выполнить / В процессе / Выполнено): ");
        String statusInput = scanner.nextLine().trim();
        try {
            TaskStatus status = TaskStatus.fromString(statusInput);
            List<Task> filteredTasks = tasks.stream()
                    .filter(task -> task.getStatus() == status)
                    .collect(Collectors.toList());
            displayFilteredTasks(filteredTasks);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: некорректный статус. Попробуйте снова.");
        }
    }

    public void sortTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Ошибка сортировки: cписок задач пуст");
            return;
        }

        System.out.println("""
                Отсортировать задачи по:
                1. Сроку выполнения
                2. Статусу
                3. Выход
                """);
        System.out.print(">> ");
        String sortChoice = scanner.nextLine();
        switch (sortChoice) {
            case "1" -> {
                tasks.sort(Comparator.comparing(Task::getDueDate));
                System.out.println("Задачи отсортированы по сроку выполнения!");
            }
            case "2" -> {
                tasks.sort(Comparator.comparing(task -> task.getStatus().ordinal()));
                System.out.println("Задачи отсортированы по статусу");
            }
            case "3" -> System.out.println("Сортировка отменена");
            default -> System.out.println("Неверный ввод, попробуйте снова");

        }
        reindexTasks();
        displayTasks();
    }

    public Task taskFinding(String id) {
        try {
            int taskId = Integer.parseInt(id);
            return tasks.stream()
                    .filter(t -> t.getId() == taskId)
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректный ID");
            return null;
        }
    }

    private void reindexTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setId(i + 1);
        }
    }
}
