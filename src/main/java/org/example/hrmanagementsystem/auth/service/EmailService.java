package org.example.hrmanagementsystem.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    public void sendPasswordResetEmail ( String to , String resetLink){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your HRMS password");
        message.setText(
                "Hello,\n\n" +
                        "We received a request to reset your HRMS password.\n\n" +
                        "Click the link below to create a new password:\n\n" +
                        resetLink +
                        "\n\n" +
                        "This link will expire in 15 minutes and can only be used once.\n\n" +
                        "If you did not request a password reset, you can safely ignore this email.\n\n" +
                        "Regards,\n" +
                        "TRIBEX HRMS"
        );
        mailSender.send(message);
    }
}
