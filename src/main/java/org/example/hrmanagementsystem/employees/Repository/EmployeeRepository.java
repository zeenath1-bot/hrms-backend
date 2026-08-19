package org.example.hrmanagementsystem.employees.Repository;

import org.example.hrmanagementsystem.employees.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository <Employee , Long> , JpaSpecificationExecutor<Employee> {

     }