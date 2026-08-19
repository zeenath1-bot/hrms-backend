package org.example.hrmanagementsystem.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.ApiResponse;
import org.example.hrmanagementsystem.admin.dto.ResetPasswordRequest;
import org.example.hrmanagementsystem.admin.dto.UpdateUserRequest;
import org.example.hrmanagementsystem.admin.dto.UserResponseDTO;
import org.example.hrmanagementsystem.admin.service.AdminUserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully" ,adminUserService.getAllUser()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser (@PathVariable Long id ){
        return ResponseEntity.ok(new ApiResponse<>("User retrieved successfully" ,adminUserService.getUser(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id , @RequestBody @Valid UpdateUserRequest userRequest) {
        return ResponseEntity.ok(new ApiResponse<>("User updated successfully" ,adminUserService.updateUser(id , userRequest)));
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id , @RequestBody  ResetPasswordRequest request){
        adminUserService.resetPassword(id , request);
        return ResponseEntity.ok(new ApiResponse<>("Password reset successfully" , null));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id){
        adminUserService.deactivateUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deactivated successfully" , null));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id){
        adminUserService.activateUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User activated successfully" , null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id){
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully" , null));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getUsers (@RequestParam(required = false) String username ,
                                                           @RequestParam(required = false) Boolean active ,
                                                           @ParameterObject @PageableDefault(page = 0 , size = 10 , sort = "username" , direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully" ,adminUserService.searchUser(username , active , pageable)));
    }

}
