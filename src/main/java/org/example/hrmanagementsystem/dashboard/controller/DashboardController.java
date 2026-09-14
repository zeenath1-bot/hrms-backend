package org.example.hrmanagementsystem.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.dashboard.dto.*;
import org.example.hrmanagementsystem.dashboard.service.DashboardService;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
private final UserRepository userRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/employee-status")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<EmployeeStatusDTO> getEmployeeStatus(){
        return ResponseEntity.ok(dashboardService.getEmployeeStatus());
    }

    @GetMapping("/department-distribution")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<List<DepartmentDistributionDTO>> getDepartmentDistribution(){
        return ResponseEntity.ok(dashboardService.getDepartmentDistribution());
    }

    @GetMapping("/recent-employees")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<List<RecentEmployeeDTO>> getRecentEmployees() {

        return ResponseEntity.ok(
                dashboardService.getRecentEmployees()
        );
    }

    @GetMapping("/recent-projects")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<RecentProjectDTO>> getRecentProjects() {

        return ResponseEntity.ok(
                dashboardService.getRecentProjects()
        );
    }
    @GetMapping("/manager")
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public ResponseEntity<ManagerDashboardDTO> getManagerDashboard(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(dashboardService.getManagerDashboard(user.getUserId())
        );
    }
    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE')")
    public ResponseEntity<EmployeeDashboardDTO> getEmployeeDashboard(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user.getEmployee() == null) {
            throw new ResourceNotFoundException(
                    "Employee profile not found for this user");}

        return ResponseEntity.ok(
                dashboardService.getEmployeeDashboard(
                        user.getEmployee().getEmployeeId()
                )
        );
    }

    private User getAuthenticatedUser(Authentication authentication) {

        return userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }




}
