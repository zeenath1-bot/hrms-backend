package org.example.hrmanagementsystem.employees.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrmanagementsystem.department.model.Department;
import org.example.hrmanagementsystem.enums.StatusType;
import org.example.hrmanagementsystem.project.model.Project;
import org.example.hrmanagementsystem.job.model.Job;
import org.example.hrmanagementsystem.task.entity.Task;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId ;

    @Column (nullable = false)
    private String firstName ;

    @Column(nullable = false)
    private String lastName;

    @Column (nullable = false)
    private LocalDate dateofbirth ;

    @Column(nullable = false)
    private LocalDate joinDate ;

    @Column(nullable = true)
    private LocalDate exitdate ;

    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(precision = 10 , scale = 2)
    private BigDecimal salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "departmentId", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "manager", cascade = {CascadeType.MERGE})
    private List<Department> managedDepartments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name ="jobId", nullable = false)
    private Job job;

    @ManyToMany(mappedBy = "employees")
    private List<Project> projects;




}
