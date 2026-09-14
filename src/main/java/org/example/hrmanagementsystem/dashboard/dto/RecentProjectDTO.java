package org.example.hrmanagementsystem.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RecentProjectDTO {
    private Long projectId;
    private String projectName;
    private LocalDate createdDate;
    private int employeeCount;
}
