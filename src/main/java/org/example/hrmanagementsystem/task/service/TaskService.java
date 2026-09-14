package org.example.hrmanagementsystem.task.service;

import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.example.hrmanagementsystem.project.model.Project;
import org.example.hrmanagementsystem.security.model.MyUserDetails;
import org.example.hrmanagementsystem.task.Repository.TaskAssignmentRepository;
import org.example.hrmanagementsystem.task.entity.TaskAssignment;
import org.example.hrmanagementsystem.task.specifiaction.TaskSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.project.Repository.ProjectRepository;
import org.example.hrmanagementsystem.task.Repository.TaskRepository;
import org.example.hrmanagementsystem.task.dto.*;
import org.example.hrmanagementsystem.task.entity.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserRepository userRepository;


    private Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTaskTitle(dto.getTaskTitle().trim());
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));
            task.setProject(project);
        }
        return task;
    }

    private TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
                .taskId(task.getTaskId())
                .taskTitle(task.getTaskTitle())
                .projectId(task.getProject() != null ? task.getProject().getProjectId() : null)
                .projectName(task.getProject() != null ? task.getProject().getProjectName() : null)
                .build(); //creates the final taskresponse object

    }

    public TaskResponseDTO save(TaskRequestDTO dto , MyUserDetails userDetails) {
        Task task = toEntity(dto);
        Task savedTask = taskRepository.save(task);
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(()-> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));
            Project project = savedTask.getProject();
            if(!project.getEmployees().contains(employee)) {
                throw new BusinessException("Employee is not a member of the selected project");
            }
            User assignedBy = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userDetails.getUserId()));

            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(savedTask);
            assignment.setEmployee(employee);
            assignment.setAssignedBy(assignedBy);
            assignment.setAssignedDate(dto.getAssignedDate() != null ? dto.getAssignedDate() : LocalDate.now());
            assignment.setDueDate(dto.getDueDate());
            taskAssignmentRepository.save(assignment);
        }

        return toDTO(savedTask);
    }

    public TaskResponseDTO getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id
                ));
        return toDTO(task);
    }


    public List<TaskResponseDTO> getAll() {

        List<Task> tasks = taskRepository.findAll();

        if(tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found");
        }
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }


    public TaskResponseDTO updatetask(Long id, TaskRequestDTO dto) {
        Task task = findTaskById(id);
        task.setTaskTitle(dto.getTaskTitle());

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));
            task.setProject(project);
        }

        taskRepository.save(task);

        return toDTO(task);

    }

    public TaskResponseDTO updatebypatch(Long id, TaskRequestDTO dto) {
        Task task = findTaskById(id);
        if (dto.getTaskTitle() != null) {
            task.setTaskTitle(dto.getTaskTitle());
        }

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));
            task.setProject(project);
        }
        taskRepository.save(task);
        return toDTO(task);
    }

    public void deletetask(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    public Page<TaskResponseDTO> searchTasks (String tname ,Long projectId,Pageable pageable){
        Specification<Task> spec = Specification
                .where(TaskSpecification.nameLike(tname))
                .and(TaskSpecification.inProject(projectId));

        Page <Task> taskPage = taskRepository.findAll(spec , pageable);
        if(taskPage.isEmpty()){
            throw new ResourceNotFoundException("No tasks found");
        }
        return taskPage.map(this::toDTO);


    }

}

