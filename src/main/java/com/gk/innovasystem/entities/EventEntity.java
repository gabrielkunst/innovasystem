package com.gk.innovasystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private LocalDateTime juryEvaluationStartDate;

    @Column(nullable = false)
    private LocalDateTime juryEvaluationEndDate;

    @Column(nullable = false)
    private LocalDateTime popularEvaluationStartDate;

    @Column(nullable = false)
    private LocalDateTime popularEvaluationEndDate;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "event_jury", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<UserEntity> collaborators;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private List<IdeaEntity> ideas;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
