package com.gk.innovasystem.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "evaluations")
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double score;


    @ManyToOne
    @JoinColumn(name = "idea_id")
    private IdeaEntity idea;

    @ManyToOne
    @JoinColumn(name = "juror_id")
    private UserEntity juror;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;


    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
