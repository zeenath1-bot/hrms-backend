package org.example.hrmanagementsystem.task.dto;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.project.model.Project;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TaskResponseDTO {
    private Long taskId;
    private String taskTitle;


    private Long projectId;
    private String projectName;




}
