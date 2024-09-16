package com.gk.innovasystem.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoteDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Idea ID is required")
    private Long ideaId;

    @NotNull(message = "Event ID is required")
    private Long eventId;
}
