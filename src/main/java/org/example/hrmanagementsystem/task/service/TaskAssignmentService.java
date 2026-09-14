package org.example.hrmanagementsystem.task.service;

import lombok.RequiredArgsConstructor;

import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.RoleType;
import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.example.hrmanagementsystem.project.model.Project;
import org.example.hrmanagementsystem.security.model.MyUserDetails;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    private TaskAssignment toEntity(
            TaskAssignmentRequestDTO dto,
            Task task,
            Employee employee,
            User assignedBy
    ) {
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setEmployee(employee);

        assignment.setAssignedBy(assignedBy);
        assignment.setAssignedDate(dto.getAssignedDate());
        assignment.setDueDate(dto.getDueDate());
        return assignment;
    }

    private TaskAssignmentResponseDTO toDto(
            TaskAssignment assignment
    ) {
        Task task = assignment.getTask();
        Project project = task.getProject();
        Employee employee = assignment.getEmployee();
        TaskAssignmentResponseDTO dto = new TaskAssignmentResponseDTO();
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setTaskId(task.getTaskId());
        dto.setTaskTitle(task.getTaskTitle());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());

        if (project != null) {dto.setProjectId(project.getProjectId());
            dto.setProjectName(project.getProjectName());
        }

        dto.setAssignedDate(assignment.getAssignedDate());
        dto.setDueDate(assignment.getDueDate());
        dto.setTaskStatus(assignment.getTaskStatus());
        if (assignment.getAssignedBy() !=  null){

            dto.setAssignedById(assignment.getAssignedBy().getUserId());
            Employee assignedByEmployee =
                    assignment.getAssignedBy().getEmployee();

            if (assignedByEmployee != null) {
                dto.setAssignedBy(
                        assignedByEmployee.getFirstName()
                                + " "
                                + assignedByEmployee.getLastName()
                );
            }
        }
        return dto;
    }

    public TaskAssignmentResponseDTO assignTask(
            TaskAssignmentRequestDTO dto,
            MyUserDetails userDetails
    ) {


        if (dto.getAssignedDate() == null) {
            throw new BusinessException(
                    "Assigned date is required"
            );
        }

        if (dto.getDueDate() == null) {
            throw new BusinessException(
                    "Due date is required"
            );
        }

        if (dto.getAssignedDate().isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "Assigned date cannot be in the past"
            );
        }

        if (dto.getDueDate().isBefore(dto.getAssignedDate())) {
            throw new BusinessException(
                    "Due date cannot be before assigned date"
            );
        }


        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: "
                                        + dto.getTaskId()
                        )
                );

        Project project = task.getProject();

        if (project == null) {
            throw new BusinessException(
                    "Task is not assigned to a project"
            );
        }


        String role = userDetails.getRole();

        if (RoleType.MANAGER.name().equals(role)) {

            if (project.getManager() == null) {
                throw new BusinessException(
                        "Project does not have a manager"
                );
            }

            if (!project.getManager()
                    .getUserId()
                    .equals(userDetails.getUserId())) {

                throw new BusinessException(
                        "You can only assign tasks under your projects"
                );
            }
        } else if (!RoleType.ADMIN.name().equals(role)) {

            throw new BusinessException(
                    "You are not authorized to assign tasks"
            );
        }


        Employee employee = employeeRepository.findById(
                dto.getEmployeeId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Employee not found with id: "
                                + dto.getEmployeeId()
                )
        );


        boolean employeeBelongsToProject =
                project.getEmployees()
                        .stream()
                        .anyMatch(projectEmployee ->
                                projectEmployee
                                        .getEmployeeId()
                                        .equals(
                                                employee.getEmployeeId()
                                        )
                        );

        if (!employeeBelongsToProject) {

            throw new BusinessException(
                    "Employee is not assigned to this project"
            );
        }

        boolean alreadyAssigned =
                task.getAssignments()
                        .stream()
                        .anyMatch(existingAssignment ->
                                existingAssignment
                                        .getEmployee()
                                        .getEmployeeId()
                                        .equals(
                                                employee.getEmployeeId()
                                        )
                        );

        if (alreadyAssigned) {

            throw new BusinessException(
                    "Employee is already assigned to this task"
            );
        }
        User assignedBy = userRepository.findById(
                userDetails.getUserId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with id: "
                                + userDetails.getUserId()
                )
        );


        TaskAssignment assignment = toEntity(
                dto,
                task,
                employee,
                assignedBy
        );

        TaskAssignment saved =
                taskAssignmentRepository.save(
                        assignment
                );


        return toDto(saved);
    }
    public List<TaskAssignmentResponseDTO> getAllTasks() {

        return taskAssignmentRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TaskAssignmentResponseDTO updateTaskAssignment(Long assignmentId , TaskAssignmentRequestDTO dto , MyUserDetails userDetails){
        validateDates(dto);
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task assignment not found with id: "+ assignmentId
                ));
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(()-> new ResourceNotFoundException("Task not found with id: "+ dto.getTaskId()));
        Project project = task.getProject();
        if (project == null) {
            throw new BusinessException("Task is not assigned to a project");
        }
        String role = userDetails.getRole();
        if(RoleType.MANAGER.name().equals(role)) {
            if (project.getManager() == null) {
                throw new BusinessException("Project does not have a manager");
            }
            if (!project.getManager()
                    .getUserId()
                    .equals(userDetails.getUserId())) {
                throw new BusinessException(
                        "You can only edit task assignments under your projects"
                );
            }
        }else if (!RoleType.ADMIN.name().equals(role)) {
            throw new BusinessException("You are not authorized to edit task assignments");
        }
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(()-> new ResourceNotFoundException( "Employee not found with id: " + dto.getEmployeeId()));
        boolean employeeBelongsToProject =
                project.getEmployees()
                        .stream()
                        .anyMatch(projectEmployee ->
                                projectEmployee
                                        .getEmployeeId()
                                        .equals(employee.getEmployeeId())
                        );

        if (!employeeBelongsToProject) {
            throw new BusinessException(
                    "Employee is not assigned to this project"
            );
        }
        boolean alreadyAssigned =
                task.getAssignments()
                        .stream()
                        .anyMatch(existingAssignment ->
                                !existingAssignment
                                        .getAssignmentId()
                                        .equals(assignmentId)
                                        && existingAssignment
                                        .getEmployee()
                                        .getEmployeeId()
                                        .equals(employee.getEmployeeId()));

        if (alreadyAssigned) {
            throw new BusinessException("Employee is already assigned to this task");
        }
        assignment.setTask(task);
        assignment.setEmployee(employee);
        assignment.setAssignedDate(dto.getAssignedDate());
        assignment.setDueDate(dto.getDueDate());
        TaskAssignment updated = taskAssignmentRepository.save(assignment);
        return toDto(updated);

    }
    public TaskAssignmentResponseDTO getTaskAssignmentById(Long id) {

        TaskAssignment assignment =
                taskAssignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task assignment not found with id: " + id
                                )
                        );

        return toDto(assignment);
    }

    public List<TaskAssignmentResponseDTO> getMyTasks(
            Long employeeId
    ) {if (employeeId == null) {
        return taskAssignmentRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

        return taskAssignmentRepository
                .findByEmployeeEmployeeId(employeeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public  void deleteTaskAssignment(Long assignmentId , MyUserDetails userDetails){
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Task assignment not found with id: " + assignmentId));
        String role = userDetails.getRole();

        if (RoleType.ADMIN.name().equals(role)) {
            taskAssignmentRepository.delete(assignment);
            return;
        }
        if (RoleType.MANAGER.name().equals(role)) {
            Project project = assignment.getTask().getProject();
            if (project == null || project.getManager() == null) {
                throw new BusinessException("Task project or manager not found");}
            if (!project
                    .getManager()
                    .getUserId()
                    .equals(userDetails.getUserId())) {


            throw new BusinessException("You can only delete task assignments under your projects");
        }
        taskAssignmentRepository.delete(assignment);
        return;
    }

    throw new BusinessException(
            "You are not authorized to delete task assignments"
    );
    }

    public TaskAssignmentResponseDTO updateTaskStatus(
            Long assignmentId,
            UpdateTaskStatusDTO dto,
            MyUserDetails userDetails
    ) {

        TaskAssignment assignment =
                taskAssignmentRepository
                        .findById(assignmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task assignment not found"
                                )
                        );

        String role = userDetails.getRole();
        if (RoleType.ADMIN.name().equals(role)) {
            assignment.setTaskStatus(
                    dto.getTaskStatus());
            return toDto(
                    taskAssignmentRepository.save(
                            assignment)
            );
        }



        if (RoleType.MANAGER.name().equals(role)) {

            Project project =
                    assignment
                            .getTask()
                            .getProject();

            if (project == null ||
                    project.getManager() == null) {

                throw new BusinessException(
                        "Task project or manager not found"
                );
            }

            if (!project
                    .getManager()
                    .getUserId()
                    .equals(userDetails.getUserId())) {

                throw new BusinessException(
                        "You can only modify tasks under your projects"
                );
            }

            assignment.setTaskStatus(
                    dto.getTaskStatus()
            );

            return toDto(
                    taskAssignmentRepository.save(
                            assignment
                    )
            );
        }


        if (RoleType.EMPLOYEE.name().equals(role)) {

            if (userDetails.getEmployeeId() == null ||
                    !assignment
                            .getEmployee()
                            .getEmployeeId()
                            .equals(
                                    userDetails.getEmployeeId()
                            )) {

                throw new BusinessException(
                        "You can only update your own task"
                );
            }

            assignment.setTaskStatus(
                    dto.getTaskStatus()
            );

            return toDto(
                    taskAssignmentRepository.save(
                            assignment
                    )
            );
        }


        throw new BusinessException(
                "You are not authorized to update this task"
        );
    }

    public Page<TaskAssignmentResponseDTO> searchTaskAssignments(
            TaskStatus taskStatus,
            Long employeeId,
            LocalDate dueFrom,
            LocalDate dueTo,
            Pageable pageable
    ) {Specification<TaskAssignment> spec = Specification.where(TaskAssignmentSpecifcation.hasStatus(taskStatus)).and(TaskAssignmentSpecifcation.assignedtoEmployee(employeeId)).and(TaskAssignmentSpecifcation.dueBetween(dueFrom, dueTo));
        Page<TaskAssignment> page =
                taskAssignmentRepository.findAll(
                                spec,
                                pageable);
        return page.map(this::toDto);
    }
    private void validateDates(
            TaskAssignmentRequestDTO dto) {
        if (dto.getAssignedDate() == null) {
            throw new BusinessException(
                    "Assigned date is required");
        }
        if (dto.getDueDate() == null) {
            throw new BusinessException(
                    "Due date is required");
        }
        if (dto.getAssignedDate()
                .isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "Assigned date cannot be in the past");
        }
        if (dto.getDueDate()
                .isBefore(dto.getAssignedDate())) {
            throw new BusinessException(
                    "Due date cannot be before assigned date");
        }
    }
}