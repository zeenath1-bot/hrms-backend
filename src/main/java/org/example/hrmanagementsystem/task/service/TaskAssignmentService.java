package org.example.hrmanagementsystem.task.service;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.StatusType;
import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.example.hrmanagementsystem.task.Repository.TaskAssignmentRepository;
import org.example.hrmanagementsystem.task.Repository.TaskRepository;
import org.example.hrmanagementsystem.task.dto.TaskAssignmentRequestDTO;
import org.example.hrmanagementsystem.task.dto.TaskAssignmentResponseDTO;
import org.example.hrmanagementsystem.task.dto.UpdateTaskStatusDTO;
import org.example.hrmanagementsystem.task.entity.Task;
import org.example.hrmanagementsystem.task.entity.TaskAssignment;
import org.example.hrmanagementsystem.task.specifiaction.TaskAssignmentSpecifcation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.hrmanagementsystem.security.model.MyUserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Stack;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    private TaskAssignment toEntity(TaskAssignmentRequestDTO dto , Task task , Employee employee){
        TaskAssignment assignment = new TaskAssignment();

        assignment.setTask(task);
        assignment.setEmployee(employee);
        assignment.setAssignedDate(dto.getAssignedDate());
        assignment.setDueDate(dto.getDueDate());

        return assignment;
    }

    private TaskAssignmentResponseDTO toDto(TaskAssignment assignment){
        TaskAssignmentResponseDTO dto = new TaskAssignmentResponseDTO();
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setTaskId(assignment.getTask().getTaskId());
        dto.setTaskTitle(assignment.getTask().getTaskTitle());
        dto.setEmployeeId(assignment.getEmployee().getEmployeeId());
        dto.setEmployeeName(assignment.getEmployee().getFirstName());
        dto.setProjectId(assignment.getTask().getProject().getProjectId());
        dto.setProjectName(assignment.getTask().getProject().getProjectName());
        dto.setAssignedDate(assignment.getAssignedDate());
        dto.setDueDate(assignment.getDueDate());
        dto.setTaskStatus(assignment.getTaskStatus());

        return dto;
    }



    public TaskAssignmentResponseDTO assignTask(TaskAssignmentRequestDTO dto){

        if(dto.getAssignedDate() == null){
            throw new IllegalArgumentException("Assigned data is required");
        }
        if (dto.getDueDate() == null){
            throw new IllegalArgumentException("Due date is required");
        }
        if(dto.getAssignedDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Assigned date cannot be in the past");
        }
        if(dto.getDueDate().isBefore(dto.getAssignedDate())){
            throw new IllegalArgumentException("Due date cannot be before assigned date");
        }
        Task task = taskRepository.findById(
                dto.getTaskId())
                .orElseThrow(()-> new ResourceNotFoundException("Task not found"));

        Employee employee = employeeRepository.findById(
                dto.getEmployeeId())
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found"));

        Long projectId = task.getProject().getProjectId();

        boolean isAssigned = employee.getProjects()
                .stream()
                .anyMatch(p-> p.getProjectId().equals(projectId));
        if(!isAssigned){
            throw new BusinessException("Employee is not assigned to this project");
        }

        TaskAssignment assignment = toEntity(dto,task , employee);
        TaskAssignment saved = taskAssignmentRepository.save(assignment);
        return toDto(saved);

    }

    public List <TaskAssignmentResponseDTO> getAllTasks(){
        return taskAssignmentRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<TaskAssignmentResponseDTO> getMyTasks(Long employeeId) {
        List<TaskAssignment> tasks = taskAssignmentRepository.findByEmployeeEmployeeId(employeeId);

        if(tasks.isEmpty()) {
            throw new ResourceNotFoundException("No task assigned to you");
        }
        return tasks
                .stream()
                .map(this::toDto)
                .toList();
    }


    public TaskAssignmentResponseDTO updateTaskStatus(Long assignmentId , UpdateTaskStatusDTO dto , Long employeeId){

        TaskAssignment assignment = taskAssignmentRepository
                .findById(assignmentId)
                .orElseThrow(()->new ResourceNotFoundException("Task not found"));

        if(!assignment.getEmployee().getEmployeeId().equals(employeeId)){
            throw new BusinessException("You can only update your own task");
        }

        assignment.setTaskStatus(dto.getTaskStatus());


        return toDto(taskAssignmentRepository.save(assignment));
    }

    public Page<TaskAssignmentResponseDTO> searchTaskAssignments(TaskStatus taskStatus, Long employeeId , LocalDate dueFrom , LocalDate dueTo , Pageable pageable){
        Specification<TaskAssignment> spec = Specification
                .where(TaskAssignmentSpecifcation.hasStatus(taskStatus))
                .and(TaskAssignmentSpecifcation.assignedtoEmployee(employeeId))
                .and(TaskAssignmentSpecifcation.dueBetween(dueFrom , dueTo));

        Page<TaskAssignment> taskAssignmentPage = taskAssignmentRepository.findAll(spec , pageable);
        return taskAssignmentPage.map(this::toDto);

    }
}
