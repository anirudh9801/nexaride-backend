package com.nexaride.booking_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponse {
    private String email;
    private String otp;
    private String message;

}
