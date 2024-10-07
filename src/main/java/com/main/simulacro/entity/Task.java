package com.main.simulacro.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Task extends Audit {
    @NotNull
    private  String title;

    private String description;

    private LocalDate dueDate;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignedUser;



}
