package org.example.hrmanagementsystem.department.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Bag;

@Data
@Builder
public class DepartmentResponseDTO {
    private Long deptId;
    private String deptName;
    private Long managerId;
}
