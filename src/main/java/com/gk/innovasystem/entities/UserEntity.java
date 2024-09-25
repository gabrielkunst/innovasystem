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
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<EventEntity> events;

    @ManyToOne
    @JoinColumn(name = "idea_id")
    private IdeaEntity idea;

    @ManyToMany(mappedBy = "collaborators")
    @JsonIgnore
    private List<EventEntity> eventsCollaborated;

    @OneToMany(mappedBy = "juror")
    @JsonIgnore
    private List<EvaluationEntity> evaluations;

    public enum Role {
        ADMIN, COLLABORATOR, JUROR, VOLUNTEER
    }

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}