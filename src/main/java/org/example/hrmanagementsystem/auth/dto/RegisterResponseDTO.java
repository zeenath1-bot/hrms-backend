package org.example.hrmanagementsystem.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hrmanagementsystem.enums.StatusType;

@Getter
@AllArgsConstructor
public class RegisterResponseDTO {
    private Long userId ;
    private String userName ;
    private Boolean active;
}
