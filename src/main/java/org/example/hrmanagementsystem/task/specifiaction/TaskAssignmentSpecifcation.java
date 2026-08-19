package org.example.hrmanagementsystem.task.specifiaction;

import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.task.entity.TaskAssignment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskAssignmentSpecifcation {
    public static Specification <TaskAssignment> hasStatus(TaskStatus taskStatus){
        return (root, query, criteriaBuilder) -> {
            if(taskStatus == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("taskStatus") , taskStatus);
        };
    }

    public static Specification<TaskAssignment> assignedtoEmployee(Long employeeId) {
        return (root, query, criteriaBuilder) -> {
            if (employeeId == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("employee").get("employeeId") , employeeId);
        };
    }

    public static Specification<TaskAssignment> dueBetween(LocalDate from , LocalDate to){
        return (root, query, criteriaBuilder) -> {
            if (from == null && to == null) {
                return criteriaBuilder.conjunction();
            }
            if (from != null && to != null) {
                return criteriaBuilder.between(root.get("dueDate"), from, to);
            }
            return from != null
                    ? criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), from)
                    : criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), to);


        };
    }
}
