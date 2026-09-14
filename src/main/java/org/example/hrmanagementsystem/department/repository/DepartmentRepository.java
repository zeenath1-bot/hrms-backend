package org.example.hrmanagementsystem.department.repository;

import org.example.hrmanagementsystem.department.model.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface DepartmentRepository  extends JpaRepository <Department, Long >, JpaSpecificationExecutor<Department> {
     

    
}
