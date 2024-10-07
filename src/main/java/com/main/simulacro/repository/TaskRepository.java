package com.main.simulacro.repository;

import com.main.simulacro.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository  extends JpaRepository <Task, Long> {
    List<Task> findByAssignedUser_Id(Long userId); // Obtener tareas por ID de usuario

}
