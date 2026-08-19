package org.example.hrmanagementsystem.job.controller;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.employees.controller.EmployeeController;
import org.example.hrmanagementsystem.employees.dto.EmployeeResponseDTO;
import org.example.hrmanagementsystem.job.dto.JobRequestDTO;
import org.example.hrmanagementsystem.job.dto.JobResponseDTO;
import org.example.hrmanagementsystem.job.service.JobService;
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
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")

    public ResponseEntity<ApiResponse<JobResponseDTO>> saveJob(@RequestBody JobRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Inserted new job role" , jobService.saveJob(dto)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<JobResponseDTO>> getJobById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Job role is retrieved successfully" ,jobService.getJobById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<List<JobResponseDTO>>> getAllJob(){
        return ResponseEntity.ok(new ApiResponse<>("Job roles retrieved successfully" ,jobService.getAllJob()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<JobResponseDTO>> updateJob (@PathVariable Long id , @RequestBody JobRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Job role updated successfully" ,jobService.updatejob(id , dto)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<JobResponseDTO>> patchJob (@PathVariable Long id, @RequestBody JobRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Job role updated successfully" ,jobService.updateJobByfields(id , dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<Void>> deleteJob (@PathVariable Long id){
        jobService.deleteJob(id);
        return ResponseEntity.ok(new ApiResponse<>("Job role deleted successfully." , null));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'HR')")
    public ResponseEntity<ApiResponse<Page<JobResponseDTO>>> getJob(@RequestParam (required = false) String jname ,
                                                       @ParameterObject  @PageableDefault(page = 0 , size = 5 , sort = "jobTitle" , direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Job roles retrieved successfully" ,jobService.searchJob(jname , pageable)));
    }

}
