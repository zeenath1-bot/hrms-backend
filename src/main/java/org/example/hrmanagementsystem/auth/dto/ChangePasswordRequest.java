package org.example.hrmanagementsystem.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be  be at least 8 characters")
    private String newPassword;
}
