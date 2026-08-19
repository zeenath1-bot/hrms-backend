package org.example.hrmanagementsystem.task.dto;
import jakarta.validation.constraints.NotNull;  // correct import
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.project.model.Project;
import org.example.hrmanagementsystem.task.Controller.TaskController;

import java.time.LocalDate;

@Data
public class TaskRequestDTO {

    @NotBlank(message = "Enter the taskTitle")
    private String taskTitle;

    @NotNull(message = "Enter the valid projectId")
    private Long projectId;
}
