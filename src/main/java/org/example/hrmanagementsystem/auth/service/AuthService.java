package org.example.hrmanagementsystem.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.dto.LoginRequestDTO;
import org.example.hrmanagementsystem.auth.dto.LoginResponseDTO;
import org.example.hrmanagementsystem.auth.dto.RegisterRequestDTO;
import org.example.hrmanagementsystem.auth.dto.RegisterResponseDTO;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import org.example.hrmanagementsystem.security.service.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


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
        user.setPassword(passwordEncoder.encode(dto.getPassword())); //password encod
        user.setRole(dto.getRole());
        user.setActive(true);
        user.setEmployee(employee);

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
                user.getRole().name()
        );
    }
}
