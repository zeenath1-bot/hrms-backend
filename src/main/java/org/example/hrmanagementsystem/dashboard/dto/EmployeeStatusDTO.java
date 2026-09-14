package org.example.hrmanagementsystem.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeStatusDTO {
    private long active;
    private long resigned;
    private long terminated;
    private long retired;
}
