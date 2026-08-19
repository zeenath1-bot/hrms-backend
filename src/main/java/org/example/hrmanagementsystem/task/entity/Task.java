package org.example.hrmanagementsystem.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrmanagementsystem.project.model.Project;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;

    @Column(nullable = false)
    private String taskTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

   @OneToMany(mappedBy = "task" , fetch = FetchType.LAZY)
    private List <TaskAssignment> assignments = new ArrayList<>();


}
