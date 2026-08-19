package org.example.hrmanagementsystem.department.controller;



import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.department.dto.DepartmentRequestDTO;
import org.example.hrmanagementsystem.department.dto.DepartmentResponseDTO;
import org.example.hrmanagementsystem.department.model.Department;
import org.example.hrmanagementsystem.department.service.DepartmentService;
import org.example.hrmanagementsystem.department.specification.DepartmentSpecification;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> saveDepartment(@RequestBody @Valid DepartmentRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Department inserted successfully" , departmentService.saveDepartment(dto)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> getDepartmentById (@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Department retrieved successfully" ,departmentService.getDepartmentById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDTO>>> getAllDepartment(){
        return ResponseEntity.ok(new ApiResponse<>("Departments retrieved successfully" ,departmentService.getAllDepartments()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> updateDepartment (@PathVariable Long id , @RequestBody @Valid DepartmentRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Department updated successfully " ,departmentService.updateDepartment(id , dto)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> patchEmployee(@PathVariable Long id , @RequestBody DepartmentRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Department updated successfully" ,departmentService.updateDepartmentbyFields(id , dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id){
        departmentService.deleteDept(id);
        return ResponseEntity.ok(new ApiResponse<>("Department deleted successfully." , null));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<Page<DepartmentResponseDTO>>> getDepartments(@RequestParam(required = false) String dname,@RequestParam (required = false) Boolean hasManager ,
                                                                @ParameterObject  @PageableDefault(page = 0 , size = 5 , sort = "deptName", direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Departments retrieved successfully" ,departmentService.searchDepartments(dname ,hasManager , pageable)));
    }




}
