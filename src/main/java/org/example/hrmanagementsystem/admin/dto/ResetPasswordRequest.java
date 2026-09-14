package org.example.hrmanagementsystem.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String newPassword;
}
