package com.gk.innovasystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ideas")
public class IdeaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Idea name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Impact is required")
    @Column(nullable = false)
    private String impact;

    @NotNull(message = "Estimated cost is required")
    @Column(nullable = false)
    private Double estimatedCost;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @OneToMany(mappedBy = "idea")
    @JsonIgnore
    private List<UserEntity> jury;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @OneToMany(mappedBy = "idea")
    @JsonIgnore
    private List<EvaluationEntity> evaluations;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
