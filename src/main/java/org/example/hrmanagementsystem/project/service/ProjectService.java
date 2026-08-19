package org.example.hrmanagementsystem.project.service;

import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.service.EmployeeService;
import org.example.hrmanagementsystem.enums.RoleType;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.project.Repository.ProjectRepository;
import org.example.hrmanagementsystem.project.dto.ProjectCreateDTO;
import org.example.hrmanagementsystem.project.dto.ProjectResponseDTO;
import org.example.hrmanagementsystem.project.dto.ProjectUpdateDTO;
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


    private Project toEntity (ProjectCreateDTO dto, User manager){
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setManager(manager);
        return project;
    }

//
    private ProjectResponseDTO toDTO(Project project) {
        List<String> names = project.getEmployees().stream()
                .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                .collect(Collectors.toList());

        return ProjectResponseDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .managerId(project.getManager() != null ? project.getManager().getUserId() : null)
                .managerName(project.getManager() != null ? project.getManager().getUsername() : null)
                .employeeNames(names)
                .build();
    }



        public ProjectResponseDTO save(ProjectCreateDTO dto) {

            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            User manager = userRepository.findByUsername(username)
                    .orElseThrow(()-> new ResourceNotFoundException("User not found"));


            if(!manager.getRole().equals(RoleType.MANAGER)){
                throw new BusinessException("Only Managers can create projects");
            }
            Project project = toEntity(dto , manager);

            Project savedProject = projectRepository.save(project);
            return toDTO(savedProject);
        }




        public List<ProjectResponseDTO> getAllproject(){
            return projectRepository.findAll()
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        public ProjectResponseDTO getprojectById(Long id){
             Project project = projectRepository.findById(id)
                     .orElseThrow(()-> new ResourceNotFoundException(
                             "Project not found with id: " + id
                     ));
             return toDTO(project);
        }



        public Project findProjectById(Long id){
            return projectRepository.findById(id)
                    .orElseThrow(()->
                            new ResourceNotFoundException("Project not found."));
        }

        public ProjectResponseDTO updateProject(Long id , ProjectUpdateDTO dto){
            Project project = findProjectById(id);

            if(dto.getProjectName() != null){
                project.setProjectName(dto.getProjectName());
            }
            if(dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty() ) {
                List<Employee> employeesToAdd = new ArrayList<>();

                for (Long employeeId : dto.getEmployeeIds()) {
                    Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Employee not found with id: " + employeeId
                            ));
                    if (project.getEmployees().contains(employee)) {
                        throw new BusinessException(
                                "Employee " + employeeId + " already assigned to this project"
                        );
                    }
                    employeesToAdd.add(employee);
                }
                project.getEmployees().addAll(employeesToAdd);
            }

            Project updatedProject = projectRepository.save(project);
            return toDTO(updatedProject);
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
            Employee employee = employeeService.findEmployeeById(employeeId);

            if(project.getEmployees().contains(employee)){
                throw new BusinessException("Employee already assigned to this project");
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
    }


