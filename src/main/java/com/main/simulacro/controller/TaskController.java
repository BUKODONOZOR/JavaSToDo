package com.main.simulacro.controller;

import com.main.simulacro.dto.request.TaskCreateDTO;
import com.main.simulacro.dto.request.TaskUpdateDTO;
import com.main.simulacro.dto.response.TaskResponseDTO;
import com.main.simulacro.dto.response.UserResponseDTO;
import com.main.simulacro.entity.Task;
import com.main.simulacro.entity.User;
import com.main.simulacro.service.TaskService;
import com.main.simulacro.service.UserService;
import com.main.simulacro.util.CustomUserDetails;
import com.main.simulacro.util.Role;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    private final UserService userService;

    public TaskController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Obtener detalles del usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Obtener información del usuario autenticado mediante UserResponseDTO
        User currentUser = userService.getUsByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Crear la tarea
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setDescription(taskCreateDTO.getDescription());
        task.setDueDate(taskCreateDTO.getDueDate());

        // Asignar tarea al usuario
        if (userDetails.getRole().equals(Role.ADMIN) && taskCreateDTO.getAssignedUserId() != null) {
            // Si es ADMIN y se especifica un usuario asignado
            User assignedUser = userService.getUserById(taskCreateDTO.getAssignedUserId());
            task.setAssignedUser(assignedUser);
        } else {
            // Si es USER o no se especifica assignedUserId
            User assignedUser = userService.getUserById(currentUser.getId());
            task.setAssignedUser(assignedUser);
        }

        Task createdTask = taskService.createTask(task);

        // Crear la respuesta con TaskResponseDTO
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(createdTask.getId());
        taskResponseDTO.setTitle(createdTask.getTitle());
        taskResponseDTO.setDescription(createdTask.getDescription());
        taskResponseDTO.setDueDate(createdTask.getDueDate());
        taskResponseDTO.setAssignedUserId(createdTask.getAssignedUser().getId());
        taskResponseDTO.setAssignedUsername(createdTask.getAssignedUser().getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @PutMapping("/{taskId}")
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        Task task = taskService.updateTask(taskId, taskUpdateDTO);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Authentication authentication) {
        // Obtener detalles del usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Llamar al servicio para eliminar la tarea, pasando los detalles del usuario
        taskService.deleteTask(taskId, userDetails);

        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks")
    @RolesAllowed("USER")
    public ResponseEntity<List<Task>> getMyTasks(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userService.getUsByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Task> tasks = taskService.getTasksByUser(currentUser.getId());
        return ResponseEntity.ok(tasks);
    }
}
