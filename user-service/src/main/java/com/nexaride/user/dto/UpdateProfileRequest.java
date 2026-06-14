package com.nexaride.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(
            regexp = "^\\+?[0-9]{10,12}$",
            message = "Phone number must be 10–12 digits, optionally starting with +"
    )
    private String phoneNumber;
}
