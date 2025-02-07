package edu.todo_app;

public enum TaskStatus {
    IN_PROCESS("В процессе"),
    DONE("Выполнено"),
    TODO("Выполнить");

    private final String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static TaskStatus fromString(String status) {
        for (TaskStatus ts : values()) {
            if (ts.status.equalsIgnoreCase(status)) {
                return ts;
            }
        }
        throw new IllegalArgumentException("Некорректный статус: " + status);
    }
}
