package org.example.hrmanagementsystem.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.hrmanagementsystem.enums.ProjectStatus;

@Getter
@Setter
public class UpdateProjectStatusDTO {
    @NotNull(message = "Status is required")
    private ProjectStatus status;
}
