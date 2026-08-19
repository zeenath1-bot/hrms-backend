package org.example.hrmanagementsystem.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.hrmanagementsystem.enums.TaskStatus;

@Getter
@Setter
public class UpdateTaskStatusDTO {

    @NotNull(message = "Enter the valid taskStatus")
    private TaskStatus taskStatus;
}
