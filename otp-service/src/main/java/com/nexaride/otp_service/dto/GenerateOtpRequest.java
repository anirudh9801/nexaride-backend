package com.nexaride.otp_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/*Client → will send email to  OTP generation
Validation ensure the correct input*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateOtpRequest {
    @NotBlank(message = "Invalid email format")
    @Email(message = "Invalid email format")
    private String email;
}
