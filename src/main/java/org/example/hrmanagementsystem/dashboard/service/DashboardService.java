package org.example.hrmanagementsystem.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.dashboard.dto.*;
import org.example.hrmanagementsystem.department.repository.DepartmentRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.enums.StatusType;
import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.project.Repository.ProjectRepository;
import org.example.hrmanagementsystem.task.Repository.TaskAssignmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public DashboardStatsDTO getDashboardStats(){
        long totalEmployees = employeeRepository.count();
        long totalDepartments = departmentRepository.count();
        long totalProjects = projectRepository.count();
        long assignedTasks = taskAssignmentRepository.countByTaskStatus(TaskStatus.ASSIGNED);
        long inProgressTasks = taskAssignmentRepository.countByTaskStatus(TaskStatus.IN_PROGRESS);
        long pendingTasks =
                assignedTasks + inProgressTasks;

        return new DashboardStatsDTO(
                totalEmployees, totalDepartments, totalProjects, pendingTasks
        );
    }

    public EmployeeStatusDTO getEmployeeStatus() {
        long active = employeeRepository.countByStatus(StatusType.ACTIVE);
        long resigned = employeeRepository.countByStatus(StatusType.RESIGNED);
        long terminated = employeeRepository.countByStatus(StatusType.TERMINATED);
        long retired = employeeRepository.countByStatus(StatusType.RETIRED);

        return new EmployeeStatusDTO(active , resigned, terminated , retired);
    }

    public List<DepartmentDistributionDTO> getDepartmentDistribution() {
        return employeeRepository.getDepartmentDistribution();
    }

    public List<RecentEmployeeDTO> getRecentEmployees() {
        return employeeRepository.getRecentEmployees(
                PageRequest.of(0, 5)
        );
    }
    public List<RecentProjectDTO> getRecentProjects() {
        return projectRepository
                .findTop5ByOrderByCreatedDateDesc()
                .stream()
                .map(project -> new RecentProjectDTO(
                        project.getProjectId(),
                        project.getProjectName(),
                        project.getCreatedDate(),
                        project.getEmployees().size()
                ))
                .toList();
    }

    public ManagerDashboardDTO getManagerDashboard(Long managerId) {
        long totalProjects = projectRepository.countByManagerId(managerId);
        long totalEmployees = projectRepository.countEmployeesByManagerId(managerId);
        long pendingTasks = taskAssignmentRepository.countPendingTasksByManagerId(managerId);
        long completedTasks = taskAssignmentRepository.countCompletedTasksByManagerId(managerId);
        return new ManagerDashboardDTO(totalProjects , totalEmployees , pendingTasks , completedTasks);
    }

    public EmployeeDashboardDTO getEmployeeDashboard(Long employeeId){
        long totalTasks = taskAssignmentRepository.countByEmployeeEmployeeId(employeeId);
        long pendingTasks = taskAssignmentRepository.countPendingTasksByEmployeeId(employeeId);
        long completedTasks = taskAssignmentRepository.countByEmployeeEmployeeIdAndTaskStatus(employeeId , TaskStatus.COMPLETED);
        long totalProjects = projectRepository.countProjectsByEmployeesId(employeeId);
        return new EmployeeDashboardDTO(totalTasks , pendingTasks , completedTasks , totalProjects);
    }
}
