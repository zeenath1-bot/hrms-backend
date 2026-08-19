package org.example.hrmanagementsystem.employees.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.hrmanagementsystem.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRequestDTO {

    @NotBlank(message = "First name is required ")
    private String firstName;
    private String lastName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateofbirth;

    @NotNull(message = "Join date is required")
    private LocalDate joinDate;
    private LocalDate exitDate ;

    @NotNull(message = "Enter valid status type")
    private StatusType status;

    @NotBlank(message = "Enter valid email")
    private String email;

    private String phonenumber;


    @NotNull(message = "Enter the salary")
    private BigDecimal salary;


    @NotNull(message = "Department Id is required")
    private Long deptId;

    @NotNull(message = "Job Id is required")
    private Long jobId;


}


