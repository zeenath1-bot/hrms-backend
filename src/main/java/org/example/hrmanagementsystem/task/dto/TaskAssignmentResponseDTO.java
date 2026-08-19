package org.example.hrmanagementsystem.task.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.hrmanagementsystem.enums.TaskStatus;

import java.time.LocalDate;

@Getter
@Setter
public class TaskAssignmentResponseDTO {
    private Long assignmentId;
    private Long taskId ;
    private String taskTitle;

    private Long employeeId;
    private String employeeName;

    private Long projectId ;
    private String projectName;

    private LocalDate assignedDate;
    private LocalDate dueDate;

    private TaskStatus taskStatus;
}
