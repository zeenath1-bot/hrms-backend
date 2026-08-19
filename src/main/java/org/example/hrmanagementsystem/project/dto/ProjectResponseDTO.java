package org.example.hrmanagementsystem.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProjectResponseDTO {
    private Long projectId ;
    private String projectName;
    private Long managerId;
    private String managerName;
    private List<String> employeeNames;
}
