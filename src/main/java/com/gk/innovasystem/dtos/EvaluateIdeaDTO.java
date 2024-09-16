package com.gk.innovasystem.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvaluateIdeaDTO {

    @NotNull(message = "Juror ID is required")
    private Long jurorId;

    @NotNull(message = "Idea ID is required")
    private Long ideaId;

    @NotNull(message = "Score is required")
    private Double score;
}
