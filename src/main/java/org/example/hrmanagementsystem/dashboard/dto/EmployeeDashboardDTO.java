package org.example.hrmanagementsystem.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardDTO {
    private long totalTasks;
    private long pendingTasks;
    private long completedTasks;
    private long totalProjects;
}
