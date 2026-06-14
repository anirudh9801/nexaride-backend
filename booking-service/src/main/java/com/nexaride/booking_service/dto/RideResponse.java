package com.nexaride.booking_service.dto;

import com.nexaride.booking_service.entity.RideStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideResponse {
    private Long rideId;
    private String sourceAddress;
    private String destinationAddress;
    private String otp;
    private RideStatus status;
    private LocalDateTime createdAt;
}
