package org.example.hrmanagementsystem.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String role;
    private Long employeeId;
}
