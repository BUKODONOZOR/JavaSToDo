package com.main.simulacro.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter

public abstract class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAd;

    @UpdateTimestamp
    private LocalDateTime updateAt;
}
