package org.example.hrmanagementsystem.employees.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.department.dto.DepartmentResponseDTO;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.dto.EmployeeRequestDTO;
import org.example.hrmanagementsystem.employees.dto.EmployeeResponseDTO;
import org.example.hrmanagementsystem.employees.service.EmployeeService;
import org.example.hrmanagementsystem.enums.StatusType;
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
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> saveEmployee(@RequestBody  @Valid EmployeeRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Employee inserted successfully" ,employeeService.saveEmployee(dto)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById (@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Employee retrieved successfully" ,employeeService.getEmployeeById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAllEmployees(){
        return ResponseEntity.ok(new ApiResponse<>("Employees retrieved successfully" ,employeeService.getAllemployees()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> postEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Employee updated successfully" ,employeeService.updateEmployees(id , dto)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> patchEmployee(@PathVariable Long id , @RequestBody EmployeeRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Employee updated successfully" ,employeeService.updateEmployeeFields(id , dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse<>("Employee deleted successfully." , null));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<Page<EmployeeResponseDTO>>> getEmployees (@RequestParam (required = false) String ename ,
                                                                   @RequestParam(required = false) StatusType statusType ,
                                                                   @RequestParam(required = false) Long departmentId ,
                                                                   @RequestParam(required = false) Long jobId ,
                                                                   @ParameterObject  @PageableDefault (page = 0 , size = 5 , sort = "employeeId" , direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Employees retrieved successfully " , employeeService.searchEmployees(ename ,statusType,departmentId,jobId, pageable)));
    }





}
