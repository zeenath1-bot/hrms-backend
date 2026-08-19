package org.example.hrmanagementsystem.department.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrmanagementsystem.employees.model.Employee;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table  (name = "departments")
@Getter
@Setter
@NoArgsConstructor

public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(length = 2)
    private Long deptId;

    @Column(nullable = false, unique = true)
    private String deptName;



    //department cannot be deleted if employees exist
    @OneToMany(mappedBy = "department" , cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Employee> employees = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerId" , nullable = true)
    private Employee manager;


}



