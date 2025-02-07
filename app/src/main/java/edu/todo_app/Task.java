package edu.todo_app;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Task {
    private  int id;
    private String title;
    private String description;
    private LocalDate dueDate ;
    private TaskStatus status;




    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        String formattedDueDate = dueDate.format(formatter);

        return id + ". Название: " + title + " Описание: " + description + " Статус: " + status.getStatus() + " Выполнить до: " + formattedDueDate;
    }
}
