package edu.todoapp;

import java.util.Scanner;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    private final TaskService taskService = new TaskService(scanner);


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
                case "1" -> taskService.addTask();
                case "2" -> taskService.displayTasks();
                case "3" -> taskService.editTask();
                case "4" -> taskService.removeTask();
                case "5" -> taskService.filterTasksByStatus();
                case "6" -> taskService.sortTasks();
                case "7" -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный ввод, попробуйте снова.");
            }
        }
    }
}
