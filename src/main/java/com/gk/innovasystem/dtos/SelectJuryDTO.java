package com.gk.innovasystem.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SelectJuryDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Jury IDs list cannot be empty")
    private List<Long> juryIds;
}
