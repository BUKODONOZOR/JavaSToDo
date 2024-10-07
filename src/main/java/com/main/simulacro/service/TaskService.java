package com.main.simulacro.service;

import com.main.simulacro.dto.request.TaskUpdateDTO;
import com.main.simulacro.entity.Task;
import com.main.simulacro.entity.User;
import com.main.simulacro.repository.TaskRepository;
import com.main.simulacro.util.CustomUserDetails;
import com.main.simulacro.util.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Crear una nueva tarea
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, TaskUpdateDTO taskUpdateDTO) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        // Obtener detalles del usuario autenticado desde el SecurityContext
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verificar si el usuario tiene permisos para actualizar la tarea
        if (!userDetails.getRole().equals(Role.ADMIN) && existingTask.getAssignedUser().getId() != userDetails.getId()) {
            throw new SecurityException("No tienes permiso para actualizar esta tarea.");
        }

        // Actualizar la tarea solo si el usuario tiene permiso
        existingTask.setTitle(taskUpdateDTO.getTitle());
        existingTask.setDescription(taskUpdateDTO.getDescription());
        existingTask.setDueDate(taskUpdateDTO.getDueDate());

        // Guardar y retornar la tarea actualizada
        return taskRepository.save(existingTask);
    }



    // Eliminar una tarea
    public void deleteTask(Long taskId, CustomUserDetails userDetails) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        // Obtener detalles del usuario autenticado desde el SecurityContext
        userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Comparar el rol del usuario autenticado y verificar si es el dueño de la tarea
        if (!userDetails.getRole().equals(Role.ADMIN) && existingTask.getAssignedUser().getId() != userDetails.getId()) {
            throw new SecurityException("No tienes permiso para eliminar esta tarea.");
        }

        // Eliminar la tarea
        taskRepository.delete(existingTask);
    }




    // Obtener todas las tareas de un usuario
    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedUser_Id(userId);
    }

    // Obtener todas las tareas (solo accesible para ADMIN)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
