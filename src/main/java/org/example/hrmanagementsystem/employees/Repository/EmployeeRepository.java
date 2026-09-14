package org.example.hrmanagementsystem.employees.Repository;

import org.example.hrmanagementsystem.dashboard.dto.RecentEmployeeDTO;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.example.hrmanagementsystem.dashboard.dto.DepartmentDistributionDTO;

public interface EmployeeRepository extends JpaRepository <Employee , Long> , JpaSpecificationExecutor<Employee> {

     List <Employee> findByStatus (StatusType status);
long countByStatus(StatusType status);
long countByDepartmentDeptId(Long deptId);
     @Query("""
    SELECT new org.example.hrmanagementsystem.dashboard.dto.DepartmentDistributionDTO(
        e.department.deptName,
        COUNT(e)
    )
    FROM Employee e
    GROUP BY e.department.deptId, e.department.deptName
    ORDER BY COUNT(e) DESC
    """)
     List<DepartmentDistributionDTO> getDepartmentDistribution();

     @Query("""
    SELECT new org.example.hrmanagementsystem.dashboard.dto.RecentEmployeeDTO(
        e.employeeId,
        e.firstName,
        e.lastName,
        e.department.deptName,
        CAST(e.status AS string),
        e.joinDate
    )
    FROM Employee e
    ORDER BY e.joinDate DESC
    """)
     List<RecentEmployeeDTO> getRecentEmployees(
             org.springframework.data.domain.Pageable pageable
     );

     @Query("SELECT e FROM Employee e LEFT JOIN User u ON u.employee = e WHERE u.userId IS NULL")
     List<Employee> findAllWithoutUserAccount();
     }