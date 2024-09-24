package com.gk.innovasystem.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateIdeaDTO {

    @NotBlank(message = "Idea name is required")
    private String name;

    @NotBlank(message = "Impact is required")
    private String impact;

    @NotNull(message = "Estimated cost is required")
    private Double estimatedCost;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Created by is required")
    private Long createdBy;

    @NotNull(message = "Event is required")
    private Long eventId;
}
