package org.example.hrmanagementsystem.job.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrmanagementsystem.employees.model.Employee;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Job_des")
@Getter
@Setter
@NoArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column (nullable = false , unique = true)
    private String jobTitle;

    @Column
    private String jobCode;

    @Column
    private BigDecimal minSalary;

    @Column
    private BigDecimal maxSalary;

   //job cannot be deleted if assigned to someone
   @OneToMany(mappedBy = "job")
   private List<Employee> employees = new ArrayList<>();


}
