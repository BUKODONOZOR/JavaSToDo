package com.main.simulacro.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long assignedUserId;
    private String assignedUsername;  // Nombre del usuario asignado para mostrar en las respuestas
}
