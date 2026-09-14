package org.example.hrmanagementsystem.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDashboardDTO {
    private long totalProjects;
    private long totalEmployees;
    private long pendingTasks;
    private long completedTasks;
}
