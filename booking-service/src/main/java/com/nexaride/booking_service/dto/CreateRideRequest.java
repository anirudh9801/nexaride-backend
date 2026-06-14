package com.nexaride.booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRideRequest {
    @NotBlank(message = "Source address is required")
    private String sourceAddress;
    @NotBlank(message = "Destination address is required")
    private String destinationAddress;
}
