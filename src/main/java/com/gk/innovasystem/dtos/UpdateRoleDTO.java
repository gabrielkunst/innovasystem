package com.gk.innovasystem.dtos;

import com.gk.innovasystem.entities.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleDTO {
    @NotNull(message = "Admin ID is required")
    private Long adminId;

    @NotNull(message = "User ID is required")
    private UserEntity.Role role;
}
