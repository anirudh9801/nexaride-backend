package com.nexaride.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest{
    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$",
            message = "Password must be 8-12 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String password;
    @Pattern(
            regexp = "^\\+?[0-9]{10,12}$",
            message = "Phone number must be 10–12 digits, optionally starting with +"
    )
    @NotBlank(message = "Phone number required")
    private String phoneNumber;

}
