package org.example.hrmanagementsystem.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobRequestDTO {

    @NotBlank(message = "Enter valid job role ")
    private String jobTitle;
    private String jobCode;
    @NotNull(message = "Minimum salary is required ")
    private BigDecimal minSalary;

    @NotNull(message = "Maximum salary is required ")
    private BigDecimal maxSalary;
}
