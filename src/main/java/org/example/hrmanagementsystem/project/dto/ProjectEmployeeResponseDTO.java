package org.example.hrmanagementsystem.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectEmployeeResponseDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
}
