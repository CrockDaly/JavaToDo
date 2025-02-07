package edu.todo_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;
    private final Scanner scanner = new Scanner(System.in);
    private final TaskService taskService = new TaskService(tasks, scanner);


    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        while (true) {
            System.out.println("\nToDo List:");
            System.out.println("""
                    \nВыберите действие:
                    1. Добавить
                    2. Вывести список задач
                    3. Редактировать
                    4. Удалить
                    5. Отфильтровать по статусу
                    6. Отсортировать
                    7. Выход
                    """);
            System.out.print(">> ");
            String command = scanner.nextLine();

            switch (command) {
                case "add" -> taskService.addTask();
                case "list" -> taskService.displayTasks();
                case "edit" -> taskService.editTask();
                case "delete" -> taskService.removeTask();
                case "filter" -> taskService.filterTasksByStatus();
                case "sort" -> taskService.sortTasks();
                case "exit" -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный ввод, попробуйте снова.");
            }
        }
    }
}
