package org.example.hrmanagementsystem.task.Repository;

import org.example.hrmanagementsystem.task.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment , Long> , JpaSpecificationExecutor<TaskAssignment> {
    List<TaskAssignment> findByEmployeeEmployeeId(Long employeeId);
}
