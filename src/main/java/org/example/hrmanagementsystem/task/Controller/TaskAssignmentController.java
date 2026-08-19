package org.example.hrmanagementsystem.task.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.enums.TaskStatus;
import org.example.hrmanagementsystem.security.model.MyUserDetails;
import org.example.hrmanagementsystem.task.dto.TaskAssignmentRequestDTO;
import org.example.hrmanagementsystem.task.dto.TaskAssignmentResponseDTO;
import org.example.hrmanagementsystem.task.dto.UpdateTaskStatusDTO;
import org.example.hrmanagementsystem.task.service.TaskAssignmentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/task-assignments")
@RequiredArgsConstructor
public class TaskAssignmentController {
    private final TaskAssignmentService taskAssignmentService ;

    @PostMapping("/assign")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<TaskAssignmentResponseDTO>> assignTask(@RequestBody @Valid TaskAssignmentRequestDTO dto){
        return ResponseEntity.ok(
                new ApiResponse<>("Task assigned successfully " , taskAssignmentService.assignTask(dto)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<TaskAssignmentResponseDTO>>> getAllTasks(){
        return ResponseEntity.ok(new ApiResponse<>("Tasks retrieved successfully" ,taskAssignmentService.getAllTasks()));
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<TaskAssignmentResponseDTO>>> getMyTasks(@AuthenticationPrincipal MyUserDetails userDetails){
        return ResponseEntity.ok(new ApiResponse<>("My tasks retreived successfully" , taskAssignmentService.getMyTasks(userDetails.getEmployeeId())));
    }

    @PatchMapping("/{assignmentId}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<TaskAssignmentResponseDTO>> updateTaskStatus(@PathVariable Long assignmentId , @RequestBody @Valid UpdateTaskStatusDTO dto , @AuthenticationPrincipal MyUserDetails myUserDetails){
        return ResponseEntity.ok(new ApiResponse<>("Task status updated successfully" ,taskAssignmentService.updateTaskStatus(assignmentId , dto , myUserDetails.getEmployeeId())));

    }
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Page<TaskAssignmentResponseDTO>>> getTaskAssignments(@RequestParam(required = false)TaskStatus taskStatus ,
                                                                             @RequestParam (required = false)Long employeeId,
                                                                             @RequestParam (required = false) LocalDate dueFrom ,
                                                                              @RequestParam (required = false) LocalDate dueTo,
                                                                             @ParameterObject @PageableDefault(page = 0 , size = 5 , sort = "assignmentId" , direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Task assignments retreived successfully" ,taskAssignmentService.searchTaskAssignments(taskStatus , employeeId , dueFrom , dueTo ,pageable)));
    }

}
