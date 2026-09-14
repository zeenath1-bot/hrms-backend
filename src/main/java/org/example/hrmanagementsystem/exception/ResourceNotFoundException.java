package org.example.hrmanagementsystem.exception;

import org.springframework.context.annotation.Bean;


public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
