package com.nexaride.booking_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateOtpRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email")
    private String email;
}
