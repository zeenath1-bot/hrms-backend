package org.example.hrmanagementsystem.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.TaskStatus;

import java.time.LocalDate;

@Entity
@Table(name = "taskAssignment")
@Getter
@Setter
@NoArgsConstructor
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId" , nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId" , nullable = false)
    private Employee employee;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus taskStatus = TaskStatus.ASSIGNED;

    @Column(nullable = false)
    private LocalDate assignedDate;

    @Column(nullable = false)
    private LocalDate dueDate;
}
