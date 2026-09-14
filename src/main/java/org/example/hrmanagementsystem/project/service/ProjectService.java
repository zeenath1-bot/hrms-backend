package org.example.hrmanagementsystem.project.service;

import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.service.EmployeeService;
import org.example.hrmanagementsystem.enums.ProjectStatus;
import org.example.hrmanagementsystem.enums.RoleType;
import org.example.hrmanagementsystem.enums.StatusType;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.project.Repository.ProjectRepository;
import org.example.hrmanagementsystem.project.dto.*;
import org.example.hrmanagementsystem.project.model.Project;
import org.example.hrmanagementsystem.project.specification.ProjectSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;


    private Project toEntity(ProjectCreateDTO dto, User manager) {
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setManager(manager);

        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            List<Employee> employees = employeeRepository.findAllById(dto.getEmployeeIds());
            if (employees.size() != dto.getEmployeeIds().size()) {
                throw new ResourceNotFoundException("One or more employees were not founnd");}
            boolean hasInactiveEmployee = employees.stream()
                    .anyMatch(employee -> employee.getStatus() != StatusType.ACTIVE);
            if (hasInactiveEmployee) {
                throw new BusinessException("Only active employees can be assigned to projects.");}
            project.setEmployees(employees);
        }
        return project;
    }


    private ProjectResponseDTO toDTO(Project project) {

        List<Long> employeeIds = project.getEmployees().stream()
                .map(emp -> emp.getEmployeeId())
                .collect(Collectors.toList());
        List<String> employeeNames = project.getEmployees().stream()
                .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                .collect(Collectors.toList());
        return ProjectResponseDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .managerId(
                        project.getManager() != null ? project.getManager().getUserId() : null)
                .managerName(
                        project.getManager() != null ? project.getManager().getUsername() : null)
                .employeeIds(employeeIds)
                .employeeNames(employeeNames)
                .status(project.getStatus())
                .build();
    }


    public ProjectResponseDTO save(ProjectCreateDTO dto) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User manager = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!manager.getRole().equals(RoleType.MANAGER) && !manager.getRole().equals(RoleType.ADMIN)) {
            throw new BusinessException("Only Admins and Managers can create projects");
        }
        Project project = toEntity(dto, manager);
        Project savedProject = projectRepository.save(project);
        return toDTO(savedProject);
    }


    public List<ProjectResponseDTO> getAllproject() {
        return projectRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProjectResponseDTO getprojectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + id
                ));
        return toDTO(project);
    }


    public Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));
    }

    public ProjectResponseDTO updateProject(Long id, ProjectUpdateDTO dto) {
        Project project = findProjectById(id);

        if (dto.getProjectName() != null) {
            validateProjectIsActive(project);
            project.setProjectName(dto.getProjectName());
        }

        if (dto.getEmployeeIds() != null) {
            validateProjectIsActive(project);

            if (!dto.getEmployeeIds().isEmpty()) {

                List<Employee> employees = employeeRepository.findAllById(dto.getEmployeeIds());
                if (employees.size() != dto.getEmployeeIds().size()) {
                    throw new ResourceNotFoundException("One or more employees were not found");
                }
                boolean hasInactiveEmployee = employees.stream()
                        .anyMatch(employee -> employee.getStatus() != StatusType.ACTIVE);
                if (hasInactiveEmployee) {
                    throw new BusinessException(
                            "Only active employees can be assigned to projects."
                    );
                }
                for (Employee employee : employees) {
                    if (!project.getEmployees().contains(employee)) {
                        project.getEmployees().add(employee);
                    }
                }
            }
        }
        if(dto.getStatus() != null){
            validateStatusChange(project.getStatus(), dto.getStatus());
            project.setStatus(dto.getStatus());
        }
            Project updatedProject = projectRepository.save(project);
            return toDTO(updatedProject);
        }

        public ProjectResponseDTO updateProjectStatus(Long id , UpdateProjectStatusDTO dto){
        Project project = findProjectById(id);
        validateStatusChange(project.getStatus(), dto.getStatus());
        project.setStatus(dto.getStatus());
        Project updated = projectRepository.save(project);
        return toDTO(updated);
        }


        public void deleteproject(Long id){
            Project project = findProjectById(id);
            project.setDeleted(true);
            projectRepository.save(project);
        }

        public void restoreProject (Long id){
        Project project = projectRepository.findByIncludingDeleted(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found with id: "+ id));
        if(!project.isDeleted()){
            throw new BusinessException("Project is not deleted");
        }
        project.setDeleted(false);
        projectRepository.save(project);
        }



        public ProjectResponseDTO assignEmployeeToProject (Long projectId , Long employeeId){
            Project project = findProjectById(projectId);
            validateProjectIsActive(project);
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with id: " + employeeId)
                    );
            if (employee.getStatus() != StatusType.ACTIVE) {
                throw new BusinessException(
                        "Employee is already assigned to this project"
                );
            }
            if(project.getEmployees().contains(employee)){
                throw new BusinessException("Employee is already assigned to this project");
            }
            project.getEmployees().add(employee);
            Project saved = projectRepository.save(project);

            return toDTO(saved);

        }

        public Page<ProjectResponseDTO> searchProjects(String pname ,Long managerId, Pageable pageable){
            Specification<Project> spec = Specification
                    .where(ProjectSpecification.nameLike(pname))
                    .and(ProjectSpecification.hasManager(managerId));

            Page <Project> projectPage = projectRepository.findAll(spec , pageable);
            return projectPage.map(this::toDTO);
        }

        public void removeEmployeeFromProject(Long projectId, Long employeeId){
        Project project = findProjectById(projectId);
        validateProjectIsActive(project);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if(!project.getEmployees().contains(employee)) {
            throw new BusinessException("Employee is not assigned to this project");
        }
        project.getEmployees().remove(employee);
        projectRepository.save(project);
        }

        public List <ProjectEmployeeResponseDTO> getProjectEmployees (Long projectId) {
        Project project = findProjectById(projectId);
        return project.getEmployees()
                .stream()
                .map(employee -> new ProjectEmployeeResponseDTO(
                        employee.getEmployeeId(),
                        employee.getFirstName(),
                        employee.getLastName()
                ))
                .toList();
        }

        private void validateProjectIsActive (Project project){
        if(project.getStatus() != ProjectStatus.ACTIVE){
            throw new BusinessException("Only active projects can be modified.");
        }
        }

    private void validateStatusChange(ProjectStatus currentStatus, ProjectStatus newStatus){
        if(currentStatus == newStatus){
            return;
        }
        if(newStatus == ProjectStatus.ACTIVE) {
            return;
        }
        if(currentStatus != ProjectStatus.ACTIVE) {
            throw new BusinessException("Only active projects can be completed or cancelled.");
        }
    }
    }



