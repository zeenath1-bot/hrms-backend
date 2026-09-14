package org.example.hrmanagementsystem.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class DepartmentRequestDTO {

    @NotBlank(message = "Enter valid department name")
    private String deptName;

    @NotNull(message = "Enter valid manager Id")
    private Long managerId;
}
