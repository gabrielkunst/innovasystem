package com.gk.innovasystem.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateEventDTO {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotNull(message = "Jury evaluation start date is required")
    @Future(message = "Jury evaluation start date must be in the future")
    private LocalDateTime juryEvaluationStartDate;

    @NotNull(message = "Jury evaluation end date is required")
    @Future(message = "Jury evaluation end date must be in the future")
    private LocalDateTime juryEvaluationEndDate;

    @NotNull(message = "Popular evaluation start date is required")
    @Future(message = "Popular evaluation start date must be in the future")
    private LocalDateTime popularEvaluationStartDate;

    @NotNull(message = "Popular evaluation end date is required")
    @Future(message = "Popular evaluation end date must be in the future")
    private LocalDateTime popularEvaluationEndDate;

    @NotNull(message = "Created by is required")
    private Long createdBy;
}
