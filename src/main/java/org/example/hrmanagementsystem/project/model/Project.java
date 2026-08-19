package org.example.hrmanagementsystem.project.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.task.entity.Task;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table (name ="project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long projectId;

    @Column(nullable = false)
    private String projectName ;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerId" , nullable = false)
    private User manager ;


    @ManyToMany
    @JoinTable(
            name ="project_employee",
            joinColumns = @JoinColumn(name= "projectId" ),
            inverseJoinColumns= @JoinColumn(name="employeeId")
    )
    private List<Employee> employees = new ArrayList<>();


    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks  = new ArrayList<>();


}
