package org.example.hrmanagementsystem.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.admin.dto.ResetPasswordRequest;
import org.example.hrmanagementsystem.admin.dto.UpdateUserRequest;
import org.example.hrmanagementsystem.admin.dto.UserResponseDTO;
import org.example.hrmanagementsystem.admin.specification.UserSpecification;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.PageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .employeeId(user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null)
                .build();
    }

    public List<UserResponseDTO> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UserResponseDTO getUser(Long id) {
        User user= userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDTO(user);
    }

    public UserResponseDTO updateUser(Long id , UpdateUserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
        if (userRequest.getUsername() != null && !userRequest.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userRequest.getUsername())) {
                throw new BusinessException("Username already taken: " + userRequest.getUsername());
            }
            user.setUsername(userRequest.getUsername());

        }

        if(userRequest.getActive() != null) {
            user.setActive(userRequest.getActive());
        }

     if (userRequest.getRole() != null) {
         user.setRole(userRequest.getRole());
     }
     User savedUser = userRepository.save(user);

      return toDTO(savedUser);

}
    public void resetPassword(Long id , ResetPasswordRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    public void deactivateUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }
    public void activateUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }
    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User not found with id: "+ id);
        }
        userRepository.deleteById(id);
    }

    public Page<UserResponseDTO> searchUser (String username , Boolean active, Pageable pageable) {
        Specification<User> spec = Specification
                .where(UserSpecification.nameLike(username))
                .and(UserSpecification.isActive(active));


        Page<User> userPage = userRepository.findAll(spec , pageable);
        return userPage.map(this:: toDTO);
    }

}
