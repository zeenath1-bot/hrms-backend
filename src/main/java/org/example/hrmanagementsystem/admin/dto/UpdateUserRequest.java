package org.example.hrmanagementsystem.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.hrmanagementsystem.enums.RoleType;
import org.hibernate.engine.spi.ManagedEntity;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank(message = "Valid username is required")
    private String username;

    @NotNull(message = "Valid role type is required")
    private RoleType role;


    private Boolean active;
}
