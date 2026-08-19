package org.example.hrmanagementsystem.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.hrmanagementsystem.enums.RoleType;
import org.springframework.security.core.parameters.P;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank(message = "Valid username is required" )
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role type is required")
    private RoleType role;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    public void setUsername(String username) {
        this.username = (username != null) ? username.trim() : null ;
    }
}

