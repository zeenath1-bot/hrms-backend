package org.example.hrmanagementsystem.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CurrentUserResponseDTO {
    private String username;
    private String role;
    private boolean active;
    private boolean mustChangePassword;

    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String department;
    private String jobPosition;

    private LocalDate joinDate;

}
