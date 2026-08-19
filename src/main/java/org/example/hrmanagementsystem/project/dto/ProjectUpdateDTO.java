package org.example.hrmanagementsystem.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectUpdateDTO {
    private String projectName;
    private List<Long> employeeIds;
}
