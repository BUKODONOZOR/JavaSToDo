package com.main.simulacro.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskCreateDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long assignedUserId;  // El ID del usuario al que se asigna la tarea (solo para ADMIN)
}
