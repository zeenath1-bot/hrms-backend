package org.example.hrmanagementsystem.task.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskAssignmentRequestDTO {

    @NotNull (message = "Enter valid taskId")
    private Long taskId ;

    @NotNull(message = "Enter valid employeeId")
    private Long employeeId ;
    private LocalDate assignedDate;
    private LocalDate dueDate;
}
