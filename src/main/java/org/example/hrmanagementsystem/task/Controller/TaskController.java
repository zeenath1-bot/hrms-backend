package org.example.hrmanagementsystem.task.Controller;

import lombok.RequiredArgsConstructor;

import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.task.Repository.TaskRepository;
import org.example.hrmanagementsystem.task.dto.TaskRequestDTO;
import org.example.hrmanagementsystem.task.dto.TaskResponseDTO;
import org.example.hrmanagementsystem.task.service.TaskService;
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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> saveTask (@RequestBody TaskRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Task inserted successfully" ,taskService.save(dto)));}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTask(){
        return ResponseEntity.ok(new ApiResponse<>("Tasks retrieved successfully" , taskService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> getTaskbyid (@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Task retrieved successfully" , taskService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(@PathVariable Long id , @RequestBody TaskRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Task updated successfully" ,taskService.updatetask(id , dto)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> patchTask(@PathVariable Long id ,@RequestBody TaskRequestDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Task updated successfully" , taskService.updatebypatch(id , dto)));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteTask (@PathVariable Long id){
        taskService.deletetask(id);
        return ResponseEntity.ok(new ApiResponse<>("Task deleted successfully." , null));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN' , 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<TaskResponseDTO>>> getTasks (@RequestParam(required = false) String tname,
                                                           @RequestParam(required = false) Long projectId ,
                                                           @ParameterObject @PageableDefault(page = 0 , size = 5 , sort = "taskTitle" , direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Tasks retrieved successfully." ,taskService.searchTasks(tname , projectId ,pageable)));
    }




}
