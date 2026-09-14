package org.example.hrmanagementsystem.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalEmployees;
    private long totalDepartments;
    private  long totalProjects;
    private long pendingTasks;
}
