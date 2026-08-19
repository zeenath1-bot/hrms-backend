package org.example.hrmanagementsystem.employees.dto;

import lombok.Builder;
import lombok.Data;
import org.example.hrmanagementsystem.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder
@Data
public class EmployeeResponseDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private LocalDate dateofbirth;
    private LocalDate joinDate;
    private LocalDate exitDate ;
    private StatusType status;
    private String email;
    private String phonenumber;
    private BigDecimal salary;
    private Long deptId;
    private Long jobId;
}

