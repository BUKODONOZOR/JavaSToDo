package com.main.simulacro.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskUpdateDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
}
