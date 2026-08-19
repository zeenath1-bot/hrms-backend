package org.example.hrmanagementsystem.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.RoleType;
import org.example.hrmanagementsystem.project.model.Project;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId ;

    @Column(nullable = false , unique = true)
    private String username ;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private boolean active = true;

    @OneToOne
    @JoinColumn(name = "employeeId" , nullable = true, unique = true)
    private Employee employee;

    @OneToMany(mappedBy = "manager" , fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();


}
