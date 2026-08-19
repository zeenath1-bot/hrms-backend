package org.example.hrmanagementsystem.task.Repository;

import org.example.hrmanagementsystem.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository <Task , Long> , JpaSpecificationExecutor<Task> {
}
