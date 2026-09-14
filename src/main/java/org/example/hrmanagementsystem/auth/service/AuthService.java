package org.example.hrmanagementsystem.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.dto.*;
import org.example.hrmanagementsystem.auth.entity.PasswordResetToken;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.PasswordResetTokenRepository;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.example.hrmanagementsystem.security.model.MyUserDetails;
import org.example.hrmanagementsystem.security.service.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.hrmanagementsystem.auth.dto.ResetPasswordRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;



    public RegisterResponseDTO register(RegisterRequestDTO dto){
        if(userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new BusinessException("Username already exists");
        }
        Employee employee = null;
        if(dto.getEmployeeId() != null) {
            employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee ID not found"));
            if (userRepository.existsByEmployee_EmployeeId(dto.getEmployeeId())) {
                throw new BusinessException("Employee already has login account.");
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setActive(true);
        user.setEmployee(employee);
        user.setMustChangePassword(true);

        User savedUser = userRepository.save(user);

        return new RegisterResponseDTO(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.isActive()
        ) ;
    }

    public LoginResponseDTO login(LoginRequestDTO dto){
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!user.isActive()){
            throw new DisabledException("Account is deactivated");
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );
        return new LoginResponseDTO(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.isMustChangePassword()
        );
    }

    public CurrentUserResponseDTO getCurrentUser(String username) {
        MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(username);
        User user = userDetails.getUser();
        Employee employee = user.getEmployee();
        return new CurrentUserResponseDTO(
                user.getUsername(),
                user.getRole().name(),
                user.isActive(),
                user.isMustChangePassword(),
                employee != null ? employee.getEmployeeId(): null,
                employee != null ? employee.getFirstName() : null,
                employee != null ? employee.getLastName() : null,
                employee != null ? employee.getEmail() : null,
                employee != null ? employee.getPhoneNumber() : null,
                employee != null && employee.getDepartment() != null
                ?employee.getDepartment().getDeptName() : null,
                employee != null && employee.getJob() != null
                ? employee.getJob().getJobTitle() : null,
                employee != null ? employee.getJoinDate() : null
        );
    }

    public void resetPassword (ResetPasswordRequest request){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token"));

        if(resetToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {
            throw new BusinessException("Reset token has expired");
        }
        User user = resetToken.getUser();
        if(!user.isActive()) {
            throw new BusinessException("Account is deactivated");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public void forgotPassword (ForgotPasswordRequest request) {
        String username = request.getUsername().trim();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new BusinessException("User not found"));
        if(!user.isActive()){
            throw new BusinessException("Account is deactivated");
        }
        if (user.getEmployee() == null ||
        user.getEmployee().getEmail() == null ||
        user.getEmployee().getEmail().isBlank()){
            throw new BusinessException("No email address is associated with this account");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(
                user.getEmployee().getEmail(), resetLink
        );
    }

    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

}
