package org.example.hrmanagementsystem.task.Repository;

import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.task.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment , Long> , JpaSpecificationExecutor<TaskAssignment> {
    List<TaskAssignment> findByEmployeeEmployeeId(Long employeeId);
    long countByTaskStatus(TaskStatus taskStatus);

    long countByEmployeeEmployeeId(Long employeeId);
    long countByEmployeeEmployeeIdAndTaskStatus(Long employeeId , TaskStatus taskStatus);

    @Query("""
SELECT COUNT (ta)
FROM TaskAssignment ta
WHERE ta.employee.employeeId = :employeeId
AND ta.taskStatus IN (
org.example.hrmanagementsystem.enums.TaskStatus.ASSIGNED,
            org.example.hrmanagementsystem.enums.TaskStatus.IN_PROGRESS
)
""")
    long countPendingTasksByEmployeeId(
            @Param("employeeId") Long employeeId
    );

    @Query("""
    SELECT COUNT(ta)
      FROM TaskAssignment ta
            WHERE ta.task.project.manager.userId = :managerId
            AND ta.taskStatus IN (
                org.example.hrmanagementsystem.enums.TaskStatus.ASSIGNED,
                org.example.hrmanagementsystem.enums.TaskStatus.IN_PROGRESS
            )
""")
    long countPendingTasksByManagerId(
            @Param("managerId") Long managerId
    );
    @Query("""
        SELECT COUNT(ta)
        FROM TaskAssignment ta
        WHERE ta.task.project.manager.userId = :managerId
        AND ta.taskStatus =
            org.example.hrmanagementsystem.enums.TaskStatus.COMPLETED
    """)
    long countCompletedTasksByManagerId(
            @Param("managerId") Long managerId
    );
}
