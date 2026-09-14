package org.example.hrmanagementsystem.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RecentEmployeeDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String department;
    private String status;
    private LocalDate joinDate;
}

