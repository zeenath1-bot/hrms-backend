package org.example.hrmanagementsystem.project.dto;

import lombok.Data;
import org.example.hrmanagementsystem.enums.ProjectStatus;

import java.util.List;

@Data
public class ProjectUpdateDTO {
    private String projectName;
    private List<Long> employeeIds;
    private ProjectStatus status;
}
