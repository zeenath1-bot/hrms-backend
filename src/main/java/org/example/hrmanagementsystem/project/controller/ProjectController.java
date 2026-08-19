package org.example.hrmanagementsystem.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.service.EmployeeService;
import org.example.hrmanagementsystem.project.dto.ProjectCreateDTO;
import org.example.hrmanagementsystem.project.dto.ProjectResponseDTO;
import org.example.hrmanagementsystem.project.dto.ProjectUpdateDTO;
import org.example.hrmanagementsystem.project.service.ProjectService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> saveProject(@RequestBody @Valid ProjectCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Project created successfully" ,
                        projectService.save(dto)));
    }
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllproject(){
        return ResponseEntity.ok(new ApiResponse<>("Projects retrieved successfully" ,projectService.getAllproject()));}

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>>  getProjectById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Project retrieved successfully" ,projectService.getprojectById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject (@PathVariable Long id , @RequestBody ProjectUpdateDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Project updated successfully" ,projectService.updateProject(id , dto)));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteproject(@PathVariable Long id){
        projectService.deleteproject(id);
        return ResponseEntity.ok(new ApiResponse<>("Project deleted successfully." , null));
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> restoreProject (@PathVariable Long id){
        projectService.restoreProject(id);
        return ResponseEntity.ok(new ApiResponse<>("Project restored successfully" , null));
    }

    @PostMapping("/{projectId}/assign-employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> assignEmployee(@PathVariable Long projectId , @PathVariable Long employeeId){
        return ResponseEntity.ok(new ApiResponse<>("Employee assigned successfully" ,projectService.assignEmployeeToProject(projectId,employeeId)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<ProjectResponseDTO>>> getProjects (@RequestParam(required = false) String pname ,
                                                                 @RequestParam(required = false) Long managerId ,
                                                                 @ParameterObject @PageableDefault(page = 0 , size = 5 , sort = "projectName" , direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Projects retrieved successfully",projectService.searchProjects(pname , managerId ,pageable)));
    }

}
