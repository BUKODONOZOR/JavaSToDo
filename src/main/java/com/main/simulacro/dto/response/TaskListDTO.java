package com.main.simulacro.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskListDTO {
    private Long id;
    private String title;
    private LocalDate dueDate;
}
