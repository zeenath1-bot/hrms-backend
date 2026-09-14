package org.example.hrmanagementsystem.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.admin.dto.ResetPasswordRequest;
import org.example.hrmanagementsystem.admin.dto.UpdateUserRequest;
import org.example.hrmanagementsystem.admin.dto.UserResponseDTO;
import org.example.hrmanagementsystem.admin.specification.UserSpecification;
import org.example.hrmanagementsystem.auth.entity.PasswordResetToken;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.PasswordResetTokenRepository;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.auth.service.EmailService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    private UserResponseDTO toDTO(User user) {
        String employeeName = null;
        if (user.getEmployee() != null){
            employeeName = user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName();
        }
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .employeeId(user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null)
                .employeeName(employeeName)
                .active(user.isActive())
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


    public String createPasswordResetRequest (Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.isActive()) {
            throw new BusinessException("Cannot reset password for an inactive user");
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);
        passwordResetTokenRepository.save(resetToken);
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        if(user.getEmployee() != null &&
        user.getEmployee().getEmail() != null &&
        !user.getEmployee().getEmail().isBlank()){
            emailService.sendPasswordResetEmail(user.getEmployee().getEmail() , resetLink);
        }
        return resetLink;
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
