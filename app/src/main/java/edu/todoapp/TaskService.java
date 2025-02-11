package edu.todoapp;

import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class TaskService {
    @Getter
    private final List<Task> tasks;
    private final Scanner scanner;
    private int nextId = 1;

    public TaskService( Scanner scanner) {
        this.tasks = new ArrayList<>();
        this.scanner = scanner;
    }

    public void addTask() {
        System.out.println("Введите название задачи: ");
        String title = scanner.nextLine();

        System.out.println("Введите описание задачи: ");
        String description = scanner.nextLine();

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

        tasks.add(new Task(nextId++, title, description, dueDate, TaskStatus.TODO));
    }

    public void displayTasks(List<Task> tasksToDisplay) {
        if (tasksToDisplay.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        } else {
           tasksToDisplay.forEach(System.out::println);
        }
    }

    public void editTask() {
        if (tasks.isEmpty()) {
            System.out.println("Ошибка редактирования: cписок задач пуст");
            return;
        }

        System.out.println("Выберите задачу, которую вы хотите изменить: ");
        displayTasks(tasks);
        String id = scanner.nextLine();
        Optional<Task> optionalTask = getTask(id);

        if (optionalTask.isEmpty()) {
            System.out.println("Ошибка: задача не найдена");
            return;
        }

        Task task = optionalTask.get();
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
                case "1" -> editTitle(task);
                case "2" -> editDescription(task);
                case "3" -> editDueDate(task);
                case "4" -> editStatus(task);
                case "5" -> {
                    return;
                }
                default -> System.out.println("Неверный ввод, попробуйте снова");
            }
    }

    private void editTitle(Task task) {
        System.out.println("Введите новое название задачи: ");
        String newTitle = scanner.nextLine();
        task.setTitle(newTitle);
        System.out.println("Название изменено!");
    }

    private void editDescription(Task task) {
        System.out.println("Введите новое описание задачи: ");
        String newDescription = scanner.nextLine();
        task.setDescription(newDescription);
        System.out.println("Описание изменено!");
    }

    private void editDueDate(Task task) {
        System.out.println("Введите новый срок выполнения задачи в формате дд.мм.гг");
        String newDueDate = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");

        try {
            task.setDueDate(LocalDate.parse(newDueDate, formatter));
            System.out.println("Срок выполнения изменён!");
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка: Некорректный формат даты. Используйте формат дд.мм.гг");
        }
    }

    private void editStatus(Task task) {
        System.out.println("""
            Выберите статус задачи:
            1. В процессе
            2. Выполнено
            3. Выход
            """);
        System.out.print(">> ");
        String statusChoice = scanner.nextLine();

        if ("3".equals(statusChoice)) {
            return;
        }

        if (!statusChoice.equals("1") && !statusChoice.equals("2")) {
            System.out.println("Неверный ввод, попробуйте снова");
            return;
        }

        TaskStatus newStatus = statusChoice.equals("1") ? TaskStatus.IN_PROCESS : TaskStatus.DONE;
        task.setStatus(newStatus);
        System.out.println("Статус изменён!");
    }



    public void removeTask() {
        if (tasks.isEmpty()) {
            System.out.println("Ошибка удаления: cписок задач пуст");
            return;
        }

        System.out.println("Выберите задачу, которую вы хотите удалить: ");
        displayTasks(tasks);
        String id = scanner.nextLine();
       Optional<Task> task = getTask(id);
        if (task.isPresent()) {
            tasks.remove(task.get());
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
            displayTasks(filteredTasks);

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
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
        displayTasks(tasks);
    }

    public Optional<Task> getTask(String id) {
        try {
            int taskId = Integer.parseInt(id);
            return tasks.stream()
                    .filter(t -> t.getId() == taskId)
                    .findFirst();
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректный ID");
            return Optional.empty();
        }
    }


}
