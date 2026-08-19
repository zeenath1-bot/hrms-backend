package org.example.hrmanagementsystem.job.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class JobResponseDTO {
    private Long jobId;
    private String jobTitle;
    private String jobCode;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
